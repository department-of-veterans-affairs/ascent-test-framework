package gov.va.ascent.test.framework.selenium;

import java.io.File;
import java.net.MalformedURLException;
import org.apache.commons.lang3.StringUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

import gov.va.ascent.test.framework.service.RESTConfigService;
import gov.va.ascent.test.framework.service.VaultService;
import gov.va.ascent.test.framework.util.AppConstants;
import gov.va.ascent.test.framework.util.RESTUtil;

/**
 * It's a base class for all selenium web page implementation class that contains reusable functionality 
 * such as configuring webdriver, setting up page objects and SSL configurations.
 *
 */
public class BasePage {
	protected static WebDriver selenium;
	private static final String BROWSER_NAME = System.getProperty("browser");
	private static final String WEBDRIVER_PATH = System.getProperty("webdriverPath");
	private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);

	public BasePage(WebDriver selenium) {
		BasePage.selenium = selenium;
	}

	public void initialize(Object o) {
		PageFactory.initElements(selenium, o);
	}
    /**
     * Configures webdriver capabilities for chrome and HTML unit driver.
     * @return
     */
	public static synchronized WebDriver getDriver() {

		try {
			// Chrome
			DesiredCapabilities dcChrome = DesiredCapabilities.chrome();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("start-maximized");
			dcChrome.setJavascriptEnabled(true);
			dcChrome.setCapability(CapabilityType.BROWSER_NAME, "Chrome");
			dcChrome.setCapability(ChromeOptions.CAPABILITY, options);
			dcChrome.setCapability("ignoreProtectedModeSettings", true);
			dcChrome.setCapability("acceptInsecureCerts", true);
			dcChrome.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

			if (selenium == null) {
				if (BROWSER_NAME == null || "HtmlUnit".equalsIgnoreCase(BROWSER_NAME)) {
					DesiredCapabilities dcHtml = DesiredCapabilities.htmlUnit();
					dcHtml.setCapability("ignoreProtectedModeSettings", true);
					dcHtml.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
					dcHtml.setCapability("acceptInsecureCerts", true);
					dcHtml.setCapability("handlesAlerts", true);
					dcHtml.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, true);

					selenium = getHtmlUnitDriver(dcHtml);
					dcHtml.setJavascriptEnabled(true);
					((HtmlUnitDriver) selenium).setJavascriptEnabled(true);
					selenium.manage().window().maximize();
				} else if ("CHROME".equalsIgnoreCase(BROWSER_NAME)) {
					System.setProperty("webdriver.chrome.driver", WEBDRIVER_PATH);
					selenium = new ChromeDriver(dcChrome);
					selenium.manage().window().maximize();
				}
			}
			LOGGER.debug("Driver already initialized");
		} catch (Exception e) {
			LOGGER.error("ERROR", "Could not launch the WebDriver selenium", e);
		}
		return selenium;

	}
    
	private static HtmlUnitDriver getHtmlUnitDriver(DesiredCapabilities dcHtml) {
		String pathToKeyStore = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStore", true);
		String password = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStorePassword", true);
		if (StringUtils.isBlank(pathToKeyStore)) {
			return new HtmlUnitDriver(dcHtml);
		} else {
			return new HtmlUnitDriver(dcHtml) {
				@Override
				protected WebClient modifyWebClient(WebClient client) {
					try {
						File certificateFile = new File(pathToKeyStore);
						client.getOptions().setSSLClientCertificate(certificateFile.toURI().toURL(), password, "jks");
					} catch (MalformedURLException e) {
						LOGGER.error("Unable to load JKS");
						return null;
					}
					final String vaultToken = System.getProperty(AppConstants.VAULT_TOKEN_PARAM_NAME);
					if (!StringUtils.isBlank(vaultToken)) {
						final String jsonResponse = VaultService.getVaultCredentials(vaultToken);
						final RESTUtil restUtil = new RESTUtil();
						String userName = restUtil.parseJSON(jsonResponse, "data.'username'");
						String password = restUtil.parseJSON(jsonResponse, "data.'password'");
						DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
						provider.addCredentials(userName, password);
						client.setCredentialsProvider(provider);
					}
					return client;
				}

			};
		}
	}

    /**
     * Wait method used to sync for different objects
     * @param waitMilliSeconds
     * @return
     */
	public static synchronized WebDriverWait getWebDriverWait(int waitMilliSeconds) {
		return new WebDriverWait(selenium, waitMilliSeconds);
	}
    /**
     * Delete cookies and close browser.
     */
	public static void closeBrowser() {
		selenium.manage().deleteAllCookies();
		selenium.quit();
	}

}
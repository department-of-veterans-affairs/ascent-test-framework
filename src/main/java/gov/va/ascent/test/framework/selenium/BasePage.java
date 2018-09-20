package gov.va.ascent.test.framework.selenium;

import java.io.File;

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

import com.gargoylesoftware.htmlunit.WebClient;

import gov.va.ascent.test.framework.service.RESTConfigService;

public class BasePage {
	protected static WebDriver selenium;
	private static final String BROWSER_NAME = System.getProperty("browser");
	private static final String WEBDRIVER_PATH = System.getProperty("webdriverPath");
	private static final Logger log = LoggerFactory.getLogger(BasePage.class);
	private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);

	public BasePage(WebDriver selenium) {
		BasePage.selenium = selenium;
	}

	public void initialize(Object o) {
		PageFactory.initElements(selenium, o);
	}

	public static synchronized WebDriver getDriver()  {

		try {
			// Chrome
			DesiredCapabilities dcChrome = DesiredCapabilities.chrome();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("start-maximized");
			dcChrome.setJavascriptEnabled(true);
			dcChrome.setCapability(CapabilityType.BROWSER_NAME, "Chrome");
			dcChrome.setCapability(ChromeOptions.CAPABILITY, options);
			dcChrome.setCapability("ignoreProtectedModeSettings", true);
			dcChrome.setCapability("chrome.ensureCleanSession", true);

			if (BROWSER_NAME == null || "HtmlUnit".equalsIgnoreCase(BROWSER_NAME)) {
				if (selenium == null) {
					DesiredCapabilities dcHtml = DesiredCapabilities.htmlUnit();
					dcHtml.setCapability("ignoreProtectedModeSettings", true);
					dcHtml.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
					dcHtml.setCapability("acceptInsecureCerts", true);
					dcHtml.setCapability("handlesAlerts", true);
					dcHtml.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, true);
					
					selenium = new HtmlUnitDriver(dcHtml) {
						@Override
						protected WebClient modifyWebClient(WebClient client) {
							try {
                            String pathToKeyStore = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStore", true);
                            String password = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStorePassword", true);
                            File certificateFile =new File(pathToKeyStore);
                            client.getOptions().setSSLClientCertificate(certificateFile.toURI().toURL(), password, "jks");
							}
							catch(Exception e) {
								LOGGER.error("Unable to load JKS");
							}
                            return client;
						}
					};
					dcHtml.setJavascriptEnabled(true);
					((HtmlUnitDriver)selenium).setJavascriptEnabled(true);
					selenium.manage().window().maximize();
			
				}
			else if ("CHROME".equalsIgnoreCase(BROWSER_NAME)) {
				System.setProperty("webdriver.chrome.driver", WEBDRIVER_PATH);
				if (selenium == null) {
					selenium = new ChromeDriver(dcChrome);
					selenium.manage().window().maximize();

				}
			}


		}
		}
		catch (Exception e) {
			log.error("ERROR", "Could not launch the WebDriver selenium", e);
		}
		return selenium;

	}

	// Wait method used to sync for different objects
	public static synchronized WebDriverWait getWebDriverWait(int waitMilliSeconds) {
		return new WebDriverWait(selenium, waitMilliSeconds);
	}

	public static void closeBrowser() {
		selenium.manage().deleteAllCookies();
		selenium.quit();
	}

}
package gov.va.ascent.selenium.common;

import java.io.IOException;

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

import gov.va.ascent.selenium.utils.PropertiesLoader;

public class BasePage {
	protected static WebDriver selenium;
	static final String nameOfBrowser = System.getProperty("browser");
	static final String webdriverPath = System.getProperty("webdriverPath");
	final Logger log = LoggerFactory.getLogger(BasePage.class);
	
	

	public BasePage(WebDriver selenium) {
		BasePage.selenium = selenium;

	}

	public void initialize(Object o) {

		PageFactory.initElements(selenium, o);

	}

	public synchronized static WebDriver getDriver() throws IOException {

		try {
			PropertiesLoader PropertiesLoader = new PropertiesLoader();

			// Chrome
			DesiredCapabilities dcChrome = DesiredCapabilities.chrome();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("start-maximized");
			dcChrome.setJavascriptEnabled(true);
			dcChrome.setCapability(CapabilityType.BROWSER_NAME, "Chrome");
			dcChrome.setCapability(ChromeOptions.CAPABILITY, options);
			dcChrome.setCapability("ignoreProtectedModeSettings", true);
			dcChrome.setCapability("chrome.ensureCleanSession", true);

			System.out.println("nameOfBrowser = " + nameOfBrowser);

			String proj = BasePage.class.getClassLoader().getResource("").getPath();
			String[] arr = proj.split("target");
			if (nameOfBrowser == null || nameOfBrowser.equalsIgnoreCase("HtmlUnit")) {
				if (selenium == null) {
					selenium = new HtmlUnitDriver(true);
					selenium.manage().window().maximize();
				}

			}
			else if (nameOfBrowser.equalsIgnoreCase("CHROME")) {
				// String path = (String)
				// PropertiesLoader.loadBaseProperties().getProperty("ascent.chrome.driver");
				// String finalpath = arr[0].trim() + path;
				System.setProperty("webdriver.chrome.driver", webdriverPath);
				if (selenium == null) {
					selenium = new ChromeDriver(dcChrome);
					selenium.manage().window().maximize();

				}
			}


		} catch (Exception e) {
			e.printStackTrace();
			//log.info("ERROR", "Could not launch the WebDriver selenium", e);
		}
		return selenium;

	}

	// Wait method used to sync for different objects
	public static synchronized WebDriverWait getWebDriverWait(int waitMilliSeconds) {
		WebDriverWait wait = new WebDriverWait(selenium, waitMilliSeconds);
		return wait;
	}

	public static void closeBrowser() {
		selenium.manage().deleteAllCookies();
		selenium.quit();
	}

}
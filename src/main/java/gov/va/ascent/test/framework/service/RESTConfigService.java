package gov.va.ascent.test.framework.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTConfigService {

	private static RESTConfigService instance = null;
	private Properties prop = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTConfigService.class);

	private RESTConfigService() {

	}

	public static RESTConfigService getInstance() {
		if (instance == null) {
			instance = new RESTConfigService();
			String environment = System.getProperty("test.env");
			String url = "";
			if (StringUtils.isNotBlank(environment)) {
				url = "config/vetservices-" + environment + ".properties";
			} else {
				url = "config/vetservices.properties";
			}
			URL urlConfigFile = RESTConfigService.class.getClassLoader().getResource(url);
			try (InputStream input = new FileInputStream(new File(urlConfigFile.toURI()))) {
				Properties properties = new Properties();
				properties.load(input);
				instance.prop = properties;

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (URISyntaxException uriex) {
				LOGGER.error(uriex.getMessage(), uriex);
			}

		}

		return instance;
	}

	public String getPropertyName(String pName) {
		return getPropertyName(pName, false);
	}

	public String getPropertyName(String pName, boolean isCheckSystemProp) {
		String value = "";
		if (isCheckSystemProp) {
			value = System.getProperty(pName);
			if (StringUtils.isBlank(value)) {
				value = prop.getProperty(pName);
			}
		} else {
			value = prop.getProperty(pName);
		}
		return value;
	}

}

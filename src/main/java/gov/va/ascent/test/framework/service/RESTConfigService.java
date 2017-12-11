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
	
	public static RESTConfigService getInstance()  {
		if (instance == null) {
			instance = new RESTConfigService();
			String environment = System.getProperty("test.env");
			String url = "";
			if (StringUtils.isNotBlank(environment)) {
				url = "config/vetsapi-" + environment + ".properties";
			} else {
				url = "config/vetsapi.properties";
			}
			URL urlConfigFile = RESTConfigService.class.getClassLoader().getResource(url);
			try (InputStream input = new FileInputStream(new File(urlConfigFile.toURI()))) {
				Properties properties = new Properties();
				properties.load(input);
				instance.prop = properties;
				
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}	
			catch(URISyntaxException uriex) {
				LOGGER.error(uriex.getMessage(), uriex);
			}
			
		}
		
		return instance;
	}
	
	public String getPropertyName(String pName) {
		String value = prop.getProperty(pName);
		if (null == value) {
			value = "";
		}
		return value;
	}
	
	public String getBaseUrlPropertyName() {
		String baseUrl = System.getProperty("baseURL");
		String value = "";
		if (StringUtils.isNotBlank(baseUrl)) {
			value = baseUrl;
		} else {
			value = prop.getProperty("baseURL");
		}
		return value;
	}
	
}

package gov.va.ascent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class RESTConfigService {
	
	private static RESTConfigService instance = null;
	private Properties prop = null;
	
	private RESTConfigService() {
		
	}
	
	public static RESTConfigService getInstance()  {
		if (instance == null) {
			System.out.println("Instantiating RESTConfigService ############################################## ");
			instance = new RESTConfigService();
			InputStream input = null;
			try {
				String environment = System.getProperty("test.env");
				String url = "";
				if (StringUtils.isNotBlank(environment)) {
					url = "config/restconfig-" + environment + ".properties";
				} else {
					url = "config/restconfig.properties";
				}
				URL urlConfigFile = RESTConfigService.class.getClassLoader().getResource(url);

				File strFile = new File(urlConfigFile.toURI());
				input = new FileInputStream(strFile);
				Properties properties = new Properties();
				properties.load(input);
				instance.prop = properties;
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}	
			finally {
				try {
					input.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("RESTConfigService exists $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ ");
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

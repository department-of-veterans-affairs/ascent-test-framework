package gov.va.ascent.test.framework.service;

import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.test.framework.util.PropertiesUtil;

/**
 * Configures the REST controller using {@code config/vetservices*.properties} files.
 * If an environment specific properties file is desired, a System property named {@code test.env}
 * with the name of the environment must exist. If the System test.env propety does not exist,
 * the default properties file will be used.
 * <p>
 * Examples:<br/>
 * If test.env does not exist in System properties<br/>
 * * property filename is {@code config/vetservices.properties}<br/>
 * If test.env exists in System properties<br/>
 * * test.env=ci<br/>
 * &nbsp;&nbsp;&nbsp;- property filename is {@code config/vetservices-ci.properties}<br/>
 * * test.env=stage<br/>
 * &nbsp;&nbsp;&nbsp;- property filename is {@code config/vetservices-stage.properties}<br/>
 *
 * @author aburkholder
 *
 */
public class RESTConfigService {

	/** The name of the environment in which testing is occurring */
	static final String TEST_ENV = "test.env";

	private static RESTConfigService instance = null;
	private Properties prop = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTConfigService.class);

	private RESTConfigService() {

	}

	/**
	 * Configure the REST controller.
	 *
	 * @return RESTConfigService
	 */
	public static RESTConfigService getInstance() {
		if (instance == null) {
			instance = new RESTConfigService();
			final String environment = System.getProperty(TEST_ENV);
			String url = "";
			if (StringUtils.isNotBlank(environment)) {
				url = "config/vetservices-" + environment + ".properties";
			} else {
				url = "config/vetservices.properties";
			}
			final URL urlConfigFile = RESTConfigService.class.getClassLoader().getResource(url);
			instance.prop = PropertiesUtil.readFile(urlConfigFile);
		}

		return instance;
	}

	/**
	 * Get the property value for the specified property name (key).
	 * If the key does not exist, null is returned.
	 *
	 * @param pName the property key
	 * @return property value
	 */
	public String getProperty(final String pName) {
		return getProperty(pName, false);
	}

	/**
	 * Get the property value for the specified property name (key).
	 * If the {@code isCheckSystemProp} parameter is {@code true}, then
	 * System properties will be searched first. If the property does not exist
	 * in the System properties, then the application properties will be searched.
	 *
	 * @param pName
	 * @param isCheckSystemProp
	 * @return
	 */
	public String getProperty(final String pName, final boolean isCheckSystemProp) {
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

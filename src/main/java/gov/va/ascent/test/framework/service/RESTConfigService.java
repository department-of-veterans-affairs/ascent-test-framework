package gov.va.ascent.test.framework.service;

import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.test.framework.util.AppConstants;
import gov.va.ascent.test.framework.util.PropertiesUtil;
import gov.va.ascent.test.framework.util.RESTUtil;

/**
 * A singleton to hold an instance of this class
 * AND - importantly - the test configuration for the project.
 * <p>
 * Future versions of Java and Maven must *always* spin up a new JVM for each integration test,
 * <i><b>across test iterations, and across every artifact</b></i>.
 * <p>
 * Configure the REST controller using {@code config/vetservices*.properties} files.
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
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTConfigService.class);

	/** The singleton instance of this class */
	private static RESTConfigService instance = null;
	/** The singleton instance of the configuration for the module in which this artifact is a dependency */
	private Properties prop = null;

	/** The name of the environment in which testing is occurring */
	static final String TEST_ENV = "test.env";
	/** URL regex for use by matchers */
	private static final Pattern urlPattern = Pattern.compile("(http|https)://([A-Za-z0-9\\-\\.]+)(:(\\d+))$");

	/**
	 * Do not instantiate
	 */
	private RESTConfigService() {

	}

	/**
	 * Get the configured single instance of the REST controller.
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
	 * Get the value for the specified property name (key).
	 * If the key does not exist, null is returned.
	 *
	 * @param pName the property key
	 * @return property the value associated with pName
	 */
	public String getProperty(final String pName) {
		return getProperty(pName, false);
	}

	/**
	 * Get the value for the specified property name (key).
	 * <p>
	 * If the {@code isCheckSystemProp} parameter is {@code true}, then
	 * System.properties will be searched first. If the property does not exist
	 * in the System.properties, then the application properties will be searched.
	 *
	 * @param pName the key of the property
	 * @param isCheckSystemProp set to {@code true} to first search System.properties
	 * @return String the value associated with pName
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

	/**
	 * Get the base URL for retrieving configurations.
	 * <p>
	 * It is optional to provide a value for the host's port.
	 *
	 * @return String the URL
	 */
	public static String getBaseURL() {
		final RESTConfigService restConfig = RESTConfigService.getInstance();
		final String baseURL = restConfig.getProperty("baseURL", true);

		final String vaultToken = System.getProperty(AppConstants.VAULT_TOKEN_PARAM_NAME);
		if (vaultToken != null && vaultToken != "") {
			final String jsonResponse = VaultService.getVaultCredentials(vaultToken);
			final RESTUtil restUtil = new RESTUtil();
			final String userName = restUtil.parseJSON(jsonResponse, "data.username");
			final String password = restUtil.parseJSON(jsonResponse, "data.password");

			final Matcher m = urlPattern.matcher(baseURL);
			if (!m.matches()) {
				throw new RuntimeException("Invalid base url!");
			}
			final String protocol = m.group(1).toLowerCase();
			final String host = m.group(2);
			final String port = m.group(3);
			final String finalUrl = protocol + "://" + userName + ":" + password + "@" + host + (port != null ? port : "");
			return finalUrl;
		}
		LOGGER.debug("Base URL: {}", baseURL);
		return baseURL;
	}
}

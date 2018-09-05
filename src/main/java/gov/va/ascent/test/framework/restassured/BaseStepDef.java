package gov.va.ascent.test.framework.restassured;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.Scenario;
import gov.va.ascent.test.framework.service.BearerTokenService;
import gov.va.ascent.test.framework.service.RESTConfigService;
import gov.va.ascent.test.framework.util.RESTUtil;

public class BaseStepDef {
	protected RESTUtil resUtil = null;
	protected Map<String, String> headerMap = null;
	protected String strResponse = null;
	protected RESTConfigService restConfig = null;
	private BearerTokenService bearerTokenService = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepDef.class);

	public void initREST() {
		resUtil = new RESTUtil();
		restConfig = RESTConfigService.getInstance();
	}

	public void passHeaderInformation(final Map<String, String> tblHeader) {
		headerMap = new HashMap<>(tblHeader);
	}

	public void invokeAPIUsingGet(final String strURL, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingGet(strURL);
	}

	public void invokeAPIUsingGet(final String strURL) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.getResponse(strURL);
	}

	public void invokeAPIUsingPost(final String strURL, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingPost(strURL);
	}

	public void invokeAPIUsingPost(final String strURL) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponse(strURL);
	}

	public void invokeAPIUsingPostWithMultiPart(final String strURL, final String fileName, final String submitPayloadPath) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponseWithMultipart(strURL, fileName, submitPayloadPath);
	}

	public void invokeAPIUsingDelete(final String strURL, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingDelete(strURL);
	}

	public void invokeAPIUsingDelete(final String strURL) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.deleteResponse(strURL);
	}

	private void setBearerToken() {
		bearerTokenService = BearerTokenService.getInstance();
		final String bearerToken = bearerTokenService.getBearerToken();
		headerMap.put("Authorization", "Bearer " + bearerToken);
	}

	public void validateStatusCode(final int intStatusCode) {
		resUtil.validateStatusCode(intStatusCode);
	}

	/**
	 * Loads JSON property file that contains header values in to header map. Method
	 * parameter user contains environment and user name delimited by -. The method
	 * parses the environment and user name and loads JSON header file.
	 *
	 * @param user
	 *            Contains the environment and user name delimited by - for eg:
	 *            ci-janedoe
	 * @throws IOException
	 */
	public void setHeader(final String user) throws IOException {
		final Map<String, String> tblHeader = new HashMap<>();

		final String[] values = user.split("-");

		final String env = values[0];
		final String userName = values[1];
		final String url = "users/" + env + "/" + userName + ".properties";
		final Properties properties = new Properties();
		InputStream is = null;
		try {
			is = RESTConfigService.class.getClassLoader().getResourceAsStream(url);
			properties.load(is);
			for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
				tblHeader.put((String) entry.getKey(), (String) entry.getValue());
			}
		} finally {
			IOUtils.closeQuietly(is);
		}

		passHeaderInformation(tblHeader);
	}

	public boolean compareExpectedResponseWithActual(final String strResFile) {
		boolean isMatch = false;
		try {
			final String strExpectedResponse = resUtil.readExpectedResponse(strResFile);
			final ObjectMapper mapper = new ObjectMapper();
			final Object strExpectedResponseJson = mapper.readValue(strExpectedResponse, Object.class);
			final String prettyStrExpectedResponse = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(strExpectedResponseJson);
			final Object strResponseJson = mapper.readValue(strResponse, Object.class);
			final String prettyStrResponseJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(strResponseJson);
			isMatch = prettyStrResponseJson.contains(prettyStrExpectedResponse);
		} catch (final IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
		}
		return isMatch;
	}

	/**
	 * Does an assertion per line. Reads the expected response file. Loops through
	 * each of this file and does an assertion to see if it exists in the actual
	 * service response.
	 *
	 * If the actual response contains a lot of information that we can ignore, we
	 * can just target the lines we are concern with.
	 *
	 * @param responseFileName
	 *            The response file.
	 */
	public boolean compareExpectedResponseWithActualByRow(final String strResFile) {
		final String strExpectedResponse = resUtil.readExpectedResponse(strResFile);
		final StringTokenizer tokenizer = new StringTokenizer(strExpectedResponse, "\n");
		while (tokenizer.hasMoreTokens()) {
			final String responseLine = tokenizer.nextToken();
			if (!strResponse.contains(responseLine)) {
				return false;
			}
		}
		return true;
	}

	public void postProcess(final Scenario scenario) {
		String strResponseFile = null;
		try {
			strResponseFile = "target/TestResults/Response/" + scenario.getName() + ".Response";
			FileUtils.writeStringToFile(new File(strResponseFile), strResponse, StandardCharsets.UTF_8);
		} catch (final Exception ex) {
			LOGGER.error("Failed:Unable to write response to a file", ex);

		}
		scenario.write(scenario.getStatus());
	}

}

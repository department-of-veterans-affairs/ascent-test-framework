package gov.va.ascent.test.framework.restassured;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;

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
		restConfig =  RESTConfigService.getInstance();
	}

	public void passHeaderInformation(Map<String, String> tblHeader)  {
		headerMap = new HashMap<>(tblHeader);
	}

	public void invokeAPIUsingGet(String strURL, boolean isAuth)  {
		if(isAuth) {
			setBearerToken();
		}
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.getResponse(strURL);
	}

	public void invokeAPIUsingPost(String strURL, boolean isAuth)  {
		if(isAuth) {
			setBearerToken();
		}
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponse(strURL);
	}
	
	public void invokeAPIUsingDelete(String strURL, boolean isAuth )  {
		if(isAuth) {
			setBearerToken();
		}
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.deleteResponse(strURL);
	}

	private void setBearerToken() {
		bearerTokenService = BearerTokenService.getInstance();
		String bearerToken = bearerTokenService.getBearerToken();
		headerMap.put("Authorization", "Bearer "+bearerToken);		
	}
	
	public void validateStatusCode(int intStatusCode)  {
		resUtil.validateStatusCode(intStatusCode);
	}
	
	
	public boolean compareExpectedResponseWithActual(String strResFile)  {
		boolean isMatch = false;
		try {
			String strExpectedResponse = resUtil.readExpectedResponse(strResFile);
			ObjectMapper mapper = new ObjectMapper();
			Object strExpectedResponseJson = mapper.readValue(strExpectedResponse, Object.class);
			String prettyStrExpectedResponse = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(strExpectedResponseJson);
			Object strResponseJson = mapper.readValue(strResponse, Object.class);
			String prettyStrResponseJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(strResponseJson);
			isMatch = prettyStrResponseJson.contains(prettyStrExpectedResponse);
		}
		catch (IOException ioe) {
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
	public boolean compareExpectedResponseWithActualByRow(String strResFile) {
		String strExpectedResponse = resUtil.readExpectedResponse(strResFile);
		StringTokenizer tokenizer = new StringTokenizer(strExpectedResponse, "\n");
		while (tokenizer.hasMoreTokens()) {
			String responseLine = tokenizer.nextToken();
			if (!strResponse.contains(responseLine)) {
				return false;
			}
		}
		return true;
	}
	
	public void postProcess(Scenario scenario) {
		String strResponseFile = null;
		try {
			strResponseFile = "target/TestResults/Response/" + scenario.getName() + ".Response";
			FileUtils.writeStringToFile(new File(strResponseFile), strResponse, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			LOGGER.error("Failed:Unable to write response to a file", ex);
			
		}
		scenario.write(scenario.getStatus());
	}

}

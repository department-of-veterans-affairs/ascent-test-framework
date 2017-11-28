package gov.va.ascent.util;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.Scenario;

public class BaseStepDef {
	protected RESTUtil resUtil = null;
	protected HashMap<String, String> headerMap = null;
	protected String strResponse = null;
	private RESTConfigService restConfig = null;
	private BearerTokenService bearerTokenService = null;

	final Logger log = LoggerFactory.getLogger(BaseStepDef.class);

	public void initREST() {
		try {
			resUtil = new RESTUtil();
			restConfig =  RESTConfigService.getInstance();
			bearerTokenService = BearerTokenService.getInstance();
			
		} catch (Exception ex) {
			log.info("Failed:Setup of REST util failed");
			System.out.println("Failed:Setup of REST util failed==============================");
			
		}
	}

	public void passHeaderInformation(Map<String, String> tblHeader) throws Throwable {

		headerMap = new HashMap<String, String>(tblHeader);
		System.out.println(headerMap);
	}

	public void invokeAPIUsingGet(String strURL) throws Throwable {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.GETResponse(strURL);
		log.info("Actual Response=" + strResponse);
	}

	public void invokeAPIUsingGet(String strURL, String baseUrlProperty) throws Throwable {
		String baseUrl = restConfig.getBaseUrlPropertyName();
		//String baseUrl = restConfig.getPropertyName(baseUrlProperty);
		invokeAPIUsingGet(baseUrl + strURL);
		log.info("Actual Response=" + strResponse);
	}

	public void invokeAPIUsingPost(String strURL) throws Throwable {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.POSTResponse(strURL);
		log.info("Actual Response=" + strResponse);
	}

	public void invokeAPIUsingPost(String strURL, String baseUrlProperty) throws Throwable {
		String bearerToken = bearerTokenService.getBearerToken();
		System.out.println("Bearer token ===================================="+bearerToken);
		headerMap.put("Authorization", "Bearer "+bearerToken);
		//String baseUrl = restConfig.getPropertyName(baseUrlProperty);
		String baseUrl = restConfig.getBaseUrlPropertyName();
		invokeAPIUsingPost(baseUrl + strURL);
		log.info("Actual Response=" + strResponse);
	}
	

	public void invokeAPIUsingdDelete(String strURL) throws Throwable {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.DELETEResponse(strURL);
		log.info("Actual Response=" + strResponse);
	}
	
	public void invokeAPIUsingDelete(String strURL, String baseUrlProperty) throws Throwable {
		//String baseUrl = restConfig.getPropertyName(baseUrlProperty);
		String baseUrl = restConfig.getBaseUrlPropertyName();
		invokeAPIUsingdDelete(baseUrl + strURL);
		log.info("Actual Response=" + strResponse);
	}

	public void ValidateStatusCode(int intStatusCode) throws Throwable {
		resUtil.ValidateStatusCode(intStatusCode);
	}
	
	public void checkResponseContainsValue(String strResFile) throws Throwable {

		String strExpectedResponse = resUtil.readExpectedResponse(strResFile);
		ObjectMapper mapper = new ObjectMapper();
		Object strExpectedResponseJson = mapper.readValue(strExpectedResponse, Object.class);
		String prettyStrExpectedResponse = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(strExpectedResponseJson);
		Object strResponseJson = mapper.readValue(strResponse, Object.class);
		String prettyStrResponseJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(strResponseJson);
		assertThat(prettyStrResponseJson).contains(prettyStrExpectedResponse);
		log.info("Actual Response matched the expected response");

	}

	public void postProcess(Scenario scenario) {
		String strResponseFile = null;
		try {
			strResponseFile = "target/TestResults/Response/" + scenario.getName() + ".Response";
			FileUtils.writeStringToFile(new File(strResponseFile), strResponse);
		} catch (Exception ex) {
			log.info("Failed:Unable to write response to a file");
			ex.printStackTrace();
		}
		scenario.write(scenario.getStatus());
	}

}

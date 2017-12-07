package gov.va.ascent.test.framework.restassured;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
	protected HashMap<String, String> headerMap = null;
	protected String strResponse = null;
	protected RESTConfigService restConfig = null;
	private BearerTokenService bearerTokenService = null;

	final Logger log = LoggerFactory.getLogger(BaseStepDef.class);

	public void initREST() {
		try {
			resUtil = new RESTUtil();
			restConfig =  RESTConfigService.getInstance();
			
			
		} catch (Exception ex) {
			log.info("Failed:Setup of REST util failed");
			System.out.println("Failed:Setup of REST util failed==============================");
			
		}
	}

	public void passHeaderInformation(Map<String, String> tblHeader) throws Throwable {

		headerMap = new HashMap<String, String>(tblHeader);
		System.out.println(headerMap);
	}

	public void invokeAPIUsingGet(String strURL, boolean isAuth) throws Throwable {
		if(isAuth == true) {
			setBearerToken();
		}
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.GETResponse(strURL);
		log.info("Actual Response=" + strResponse);
	}

	public void invokeAPIUsingPost(String strURL, boolean isAuth) throws Throwable {
		if(isAuth == true) {
			setBearerToken();
		}
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.POSTResponse(strURL);
		log.info("Actual Response=" + strResponse);
	}
	
	public void invokeAPIUsingDelete(String strURL, boolean isAuth ) throws Throwable {
		if(isAuth == true) {
			setBearerToken();
		}
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.DELETEResponse(strURL);
		log.info("Actual Response=" + strResponse);
	}

	private void setBearerToken() {
		bearerTokenService = BearerTokenService.getInstance();
		String bearerToken = bearerTokenService.getBearerToken();
		headerMap.put("Authorization", "Bearer "+bearerToken);		
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

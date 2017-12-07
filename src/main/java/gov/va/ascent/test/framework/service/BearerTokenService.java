package gov.va.ascent.test.framework.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.test.framework.util.RESTUtil;

public class BearerTokenService {
	final Logger log = LoggerFactory.getLogger(BearerTokenService.class);
	
	private static BearerTokenService instance = null;
	private String bearerToken = "";
	
	private BearerTokenService() {
		
	}
	
	public static BearerTokenService getInstance()  {
		
		if (instance == null) {
			System.out.println("Instantiating BearerTokenService ############################################## ");
			instance = new BearerTokenService();
			try {
				RESTConfigService restConfig = RESTConfigService.getInstance();
				//String baseUrl = restConfig.getPropertyName("baseURL");
				String baseUrl = restConfig.getBaseUrlPropertyName();
				String tokenUrl = restConfig.getPropertyName("tokenUrl");
				//String tokenUrl = "https://stage.internal.vets-api.gov:8762/api/ascent-demo-service/token";
				HashMap<String, String> headerMap = new HashMap();
				headerMap.put("Accept", "application/json;v=3");
				headerMap.put("Content-Type", "application/json;v=3; charset=ISO-8859-1");
				RESTUtil restUtil = new RESTUtil();
				restUtil.setUpRequest("token.Request", headerMap);
				instance.bearerToken = restUtil.POSTResponse(baseUrl + tokenUrl);
			} catch (Exception ex) {
				ex.printStackTrace();
			}	
		}
		else {
			System.out.println("BearerTokenService exists $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ ");
		}
		return instance;
	}
	
	public String getBearerToken() {
		return bearerToken;
	}
	

}

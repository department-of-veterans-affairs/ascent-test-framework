package gov.va.ascent.test.framework.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;

import gov.va.ascent.test.framework.util.RESTUtil;

public class BearerTokenService {

	private static BearerTokenService instance = null;
	private String bearerToken = "";

	private BearerTokenService() {

	}

	public static BearerTokenService getInstance() {

		if (instance == null) {
			instance = new BearerTokenService();
			instance.bearerToken = getToken("token.Request");
		}
		return instance;
	}

	public static String getTokenByHeaderFile(String headerFile) {
		return getToken(headerFile);
	}

	public static String getToken(String headerFile) {
		RESTConfigService restConfig = RESTConfigService.getInstance();
		String baseUrl = restConfig.getPropertyName("baseURL", true);
		String tokenUrl = restConfig.getPropertyName("tokenUrl");
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Accept", ContentType.APPLICATION_JSON.getMimeType());
		headerMap.put("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
		RESTUtil restUtil = new RESTUtil();
		restUtil.setUpRequest(headerFile, headerMap);
		return restUtil.postResponse(baseUrl + tokenUrl);
	}

	public String getBearerToken() {
		return bearerToken;
	}

}

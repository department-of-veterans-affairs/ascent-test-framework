package gov.va.ascent.test.framework.service;

import java.util.HashMap;
import java.util.Map;

import gov.va.ascent.test.framework.util.RESTUtil;

public class VaultService {
	private static final String VAULT_TOKEN_PARAM_NAME = "X-Vault-Token";

	private VaultService() {
		
	}
	
	public static String replaceUrlWithVaultCredentialDiscovery(String url)  {
		String finalUrl = url;
		RESTConfigService restConfig =  RESTConfigService.getInstance();
		String vaultToken = System.getProperty(VAULT_TOKEN_PARAM_NAME);
		if(vaultToken != null && vaultToken != "") {
			RESTUtil restUtil = new RESTUtil();
			Map<String, String> headerMap = new HashMap<>();
			headerMap.put(VAULT_TOKEN_PARAM_NAME, vaultToken);
			restUtil.setUpRequest(headerMap);
			String vaultUrl =  restConfig.getPropertyName("vault.url");
			String jsonResponse = restUtil.getResponse(vaultUrl);
			
		
			String userName = restUtil.parseJSON(jsonResponse, "data.username");
			String password = restUtil.parseJSON(jsonResponse, "data.password"); 
			
			finalUrl = url.replace("@user", userName);
			finalUrl = finalUrl.replace("@password", password);
			
		}
		return finalUrl;
	}
	
	public static String replaceUrlWithVaultCredentialDashboard(String url)  {
		String finalUrl = url;
		RESTConfigService restConfig =  RESTConfigService.getInstance();
		String vaultToken = System.getProperty(VAULT_TOKEN_PARAM_NAME);
		if(vaultToken != null && vaultToken != "") {
			RESTUtil restUtil = new RESTUtil();
			Map<String, String> headerMap = new HashMap<>();
			headerMap.put(VAULT_TOKEN_PARAM_NAME, vaultToken);
			restUtil.setUpRequest(headerMap);
			String vaultUrl =  restConfig.getPropertyName("vault.url");
			String jsonResponse = restUtil.getResponse(vaultUrl);
			
			
			String userName = restUtil.parseJSON(jsonResponse, "data.'ascent.security.username'");
			String password = restUtil.parseJSON(jsonResponse, "data.'ascent.security.password'"); 
			
			finalUrl = url.replace("@user", userName);
			finalUrl = finalUrl.replace("@password", password);
			
		}
		return finalUrl;
	}
}
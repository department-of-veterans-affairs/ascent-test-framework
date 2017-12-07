package gov.va.ascent.test.framework.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.test.framework.util.RESTUtil;

public class VaultService {
	public static final String VAULT_TOKEN_PARAM_NAME = "X-Vault-Token";

	final Logger log = LoggerFactory.getLogger(VaultService.class);
	public static String replaceUrlWithVaultCredentialDiscovery(String url)  {
		RESTConfigService restConfig =  RESTConfigService.getInstance();
		String vaultToken = System.getProperty(VAULT_TOKEN_PARAM_NAME);
		if(vaultToken != null && vaultToken != "") {
			RESTUtil restUtil = new RESTUtil();
			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put(VAULT_TOKEN_PARAM_NAME, vaultToken);
			restUtil.setUpRequest(headerMap);
			String vaultUrl =  restConfig.getPropertyName("vault.url");
			String jsonResponse = restUtil.GETResponse(vaultUrl);
			
		
			String userName = restUtil.parseJSON(jsonResponse, "data.username");
			String password = restUtil.parseJSON(jsonResponse, "data.password"); 
			
			url = url.replace("@user", userName);
			url = url.replace("@password", password);
			
		}
		return url;
}
	public static String replaceUrlWithVaultCredentialDashboard(String url)  {
		RESTConfigService restConfig =  RESTConfigService.getInstance();
		String vaultToken = System.getProperty(VAULT_TOKEN_PARAM_NAME);
		if(vaultToken != null && vaultToken != "") {
			RESTUtil restUtil = new RESTUtil();
			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put(VAULT_TOKEN_PARAM_NAME, vaultToken);
			restUtil.setUpRequest(headerMap);
			String vaultUrl =  restConfig.getPropertyName("vault.url");
			String jsonResponse = restUtil.GETResponse(vaultUrl);
			
			
			String userName = restUtil.parseJSON(jsonResponse, "data.'ascent.security.username'");
			String password = restUtil.parseJSON(jsonResponse, "data.'ascent.security.password'"); 
			
			url = url.replace("@user", userName);
			url = url.replace("@password", password);
			
		}
		return url;
}
}
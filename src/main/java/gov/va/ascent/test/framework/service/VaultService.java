package gov.va.ascent.test.framework.service;

import java.util.HashMap;
import java.util.Map;

import gov.va.ascent.test.framework.util.AppConstants;
import gov.va.ascent.test.framework.util.RESTUtil;

public class VaultService {
	
	private VaultService() {
		
	}
	
	public static String getVaultCredentials(String vaultToken)  {
		RESTConfigService restConfig =  RESTConfigService.getInstance();
		RESTUtil restUtil = new RESTUtil();
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put(AppConstants.VAULT_TOKEN_PARAM_NAME, vaultToken);
		restUtil.setUpRequest(headerMap);
		String vaultUrlDomain =  restConfig.getPropertyName("vault.url.domain", true);
		String vaultURLPath =  restConfig.getPropertyName("vault.url.path", true);
		return restUtil.getResponse(vaultUrlDomain+vaultURLPath);

	}
}
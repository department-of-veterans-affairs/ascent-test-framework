package gov.va.ascent.test.framework.service;

import java.util.HashMap;
import java.util.Map;

import gov.va.ascent.test.framework.util.AppConstants;
import gov.va.ascent.test.framework.util.RESTUtil;

public class VaultService {

	private VaultService() {

	}

	public static String getVaultCredentials(final String vaultToken) {
		final RESTConfigService restConfig = RESTConfigService.getInstance();
		final RESTUtil restUtil = new RESTUtil();
		final Map<String, String> headerMap = new HashMap<>();
		headerMap.put(AppConstants.VAULT_TOKEN_PARAM_NAME, vaultToken);
		restUtil.setUpRequest(headerMap);
		final String vaultUrlDomain = restConfig.getProperty("vault.url.domain", true);
		final String vaultURLPath = restConfig.getProperty("vault.url.path", true);
		return restUtil.getResponse(vaultUrlDomain + vaultURLPath);

	}
}
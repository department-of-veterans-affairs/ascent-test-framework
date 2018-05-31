package gov.va.ascent.test.framework.restassured;

import java.util.HashMap;
import java.util.Map;


import gov.va.ascent.test.framework.service.RESTConfigService;
import gov.va.ascent.test.framework.util.RESTUtil;
/**
 * Handler object that extends BaseStepDef to handle rest based api call. Step definition class inject this object thru constructor.
 *
 */
public class BaseStepDefHandler extends BaseStepDef {
	public BaseStepDefHandler() {
		initREST();
	}

	public RESTUtil getRestUtil() {
		return resUtil;
	}
	@Override
	public void passHeaderInformation(Map<String, String> tblHeader) {
		headerMap = new HashMap<>(tblHeader);
	}

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	public String getStrResponse() {
		return strResponse;
	}

	public RESTConfigService getRestConfig() {
		return restConfig;
	}

}

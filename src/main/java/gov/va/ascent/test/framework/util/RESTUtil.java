package gov.va.ascent.test.framework.util;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.response.Response;

public class RESTUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RESTUtil.class);

	private Map<String, String> mapReqHeader = new HashMap<>(); // stores
																			// request
																			// headers
	String contentType = new String();
	String jsonText = new String();
	File requestFile = null;
	File responseFile = null;
	PrintStream requestStream = null;
	Response response = null; // stores response from rest

	/**
	 * Reads file content for a given file resource using URL object.
	 * 
	 * @param strRequestFile
	 * @param mapHeader
	 * @throws Exception
	 */
	public void setUpRequest(String strRequestFile, Map<String, String> mapHeader)  {
		try {
			mapReqHeader = mapHeader;
			if (strRequestFile != null) {
				URL urlFilePath = RESTUtil.class.getClassLoader().getResource("Request/" + strRequestFile);
				requestFile = new File(urlFilePath.toURI());

				// Note - Enhance the code so if Header.Accept is xml, then it
				// should use something like convertToXML function
				jsonText = readFile(requestFile);
			}
		} catch (URISyntaxException ex) {
			LOGGER.error("Unable to do setUpRequest", ex);
		}
	}

	/**
	 * Assigns given header object into local header map.
	 * 
	 * @param mapHeader
	 * @throws Exception
	 */
	public void setUpRequest(Map<String, String> mapHeader)  {
		mapReqHeader = mapHeader;
	}

	/**
	 * Invokes REST end point for a GET method using REST assured API and return
	 * response json object.
	 * 
	 * @param serviceURL
	 * @return
	 */
	public String getResponse(String serviceURL) {
		RestAssured.useRelaxedHTTPSValidation();
		response = given().headers(mapReqHeader).urlEncodingEnabled(false).when().get(serviceURL);
		return response.asString();
	}

	public String deleteResponse(String serviceURL) {
		response = given().headers(mapReqHeader).urlEncodingEnabled(false).when().delete(serviceURL);
		return response.asString();
	}

	public String postResponse(String serviceURL) {
		RestAssured.useRelaxedHTTPSValidation();
		response = given().urlEncodingEnabled(false).headers(mapReqHeader).body(jsonText).when()
				.post(serviceURL);
		return response.asString();
	}

	public String putResponse(String serviceURL) {
		RestAssured.useRelaxedHTTPSValidation();
		response = given().urlEncodingEnabled(false).headers(mapReqHeader).body(jsonText).when().put(serviceURL);
		return response.asString();
	}

	/**
	 * Parses json object for a given key and match with given expected value.
	 * 
	 * @param json
	 * @param strRoot
	 * @param strField
	 * @param strExpectedValue
	 * @return
	 */
	public String parseJSON(String json, String strRoot, String strField, String strExpectedValue) {
		String strResult = null;
		JsonPath jsonPath = new JsonPath(json).setRoot(strRoot);
		List<String> lstField = jsonPath.get(strField);
		if (lstField.contains(strExpectedValue)) {
			strResult = lstField.toString();
			LOGGER.info("Passed:Field=" + strField + " matched the expected value=" + strExpectedValue);
		} else {
			strResult = lstField.toString();
			LOGGER.info("Failed:Field=" + strField + " expected value=" + strExpectedValue + " and actual value="
					+ lstField.toString());
		}
		return strResult;
	}
	
	public String parseJSON(String json, String strField) {
		String strResult = null;
		try {
			JsonPath jsonPath = new JsonPath(json);
			strResult = jsonPath.get(strField).toString();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(),ex);
		}
		return strResult;
	}

	public String parseJSONroot(String json, String strRoot) {
		String strResult = null;
		strResult = new JsonPath(json).get(strRoot).toString();
		 
		return strResult;
	}

	public String parseXML(String xml, String strFieldName, String strExpectedValue) {
		String strResult = null;

		XmlPath xmlPath = new XmlPath(xml);
		String strField = xmlPath.get(strFieldName).toString();
		if (strField.contains(strExpectedValue)) {
			strResult = strField;
			
		} 
		return strResult;
	}

	public String parseXML(String xml, String strFieldName) {
		XmlPath xmlPath = new XmlPath(xml);
		return xmlPath.get(strFieldName).toString();
		
	}

	public String parseXML(String xml, String strRoot, String strFieldName, String strExpectedValue) {
		String strResult = null;

		XmlPath xmlPath = new XmlPath(xml).setRoot(strRoot);

		String strField = xmlPath.get(strFieldName);
		if (strField.contains(strExpectedValue)) {
			strResult = strField;
		} 
		return strResult;
	}

	public String prettyFormatXML(String strXml) {
		String xml = strXml;
		String result = null;
		try {
			Document doc = DocumentHelper.parseText(xml);
			StringWriter sw = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xw = new XMLWriter(sw, format);
			xw.write(doc);
			result = sw.toString();
		} catch (DocumentException |IOException ex) {
			LOGGER.error(ex.getMessage(),ex);
		}
		
		return result;
	}

	public boolean compareResponse(String strExpectedResponseFile, String strActualResponse) {
		boolean isEqual = false;
		try {
			URL urlFilePath = RESTUtil.class.getClassLoader().getResource("Response/" + strExpectedResponseFile);
			responseFile = new File(urlFilePath.toURI());
			String strExpectedResponse = readFile(responseFile);

			if (strActualResponse.contains(strExpectedResponse)) {
				isEqual = true;
			}
		} catch (URISyntaxException ex) {
			LOGGER.error(ex.getMessage(),ex);
		}
		return isEqual;

	}

	public String readExpectedResponse(String filename) {
		String strExpectedResponse = null;
		try {
			URL urlFilePath = RESTUtil.class.getClassLoader().getResource("Response/" + filename);
			File strFilePath = new File(urlFilePath.toURI());
			strExpectedResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		} catch (URISyntaxException |IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		
		return strExpectedResponse;
	}

	protected String readFile(File filename) {
		String content = null;
		File file = filename;
		try (FileReader reader = new FileReader(file)) {
				char[] chars = new char[(int) file.length()];
				reader.read(chars);
				content = new String(chars);
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return content;
		
	}

	public void validateStatusCode(int intStatusCode) {
		int actStatusCode = response.getStatusCode();
		Assert.assertEquals(intStatusCode, actStatusCode);
	}

}

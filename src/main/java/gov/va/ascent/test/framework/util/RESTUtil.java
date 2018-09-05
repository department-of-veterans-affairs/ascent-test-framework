package gov.va.ascent.test.framework.util;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import com.jayway.restassured.specification.RequestSpecification;

public class RESTUtil {

	private static final String DOCUMENTS_FOLDER_NAME = "documents";
	private static final String PAYLOAD_FOLDER_NAME = "payload";
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
	public void setUpRequest(final String strRequestFile, final Map<String, String> mapHeader) {
		try {
			mapReqHeader = mapHeader;
			if (strRequestFile != null) {
				LOGGER.info("Request File {}", strRequestFile);
				final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("Request/" + strRequestFile);
				if (urlFilePath == null) {
					LOGGER.error("Requested File Doesn't Exist: {}", "Request/" + strRequestFile);
				} else {
					requestFile = new File(urlFilePath.toURI());
					// Note - Enhance the code so if Header.Accept is xml, then it
					// should use something like convertToXML function
					jsonText = readFile(requestFile);
				}
			}
		} catch (final URISyntaxException ex) {
			LOGGER.error("Unable to do setUpRequest", ex);
		}
	}

	/**
	 * Assigns given header object into local header map.
	 *
	 * @param mapHeader
	 * @throws Exception
	 */
	public void setUpRequest(final Map<String, String> mapHeader) {
		mapReqHeader = mapHeader;
	}

	/**
	 * Invokes REST end point for a GET method using REST assured API and return
	 * response json object.
	 *
	 * @param serviceURL
	 * @return
	 */
	public String getResponse(final String serviceURL) {
		RestAssured.useRelaxedHTTPSValidation();

		RequestSpecification requestSpecification = given();
		if (LOGGER.isDebugEnabled()) {
			requestSpecification = given().log().all();
		}
		response = requestSpecification.headers(mapReqHeader).urlEncodingEnabled(false).when().get(serviceURL);
		return response.asString();
	}

	public String deleteResponse(final String serviceURL) {
		RequestSpecification requestSpecification = given();
		if (LOGGER.isDebugEnabled()) {
			requestSpecification = given().log().all();
		}
		response = requestSpecification.headers(mapReqHeader).urlEncodingEnabled(false).when().delete(serviceURL);
		return response.asString();
	}

	public String postResponse(final String serviceURL) {
		RestAssured.useRelaxedHTTPSValidation();
		RequestSpecification requestSpecification = given();
		if (LOGGER.isDebugEnabled()) {
			requestSpecification = given().log().all();
		}
		response = requestSpecification.urlEncodingEnabled(false).headers(mapReqHeader).body(jsonText).when()
				.post(serviceURL);
		return response.asString();
	}

	public String postResponseWithMultipart(final String serviceURL, final String fileName, final String submitPayloadPath) {
		RestAssured.useRelaxedHTTPSValidation();
		RequestSpecification requestSpecification = given();
		if (LOGGER.isDebugEnabled()) {
			requestSpecification = given().log().all();
		}
		final URL urlFilePath = RESTUtil.class.getClassLoader().getResource(DOCUMENTS_FOLDER_NAME + "/" + fileName);
		final URL urlSubmitPayload = RESTUtil.class.getClassLoader().getResource(PAYLOAD_FOLDER_NAME + "/" + submitPayloadPath);
		
		try {
			final File filePath = new File(urlFilePath.toURI());
			final File filePathSubmitPayload = new File(urlSubmitPayload.toURI());
			String submitPayload = FileUtils.readFileToString(filePathSubmitPayload, "UTF-8");
			response = requestSpecification.contentType("multipart/form-data").urlEncodingEnabled(false).headers(mapReqHeader).when()
					.multiPart("file", filePath)
					.multiPart("submitPayload", submitPayload, "application/json")
					.post(serviceURL);
		} catch (final Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			
		}
		return response.asString();
		
	}
	
	public String putResponse(final String serviceURL) {
		RestAssured.useRelaxedHTTPSValidation();
		RequestSpecification requestSpecification = given();
		if (LOGGER.isDebugEnabled()) {
			requestSpecification = given().log().all();
		}
		response = requestSpecification.urlEncodingEnabled(false).headers(mapReqHeader).body(jsonText).when().put(serviceURL);
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
	public String parseJSON(final String json, final String strRoot, final String strField, final String strExpectedValue) {
		String strResult = null;
		final JsonPath jsonPath = new JsonPath(json).setRoot(strRoot);
		final List<String> lstField = jsonPath.get(strField);
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

	public String parseJSON(final String json, final String strField) {
		String strResult = null;
		try {
			final JsonPath jsonPath = new JsonPath(json);
			strResult = jsonPath.get(strField).toString();
		} catch (final Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return strResult;
	}

	public String parseJSONroot(final String json, final String strRoot) {
		String strResult = null;
		strResult = new JsonPath(json).get(strRoot).toString();

		return strResult;
	}

	public String parseXML(final String xml, final String strFieldName, final String strExpectedValue) {
		String strResult = null;

		final XmlPath xmlPath = new XmlPath(xml);
		final String strField = xmlPath.get(strFieldName).toString();
		if (strField.contains(strExpectedValue)) {
			strResult = strField;

		}
		return strResult;
	}

	public String parseXML(final String xml, final String strFieldName) {
		final XmlPath xmlPath = new XmlPath(xml);
		return xmlPath.get(strFieldName).toString();

	}

	public String parseXML(final String xml, final String strRoot, final String strFieldName, final String strExpectedValue) {
		String strResult = null;

		final XmlPath xmlPath = new XmlPath(xml).setRoot(strRoot);

		final String strField = xmlPath.get(strFieldName);
		if (strField.contains(strExpectedValue)) {
			strResult = strField;
		}
		return strResult;
	}

	public String prettyFormatXML(final String strXml) {
		final String xml = strXml;
		String result = null;
		try {
			final Document doc = DocumentHelper.parseText(xml);
			final StringWriter sw = new StringWriter();
			final OutputFormat format = OutputFormat.createPrettyPrint();
			final XMLWriter xw = new XMLWriter(sw, format);
			xw.write(doc);
			result = sw.toString();
		} catch (DocumentException | IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		return result;
	}

	public String readExpectedResponse(final String filename) {
		String strExpectedResponse = null;
		try {
			LOGGER.info("Response File: {}", filename);
			final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("Response/" + filename);
			if (urlFilePath == null) {
				LOGGER.error("Requested File Doesn't Exist: {}", "Response/" + filename);
			} else {
				final File strFilePath = new File(urlFilePath.toURI());
				strExpectedResponse = FileUtils.readFileToString(strFilePath, "ASCII");
			}
		} catch (URISyntaxException | IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		return strExpectedResponse;
	}

	protected String readFile(final File filename) {
		String content = null;
		final File file = filename;
		try (FileReader reader = new FileReader(file)) {
			final char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
		} catch (final IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return content;

	}

	public void validateStatusCode(final int intStatusCode) {
		final int actStatusCode = response.getStatusCode();
		Assert.assertEquals(intStatusCode, actStatusCode);
	}

}

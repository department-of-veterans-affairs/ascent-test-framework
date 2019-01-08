package gov.va.ascent.test.framework.util;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.path.json.JsonPath;
/**
 * Utility class for parsing JSON.
 * @author sravi
 *
 */
public class JsonUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

	private JsonUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static void println(final String name, final List<?> objects) {
		LOGGER.info(name + ": " + objects);
		final int n = objects.size();
		for (int i = 0; i < n; ++i) {
			LOGGER.info("  " + i + ": " + objects.get(i));
		}
	}

	public static final List<Object> getList(final String json, final String path) {
		return JsonPath.with(json).get(path);
	}

	public static final String getString(final String json, final String path) {
		return JsonPath.with(json).get(path);
	}

	public static final Map<String, Object> getMap(final String json, final String path) {
		return JsonPath.with(json).get(path);
	}

	public static final List<Map<String, Object>> getMapList(final String json, final String path) {
		return JsonPath.with(json).get(path);
	}

	public static final String getString(final Map<String, Object> map, final String name) {
		return (String) map.get(name);
	}

	public static final int getInt(final Map<String, Object> map, final String name) {
		return (int) map.get(name);
	}

	public static final long getLong(final Map<String, Object> map, final String name) {
		return (long) map.get(name);
	}

	@SuppressWarnings(value = "unchecked")
	public static final List<Map<String, Object>> getMapList(final Map<String, Object> map, final String name) {
		return (List<Map<String, Object>>) map.get(name);
	}

	public static final List<Map<String, Object>> assertMapListHasValue(final Map<String, Object> map,
			final String name) {
		final List<Map<String, Object>> list = getMapList(map, name);
		Assert.assertNotNull(list);
		return list;
	}

	public static final String assertDateYMDHasValue(final Map<String, Object> map, final String name) {
		final String value = assertStringHasValue(map, name);
		final boolean isTrue = value.matches("\\d{4}-\\d{2}-\\d{2}.*");
		assertVariable(name, isTrue, "not in format YYYY-MM-DD.");
		return value;
	}

	public static final String assertDateYMDMightHaveValue(final Map<String, Object> map, final String name) {
		final String value = getString(map, name);
		if (value != null) {
			final boolean isTrue = value.matches("\\d{4}-\\d{2}-\\d{2}.*");
			assertVariable(name, isTrue, "not in format YYYY-MM-DD.");
		}
		return value;
	}

	public static final String assertStringHasValue(final Map<String, Object> map, final String name) {
		final String value = getString(map, name);
		Assert.assertNotNull(value);
		assertVariable(name, value.trim().length() > 0, "cannot be an empty string.");
		return value;
	}

	public static final int assertIntHasValue(final Map<String, Object> map, final String name) {
		final int value = getInt(map, name);
		assertVariable(name, true, "cannot be null.");
		return value;
	}

	public static final long assertLongHasValue(final Map<String, Object> map, final String name) {
		final long value = getLong(map, name);
		assertVariable(name, true, "cannot be null.");
		return value;
	}

	public static final void assertVariable(final String name, final boolean isTrue, final String message) {
		final String text = "JSON variable " + name + ": " + message;
		Assert.assertTrue(text, isTrue);
	}

	public static final List<Object> assertListHasValue(final String json, final String path) {
		final List<Object> list = getList(json, path);
		Assert.assertNotNull(list);
		return list;
	}

	public static final List<Map<String, Object>> assertMapListHasValue(final String json, final String path) {
		final List<Map<String, Object>> list = getMapList(json, path);
		Assert.assertNotNull(list);
		return list;
	}

	public static final List<Map<String, Object>> assertMapListIsNull(final String json, final String path) {
		final List<Map<String, Object>> list = getMapList(json, path);
		Assert.assertNull(list);
		return list;
	}

	public static final Object getObjectAssertNotNull(String jsonRequest, String path) {
		Object value = JsonPath.with(jsonRequest).get(path);
		Assert.assertNotNull("json does not contain: " + path + ".", value);
		return value;
	}

	public static final Object getObjectAssertIsNull(String jsonRequest, String path) {
		Object value = JsonPath.with(jsonRequest).get(path);
		Assert.assertNull("json contains: " + path + ".", value);
		return value;
	}

	public static final String getStringAssertNotBlank(String jsonRequest, String path) {
		String value = (String) getObjectAssertNotNull(jsonRequest, path);
		Assert.assertTrue(path + " cannot be blank.", !value.trim().isEmpty());
		return value;
	}

	public static final String getStringAssertIsBlank(String jsonRequest, String path) {
		String value = (String) getObjectAssertNotNull(jsonRequest, path);
		Assert.assertTrue(path + " cannot have a value.", value.trim().isEmpty());
		return value;
	}

	public static final void assertObjectDoesNotExist(String jsonRequest, String path) {
		Object value = JsonPath.with(jsonRequest).get(path);
		Assert.assertNull("json should not contain: " + path + ".", value);
	}

}

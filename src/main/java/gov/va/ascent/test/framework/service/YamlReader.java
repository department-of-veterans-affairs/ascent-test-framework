package gov.va.ascent.test.framework.service;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlReader {

	private YamlReader() {

	}

	@SuppressWarnings("unchecked")
	public static String getProperty(String yamlStr, String yamlProperty) {

		Yaml yaml = new Yaml();
		Iterable<Object> itr = yaml.loadAll(yamlStr);
		Map<String, Object> container = new HashMap<>();
		for (Object o : itr) {
			Map<String, Object> map = (Map<String, Object>) o;
			container.putAll(map);
		}
		return getMapValue(container, yamlProperty);

	}

	private static String getMapValue(Map<String, Object> object, String token) {

		String[] st = token.split("\\.");
		Map<String, Object> object1 = parseMap(object, token);
		return object1.get(st[st.length - 1]).toString();
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> parseMap(Map<String, Object> object, String token) {
		Map<String, Object> result = object;
		if (token.contains(".")) {
			String[] st = token.split("\\.");
			result = parseMap((Map<String, Object>) object.get(st[0]), token.replace(st[0] + ".", ""));
		}
		return result;
	}

}

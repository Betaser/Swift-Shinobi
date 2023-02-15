package miscObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Json extends LinkedHashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	
	public Json() {
		
	}
	
	@SafeVarargs
	public Json(Map.Entry<String, Object>... attrs) {
		super(Arrays.stream(attrs).collect(Collectors.toMap(
			Map.Entry::getKey,
			Map.Entry::getValue,
			(e, e2) -> e,
			LinkedHashMap<String, Object>::new
		)));
	}
	
	public Json(Map<? extends String, ?> map) {
		super(map);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key) {
		return (List<T>) get(key);
	}
	
	public String getString(String key) {
		return (String) get(key);
	}
	
	public double getDouble(String key) {
		Object val = get(key);
		if (val instanceof Double)
			return ((Double) val).doubleValue();
		return Double.parseDouble((String) val);
	}
	
	public String formatted() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		formatted(sb, 1, entrySet());
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\n}");
		return sb.toString();
	}
	
	private Set<Map.Entry<String, Object>> mapToSet(Map<?, ?> map) {
		Json json = new Json(map.entrySet().stream()
			.collect(Collectors.toMap(
				e -> e.getKey().toString(),
				Map.Entry::getValue,
				(e, e2) -> e,
				LinkedHashMap<String, Object>::new
			))
		);
		
		return json.entrySet();
	}
	
	private void formattedList(StringBuilder sb, int depth, List<Object> elems) {
		for (Object elem : elems) {
			sb.append(Stream.generate(() -> "  ")
				.limit(depth)
				.collect(Collectors.joining()));
			
			if (elem instanceof Map<?, ?>) {
				sb.append("{\n");
				formatted(sb, depth + 1, mapToSet((Map<?, ?>) elem));
				putSpaces(sb, depth);
				sb.append('}');
			} else if (elem instanceof List<?>) {
				sb.append("[\n");
				formattedList(sb, depth + 1, new ArrayList<>((List<?>) elem));
				putSpaces(sb, depth);
				sb.append(']');
			} else
				sb.append(elem);
			
			sb.append(",\n");
		}
		
		if (elems.size() > 0)
			sb.deleteCharAt(sb.length() - 2);
	}
	
	private void putSpaces(StringBuilder sb, int depth) {
		sb.append(Stream.generate(() -> "  ")
			.limit(depth)
			.collect(Collectors.joining()));
	}
	
	private void formatted(StringBuilder sb, int depth, Set<Map.Entry<String, Object>> attrs) {
		for (Map.Entry<String, Object> attr : attrs) {
			sb.append(Stream.generate(() -> "  ")
				.limit(depth)
				.collect(Collectors.joining()));
			sb.append("\"" + attr.getKey() + "\": ");
			
			if (attr.getValue() instanceof Map<?, ?>) {
				sb.append("{\n");
				formatted(sb, depth + 1, mapToSet((Map<?, ?>) attr.getValue()));
				putSpaces(sb, depth);
				sb.append('}');
			} else if (attr.getValue() instanceof List<?>) {
				sb.append("[\n");
				formattedList(sb, depth + 1, new ArrayList<>((List<?>) attr.getValue()));
				putSpaces(sb, depth);
				sb.append(']');
			} else
				sb.append(attr.getValue());
			
			sb.append(",\n");
		}
		
		if (attrs.size() > 0)
			sb.deleteCharAt(sb.length() - 2);
	}
	
	public String toString() {
		if (size() == 0)
			return "{}";
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (Map.Entry<? extends String, ?> entry : entrySet()) {
			sb.append("\"" + entry.getKey() + "\"");
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}

}

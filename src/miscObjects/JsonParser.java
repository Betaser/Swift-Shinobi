package miscObjects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import entity.Entity;
import helper.Utils;
import state.levelDesignState.io.SaveLevel;

public class JsonParser {
	
	public static void main(String[] args) {
		File file = new File("res/levels/defLevel.json");
		List<Entity> entities = SaveLevel.mockEntities();
		SaveLevel.save(file, entities);
		
		JsonParser jsonParser = new JsonParser();
		
//		Noted<LoadErr, Json> loaded = jsonParser.load(file);
//		System.out.println(loaded.enumVal);
//		System.out.println(loaded.val.formatted());
		
//		File simple = new File("res/levels/simple.json");
//		Noted<LoadErr, Json> loadedSimple = jsonParser.load(simple);
//		System.out.println(loadedSimple.enumVal);
//		System.out.println(loadedSimple.val.formatted());
		
		File lists = new File("res/levels/lists.json");
		Noted<LoadErr, Json> loadedLists = jsonParser.load(lists);
		System.out.println(loadedLists.enumVal);
		System.out.println(loadedLists.val.formatted());
	}
	
	private final static List<String> KEY_TOKENS = List.of("\"", "\"", ":");
	
	public enum LoadErr {
		OK,
		IO,
		CURLY_BRACE,
		SQUARE_BRACKET,
		MAP_ENTRY,
		KEY,
		VALUE,
		ELEM,
		OTHER_TOKENS
	}
	
	static class TokenAt {
		int position;
		String token;
		TokenAt(int position, String token) {
			this.position = position;
			this.token = token;
		}
		int getPosition() {
			return position;
		}
		String getToken() {
			return token;
		}
		public String toString() {
			return "@" + position + " = " + token;
		}
	}
	
	static class JsonAt extends TokenAt {
		Json json;
		JsonAt(int position, Json json) {
			super(position, "");
			this.json = json;
		}
	}
	
	static class ListAt extends TokenAt {
		List<Object> list;
		ListAt(int position, List<Object> list) {
			super(position, "");
			this.list = list;
		}
	}
	
	Json json;
	List<Object> list;
	int tokenAtsI;

	private Noted<LoadErr, Object> parseLiteral4Map(String fileData, List<TokenAt> entryTokens) {
		int colonPos = entryTokens.get(2).position;
		int endOfEntry = entryTokens.get(3).position;
		
		String data = fileData.substring(colonPos + 1, endOfEntry).strip();
		
		if (Utils.isNum(data))
			return new Noted<>(LoadErr.OK, Double.parseDouble(data));
		
		switch (data) {
			case "true": {
				return new Noted<>(LoadErr.OK, true);
			}
			case "false": {
				return new Noted<>(LoadErr.OK, false);
			}
			case "null": {
				return new Noted<>(LoadErr.OK, null);
			}
		}
		
		return new Noted<>(LoadErr.VALUE, null);
	}
	
	private Noted<LoadErr, Object> parseLiteral4List(int prevTokenPos, String fileData, List<TokenAt> entryTokens) {
		int endOfEntry = entryTokens.get(0).position;
		
		String data = fileData.substring(prevTokenPos, endOfEntry);
		
		switch (data.strip()) {
			case "true": {
				return new Noted<>(LoadErr.OK, true);
			}
			case "false": {
				return new Noted<>(LoadErr.OK, false);
			}
			case "null": {
				return new Noted<>(LoadErr.OK, null);
			}
		}
		
		return new Noted<>(LoadErr.VALUE, null);
	}
	
	private String parseKey(String fileData, List<String> entries, List<TokenAt> entryTokens) {
		if (entries.size() < 4 || !entries.subList(0, 3).equals(KEY_TOKENS))
			return null;
		
		int quoStart = entryTokens.get(0).position;
		int quoEnd = entryTokens.get(1).position;
		
		return fileData.substring(quoStart + 1, quoEnd);
	}
	
	private String parseString4Map(String fileData, List<TokenAt> entryTokens) {
		return parseString(3, 4, fileData, entryTokens);
	}
	
	private String parseString4List(String fileData, List<TokenAt> entryTokens) {
		return parseString(0, 1, fileData, entryTokens);
	}
	
	private String parseString(int startIdx, int endIdx, String fileData, List<TokenAt> entryTokens) {
		int quoStart = entryTokens.get(startIdx).position;
		int quoEnd = entryTokens.get(endIdx).position;
		
		return fileData.substring(quoStart, quoEnd + 1);
	}
	
	private List<Object> parseList4Map(List<TokenAt> entryTokens) {
		return ((ListAt) entryTokens.get(3)).list;
	}
	
	private List<Object> parseList4List(List<TokenAt> entryTokens) {
		return ((ListAt) entryTokens.get(0)).list;
	}
	
	private Json parseJson4Map(List<TokenAt> entryTokens) {
		return ((JsonAt) entryTokens.get(3)).json;
	}
	
	private Json parseJson4List(List<TokenAt> entryTokens) {
		return ((JsonAt) entryTokens.get(0)).json;
	}
	
	private Integer findFirstLeftPos(int idx, String token, List<TokenAt> tokenAts) {
		for (int i = idx; i >= 0; i--) {
			TokenAt tokenAt = tokenAts.get(i);
			if (tokenAt.token.equals(token))
				return i;
		}
		return null;
	}
	
	private int correctIdx(int idx, int leftIdx, int rightIdx) {
		return idx - (rightIdx - leftIdx);
	}
	
	private void replaceMapTokens(int leftIdx, int rightIdx, List<TokenAt> tokenAts) {
		int position = tokenAts.get(leftIdx).position;
		
		for (int i = leftIdx; i < rightIdx; i++)
			tokenAts.remove(leftIdx);
		
		JsonAt mapReplacement = new JsonAt(position, (Json) json.clone());
		tokenAts.add(leftIdx, mapReplacement);
		json.clear();
	}
	
	private void replaceListTokens(int leftIdx, int rightIdx, List<TokenAt> tokenAts) {
		int position = tokenAts.get(leftIdx).position;

		for (int i = leftIdx; i < rightIdx; i++)
			tokenAts.remove(leftIdx);
		
		ListAt listReplacement = new ListAt(position, new ArrayList<>(list));
		tokenAts.add(leftIdx, listReplacement);
		list.clear();
	}
	
	private LoadErr checkMapContents(int idx, String fileData, List<TokenAt> tokenAts) {
		if (tokenAts.get(idx - 1).token.equals("{")) {
			replaceMapTokens(idx - 1, idx + 1, tokenAts);
			return LoadErr.OK;
		}

		Integer LEFT_TOKEN_POS = findFirstLeftPos(idx, "{", tokenAts);
		if (LEFT_TOKEN_POS == null)
			return LoadErr.MAP_ENTRY;
		
		List<TokenAt> entryTokens = new ArrayList<>();
		
		for (int i = LEFT_TOKEN_POS + 1; i <= idx; i++) {
			TokenAt tokenAt = tokenAts.get(i);
			entryTokens.add(tokenAt);
			switch (tokenAt.token) {
				case ",", "}": {
					//	Fill this map with the correct map entries
					List<String> ets = entryTokens.stream().map(t -> t.token).collect(Collectors.toList());
					String key = parseKey(fileData, ets, entryTokens);
					if (key == null)
						return LoadErr.MAP_ENTRY;

					if (ets.size() == 4 && ets.subList(0, 3).equals(KEY_TOKENS)) {
						//	Numerical or boolean or null value
						Noted<LoadErr, Object> value = parseLiteral4Map(fileData, entryTokens);
						if (value.enumVal != LoadErr.OK)
							return LoadErr.VALUE;
						
						json.put(key, value.val);
					} else if (entryTokens.size() == 5 && entryTokens.get(3) instanceof JsonAt) {
						//	Map value
						Json value = parseJson4Map(entryTokens);
						
						json.put(key, value);
					} else if (entryTokens.size() == 5 && entryTokens.get(3) instanceof ListAt) {
						//	List value
						List<Object> value = parseList4Map(entryTokens);
						
						json.put(key, value);
					} else if (ets.size() == 6 && ets.subList(0, 5).equals(List.of("\"", "\"", ":", "\"", "\""))) {
						//	String value
						String value = parseString4Map(fileData, entryTokens);
						
						json.put(key, value);
					} else {
						return LoadErr.MAP_ENTRY;
					}
					
					if (tokenAt.token.equals("}")) {
						replaceMapTokens(LEFT_TOKEN_POS, idx + 1, tokenAts);
						tokenAtsI = correctIdx(i, LEFT_TOKEN_POS, idx);
						
						return LoadErr.OK;
					}
					
					entryTokens.clear();
					
					break;
				}
			}
		}
		
		return LoadErr.OK;
	}
	
	private LoadErr checkListContents(int idx, String fileData, List<TokenAt> tokenAts) {
		if (tokenAts.get(idx - 1).token.equals("[")) {
			replaceListTokens(idx - 1, idx + 1, tokenAts);
			return LoadErr.OK;
		}
		
		Integer LEFT_TOKEN_POS = findFirstLeftPos(idx, "[", tokenAts);
		if (LEFT_TOKEN_POS == null)
			return LoadErr.ELEM;
		
		List<TokenAt> elemTokens = new ArrayList<>();
		
		for (int i = LEFT_TOKEN_POS + 1; i <= idx; i++) {
			TokenAt tokenAt = tokenAts.get(i);
			elemTokens.add(tokenAt);
			switch (tokenAt.token) {
				case ",", "]": {
					//	Fill this map with the correct list elements
					List<String> ets = elemTokens.stream().map(t -> t.token).collect(Collectors.toList());
					
					if (elemTokens.size() == 1 && ets.get(0).equals(",")) {
						//	Numerical or boolean or null element
						Noted<LoadErr, Object> elem = parseLiteral4List(tokenAts.get(i - 1).position, fileData, elemTokens);
						if (elem.enumVal != LoadErr.OK)
							return LoadErr.ELEM;
						
						list.add(elem.val);
					} else if (elemTokens.size() == 2 && elemTokens.get(0) instanceof JsonAt) {
						//	Map element
						Json elem = parseJson4List(elemTokens);
						
						list.add(elem);
					} else if (elemTokens.size() == 2 && elemTokens.get(0) instanceof ListAt) {
						//	List element
						List<Object> elem = parseList4List(elemTokens);
						
						list.add(elem);
					} else if (elemTokens.size() == 3 && ets.subList(0, 2).equals(List.of("\"", "\""))) {
						//	String element
						String elem = parseString4List(fileData, elemTokens);
						
						list.add(elem);
					} else {
						return LoadErr.ELEM;
					}
					
					if (tokenAt.token.equals("]")) {
						replaceListTokens(LEFT_TOKEN_POS, idx + 1, tokenAts);
						tokenAtsI = correctIdx(i, LEFT_TOKEN_POS, idx);

						return LoadErr.OK;
					}
					
					elemTokens.clear();
					
					break;
				}
			}
		}
		
		return LoadErr.OK;
	}
	
	private Noted<LoadErr, Json> load(String fileData, List<TokenAt> tokenAts) {
		boolean inString = false;

		for (tokenAtsI = 0; tokenAtsI < tokenAts.size(); tokenAtsI++) {
			final String TOKEN = tokenAts.get(tokenAtsI).token;
			
			if (inString) {
				if (TOKEN.equals("\""))
					inString = false;

				continue;
			}

			switch (TOKEN) {
				case "}": {
//					System.out.println(IntStream.range(0, tokenAts.size())
//						.boxed()
//						.map(i -> new Tuple2<>(i, tokenAts.get(i)))
//						.collect(Collectors.toList()));
//					System.out.println(tokenAtsI);
					LoadErr err = checkMapContents(tokenAtsI, fileData, tokenAts);
					if (err != LoadErr.OK)
						return new Noted<>(err, json);
					
					break;
				}
				case "]": {
					LoadErr err = checkListContents(tokenAtsI, fileData, tokenAts);
					if (err != LoadErr.OK)
						return new Noted<>(err, json);
					
					break;
				}
				case "\"": {
					inString = !inString;
					break;
				}
			}
		}
		
		if (tokenAts.size() != 1 || !(tokenAts.get(0) instanceof JsonAt))
			return new Noted<>(LoadErr.OTHER_TOKENS, json);
		
		json = ((JsonAt) tokenAts.get(0)).json;
		return new Noted<>(LoadErr.OK, json);
	}
	
	public Noted<LoadErr, Json> load(File file) {
		String fileData;
		try {
			fileData = Files.readAllLines(file.toPath()).stream()
				.map(s -> s + "\n")
				.collect(Collectors.joining());
			json = new Json();
			list = new ArrayList<>();
			
			List<TokenAt> noteableTokens = new ArrayList<>();
			List<String> TOKENS = List.of("{", "}", ":", ",", "[", "]", "\"");

			for (String TOKEN : TOKENS) {
				String data = fileData;
				int idx;
				int cutOff = 0;
				while ((idx = data.indexOf(TOKEN)) != -1) {
					noteableTokens.add(new TokenAt(cutOff + idx,
						data.substring(idx, idx + TOKEN.length())));
					data = data.substring(idx + 1);
					cutOff += idx + 1;
				}
			}
			
			List<TokenAt> orderedTokens = new ArrayList<>(noteableTokens);
			orderedTokens.sort(Comparator.comparing(TokenAt::getPosition));
			
			return load(fileData, orderedTokens);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Noted<>(LoadErr.IO, json);
	}

}

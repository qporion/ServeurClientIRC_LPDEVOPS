package client.views.components;

import java.util.HashMap;
import java.util.Map;

public class SmileyList {

	static final private String[] CODES = {
			"1f604",
			"1f60f",
			"1f606",
			"1f612",
			"1f627",
			"1f609",
			"1f622",
			"1f618",
			"1f61c",
			"1f623",
			"1f499",
			"1f611",
			"1f607",
			"1f608",
			"1f60a"
			};
	static final private String[] TRIGGERS = {
			"^_^",
			":/",
			">:o",
			":o",
			":(",
			";)",
			":'(",
			":*",
			":p",
			">:(",
			"<3",
			"-_-",
			"O:)",
			"3:)",
			":)"
			};
	
	static public Map<String,String> getMap() {
		
		Map<String,String> map = new HashMap<>();
		
		for(int i=0; i< CODES.length && i< TRIGGERS.length; i++) {
			map.put(TRIGGERS[i], CODES[i]);
		}
		
		return map;
	}
}

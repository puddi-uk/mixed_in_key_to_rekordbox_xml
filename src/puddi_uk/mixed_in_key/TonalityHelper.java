package puddi_uk.mixed_in_key;

import java.util.HashMap;
import java.util.Map;

public class TonalityHelper {

	private static final Map<String, String>	camelotToclassical	= new HashMap<String, String>();
	private static final Map<String, String>	classicalToCamelot	= new HashMap<String, String>();

	static {
		// @formatter:off
		camelotToclassical.put("1A",  "G♯m");
		camelotToclassical.put("1B",  "B");
		camelotToclassical.put("2A",  "D♯m");
		camelotToclassical.put("2B",  "F♯");
		camelotToclassical.put("3A",  "A♯m");
		camelotToclassical.put("3B",  "C♯");
		camelotToclassical.put("4A",  "Fm");
		camelotToclassical.put("4B",  "G♯");
		camelotToclassical.put("5A" , "Cm");
		camelotToclassical.put("5B",  "D♯");
		camelotToclassical.put("6A",  "Gm");
		camelotToclassical.put("6B",  "A♯");
		camelotToclassical.put("7A",  "Dm");
		camelotToclassical.put("7B",  "F");
		camelotToclassical.put("8A",  "Am");
		camelotToclassical.put("8B",  "C");
		camelotToclassical.put("9A",  "Em");
		camelotToclassical.put("9B",  "G");
		camelotToclassical.put("10A", "Bm");
		camelotToclassical.put("10B", "D");
		camelotToclassical.put("11A", "F♯m");
		camelotToclassical.put("11B", "A");
		camelotToclassical.put("12A", "C♯m");
		camelotToclassical.put("12B", "E");
		// @formatter:on
	}

	static {
		// @formatter:off
		classicalToCamelot.put("G♯m", "1A");
		classicalToCamelot.put("B",   "1B");
		classicalToCamelot.put("D♯m", "2A");
		classicalToCamelot.put("F♯",  "2B");
		classicalToCamelot.put("A♯m", "3A");
		classicalToCamelot.put("C♯",  "3B");
		classicalToCamelot.put("Fm",  "4A");
		classicalToCamelot.put("G♯",  "4B");
		classicalToCamelot.put("Cm",  "5A");
		classicalToCamelot.put("D♯",  "5B");
		classicalToCamelot.put("Gm",  "6A");
		classicalToCamelot.put("A♯",  "6B");
		classicalToCamelot.put("Dm",  "7A");
		classicalToCamelot.put("F",   "7B");
		classicalToCamelot.put("Am",  "8A");
		classicalToCamelot.put("C",   "8B");
		classicalToCamelot.put("Em",  "9A");
		classicalToCamelot.put("G",   "9B");
		classicalToCamelot.put("Bm",  "10A");
		classicalToCamelot.put("D",   "10B");
		classicalToCamelot.put("F♯m", "11A");
		classicalToCamelot.put("A",   "11B");
		classicalToCamelot.put("C♯m", "12A");
		classicalToCamelot.put("E",   "12B");
		// @formatter:on
	}

	private static String camelotToclassical(String camelot) {
		return camelotToclassical.get(camelot);
	}

	private static String classicalToCamelot(String classical) {
		return classicalToCamelot.get(classical);
	}
//
//	public static String tonalityToCamelot(String tonality) {
//		return camelotToclassical.containsKey(tonality) ? tonality : classicalToCamelot(tonality);
//	}
//
//	public static String tonalityToclassical(String tonality) {
//		return classicalToCamelot.containsKey(tonality) ? tonality : camelotToclassical.get(tonality);
//	}

	public static enum ScaleFormat {
		CAMELOT, CLASSICAL
	}

	public static String convertTonality(String tonality, ScaleFormat desiredScaleType) {
		if (desiredScaleType == ScaleFormat.CAMELOT) {
			return camelotToclassical.containsKey(tonality) ? tonality : classicalToCamelot(tonality);
		} else if (desiredScaleType == ScaleFormat.CLASSICAL) {
			return classicalToCamelot.containsKey(tonality) ? tonality : camelotToclassical(tonality);
		} else {
			throw new RuntimeException("Unsupported ScaleType for tonality conversion: " + tonality);
		}
	}
}

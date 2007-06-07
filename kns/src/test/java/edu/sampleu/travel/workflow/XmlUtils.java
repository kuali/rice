package edu.sampleu.travel.workflow;

public class XmlUtils {
	/**
	 * Returns a String containing the given content string enclosed within an
	 * open and close tag having the given tagName.
	 * 
	 * @param tagName
	 * @param content
	 * @return
	 */
	public static String encapsulate(String tagName, String content) {
		return openTag(tagName) + content + closeTag(tagName);
	}

	private static String openTag(String tagName) {
		return "<" + tagName + ">";
	}

	private static String closeTag(String tagName) {
		return "</" + tagName + ">";
	}

}
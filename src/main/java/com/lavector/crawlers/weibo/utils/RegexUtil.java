package com.lavector.crawlers.weibo.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	private static Map<String, Pattern> patternMap = new ConcurrentHashMap<String, Pattern>();
	public static Pattern getPattern(String patternString) {
		Pattern pattern = patternMap.get(patternString);
		if (pattern==null) {
			pattern = Pattern.compile(patternString);
			patternMap.put(patternString, pattern);
		}
		return pattern;
	}

	public static boolean isMatch(String pattern, String text) {
		return Pattern.matches(pattern, text);
	}

	public static boolean isWildCardMatch(String pattern, String text) {
		String localPattern = pattern;
		localPattern = convertFromWildcardPattern(pattern);
		return isMatch(localPattern, text);
	}

	public static boolean matchAny(String[] patterns, String text) {
		for (String pattern : patterns) {
			boolean isMatch = RegexUtil.isMatch(pattern, text);
			if (isMatch) return true;
		}
		return false;
	}

	public static String convertFromWildcardPattern(String wildcardPatternString) {
		String patternString = wildcardPatternString;
		patternString = StringUtils.replace(patternString, "*", ".*");
		patternString = StringUtils.replace(patternString, "?", ".?");
		return patternString;
	}
	
	public static String replaceAll(String input, String search, String replace) {
		String result = null;
		if (input!=null) {
			result = input.replaceAll(search, replace);
		}
		return result;
	}

	public static boolean find(String input, String search) {
		boolean result = false;
		if (input!=null && search!=null) {
			Pattern pattern = getPattern(search);
			Matcher matcher = pattern.matcher(input);
			result = matcher.find(); 
		}
		return result;
	}

	public static List<String> findAllGroups(String input, String search) {
		List<String> groups = new LinkedList<String>();
		if (input!=null && search!=null) {
			Pattern pattern = getPattern(search);
			Matcher matcher = pattern.matcher(input);
			while (matcher.find()) { 
				for (int i=0; i<=matcher.groupCount(); i++) {
					groups.add(matcher.group(i));
				}
			}
		}
		return groups;
	}

	public static String findGroup(String input, String search, int index) {
		String group = null;
		List<String> groups = findAllGroups(input, search);
		if (groups!=null && index<groups.size()) {
			group = groups.get(index);
		}
		return group;
	}

	public static String findFirstGroup(String input, String search) {
		return findGroup(input, search, 1);
	}

	public static Map<String, String> extractVariables(String input, String search, String... variableNames) {
		List<String> groups = findAllGroups(input, search);
		Map<String, String> variablesMap = new HashMap<String, String>();
		for (int i=0; i<variableNames.length; i++) {
			String variableName = variableNames[i];
			String variableValue = groups.get(i+1);
			variablesMap.put(variableName, variableValue);
		}
		return variablesMap;
	}

}

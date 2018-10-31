package edu.nd;

public class StringUtil {
	public static String BlackListFilter(String input) {
		if(input == null) input = "";
		char blank = ' ';
		
		input = input.replace('\\', blank);
		input = input.replace('/', blank);
		input = input.replace('<', blank);
		input = input.replace('>', blank);
		input = input.replace('#', blank);
		input = input.replace('@', blank);
		//input = input.replace('&', blank);
		input = input.replace('^', blank);
		//input = input.replace(';', blank);
		input = input.replace('|', blank);		
		return input;
	}
	
	public static String reverseEscape(String input) {
		if(input == null) input = "";
		
		input = input.replaceAll("&quot;", "\"");
		input = input.replaceAll("&#39;", "\'");
		input = input.replaceAll("&amp;", "&");
		input = input.replaceAll("&lt;", "<");
		input = input.replaceAll("&gt;", ">");
		
		return input;
	}
}

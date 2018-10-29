package edu.nd;

public class StringUtil {
	public static String BlackListFilter(String input) {
		if(input == null) input = "";
		
		input = input.replace('\\', ' ');
		input = input.replace('/', ' ');
		input = input.replace('<', ' ');
		input = input.replace('>', ' ');
		input = input.replace('#', ' ');
		input = input.replace('@', ' ');
		input = input.replace('&', ' ');
		input = input.replace('^', ' ');
		input = input.replace(';', ' ');
		input = input.replace('|', ' ');		
		return input;
	}
}

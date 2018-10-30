package edu.nd;

public class Logger {
	public static int level = 0;
	public static boolean stackTrace = true;

	public static void debug(String input) {
		if( level <= 1) {
			System.out.println(input);
		}
	}
	
	public static void err(String input, Exception ex) {
		if( level <= 2) {
			System.err.println(input);
			if(ex != null && stackTrace)
				ex.printStackTrace();
		}
	}

}

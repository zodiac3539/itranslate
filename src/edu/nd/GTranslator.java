package edu.nd;

import java.nio.charset.Charset;
import java.util.Iterator;

import com.google.cloud.translate.Translate;

import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class GTranslator {
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	
	public static Translate translate = null;
	
	public GTranslator() {
		if(translate == null) {
			translate = TranslateOptions.getDefaultInstance().getService();
		}
	}
	
	public String translate(String jpn) {
		String ret = "";
		Iterator<AmbiguousVO> amb = ImageViewer.ambiguous.iterator();
		while(amb.hasNext()) {
			AmbiguousVO vo = amb.next();
			jpn = jpn.replaceAll( vo.getFrom() , vo.getTo() );
			//System.out.println(vo.getFrom() + " " +vo.getTo());
		}

		Translation translation =
		        translate.translate(
		            jpn,
		            TranslateOption.sourceLanguage("ja"),
		            TranslateOption.targetLanguage("ko"));
		
		ret = new String(translation.getTranslatedText().getBytes(), UTF_8);
		System.out.println("Original: " + new String(jpn.getBytes(), UTF_8));
		System.out.println("Translated: " + ret);
		
		return ret;
	}
	
	
	/*
	public static void main(String[] args) {
	    Translate translate = TranslateOptions.getDefaultInstance().getService();
		   
	    // The text to translate
	    String text = "Hello, world!";

	    // Translates some text into Russian
	    Translation translation =
	        translate.translate(
	            text,
	            TranslateOption.sourceLanguage("en"),
	            TranslateOption.targetLanguage("ko"));


	    System.out.printf("Text: %s%n", text);
	    System.out.printf("Translation: %s%n", new String(translation.getTranslatedText().getBytes(), UTF_8));

	}*/
}

package edu.nd;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class ImgFront {
	
	private static Font myFont;
	
	public static Font getFont(int size) {
    	Map<TextAttribute, Object> attributes = new HashMap<>();

    	attributes.put(TextAttribute.FAMILY, ImageViewer.font_family);
    	attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
    	attributes.put(TextAttribute.SIZE, size);
    	myFont = Font.getFont(attributes);
    	return myFont;
	}
}

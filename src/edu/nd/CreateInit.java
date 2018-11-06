package edu.nd;

public class CreateInit {
	public String retSetting() {
		StringBuffer strb = new StringBuffer();
		strb.append("{ \"zoom\" : 1.0, \n");
		strb.append(" \"tesseract\": \"tesseract.exe\",\n");
		strb.append(" \"option\": \"-l;jpn_vert;--psm;5;--oem;1\",\n");
		strb.append(" \"defaultWidth\" : 1200,\n");
		strb.append(" \"defaultHeight\" : 900,\n");
		strb.append(" \"font_size\": 24,\n");
		strb.append(" \"font_family\" : \"Malgun Gothic\",\n");
		strb.append(" \"lowhsv1\": \"154,20,0\",\n");
		strb.append(" \"highhsv1\": \"255,255,255\",\n");
		strb.append(" \"lowhsv2\": \"0,0,0\",\n");
		strb.append(" \"highhsv2\": \"255,255,255\",\n");
		strb.append(" \"lowhsv3\": \"0,0,0\",\n");
		strb.append(" \"highhsv3\": \"255,255,255\",\n");
		strb.append(" \"kernel_width\": 3,\n");
		strb.append(" \"kernel_height\": 1,\n");
		strb.append(" \"repeat\": 1,\n");
		strb.append(" \"linebreak\": 7,\n");
		strb.append(" \"fontcolor\": \"#000000\",\n");
		strb.append(" \"fromlan\": \"ja\",\n");
		strb.append(" \"tolan\": \"en\"\n");		
		strb.append(" }");
		return strb.toString();
	}
	
	public String retAmb() {
		StringBuffer strb = new StringBuffer();
		strb.append("{    \"list\":[ \n");
		strb.append("     { \"from\" : \"ひまり\", \"to\" : \"Himari\" , \"flag\" : 1}\n");
		strb.append(" ]}");
		return strb.toString();
	}
}

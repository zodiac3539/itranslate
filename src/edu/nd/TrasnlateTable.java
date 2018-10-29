package edu.nd;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.json.JSONArray;
import org.json.JSONObject;


public class TrasnlateTable extends AbstractTableModel {
	private String[] columnNames = {"Num",
            "X",
            "Y",
            "original",
            "translated"};
	private List<TranslateVO> tlist = new LinkedList<TranslateVO>();
	
	public TrasnlateTable() {
		super();
	}
	
	public void addToList(TranslateVO vo) {
		vo.setNum(tlist.size()+1);
		tlist.add(vo);
	}
	
	public TranslateVO getVO(int num) {
		return tlist.get(num);
	}
	
	public void remove(int num) {
		tlist.remove(tlist.get(num));
	}
	
	public String getColumnName(int col) {
        return columnNames[col];
    }
	public String escape (String input) {
		if(input == null) input = "";
		input = input.replaceAll("\"", "");
		input = input.replaceAll("\n", "\\n");

		return input;
	}
	
	public int buildListFromJSON(String filename) {
		tlist = new LinkedList<TranslateVO>();
		
    	File jsonfile = new File(filename);
    	if(!jsonfile.exists()) { 
    		return -1;
    	}
	    JLoader jl = new JLoader();
	    JSONArray letsprintthemall = null;
	    try {
	    	JSONObject jobj = jl.loadJson(filename);
        	letsprintthemall = jobj.getJSONArray("list");
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    	return -1;
	    }
    	Iterator<Object> it = letsprintthemall.iterator();
    	//g2d.setColor(Color.BLACK);
    	int k = 1;
    	while(it.hasNext()) {
    		JSONObject obj = (JSONObject) it.next();
    		String translated = obj.getString("translated");
       		String original = obj.getString("original");
       	 
    		int x = obj.getInt("x");
    		int y = obj.getInt("y");
    		
    		TranslateVO vo = new TranslateVO();
    		
    		vo.setNum(k);
    		vo.setX(x);
    		vo.setY(y);
    		vo.setOriginal(original);
    		vo.setTranslated(translated);
    		if(vo.getX() > 0) tlist.add(vo);
    		k++;
    	}
		return 0;
	}
	public List<TranslateVO> getTranslateList() {
		return this.tlist;
	}
	
	public int save(String filename) {
		//{
		//    "list":[
		//     { "from" : "膝", "to" : "膣" , "flag" : 1},
		// ]
	    //}
				
		if(tlist.isEmpty()) {
			return -2;
		}
		
		StringBuffer strb = new StringBuffer();
		strb.append("{" + "\n");
		strb.append("    \"list\": [" + "\n");
		for( TranslateVO element : tlist ) {
			strb.append("    { " + "\n");
			strb.append("        \"x\" : " + element.getX() + ",\n");
			strb.append("        \"y\" : " + element.getY() + ",\n");
			strb.append("        \"original\" : \""  + escape(element.getOriginal()) + "\", \n");
			strb.append("        \"translated\" : \"" + escape(element.getTranslated()) + "\" \n");
			strb.append("    }, " + "\n");			
		}
		
		strb.append("    { " + "\n");
		strb.append("        \"x\" : -1,"  + "\n");
		strb.append("        \"y\" : -1,"  + "\n");
		strb.append("        \"original\" : \"eod\","  + "\n");
		strb.append("        \"translated\" : \"eod\" "  + "\n");
		strb.append("    } " + "\n");
		
		strb.append("]" + "\n");
		strb.append("}" + "\n");
		
		try {
			File file = new File( filename );
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(strb.toString().getBytes());
			fos.flush();
			fos.close();
			return 0;
		} catch(Exception ex) {
			ex.printStackTrace();
			
		}
		return -1;
	}


	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 5;
	}


	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return tlist.size();
	}


	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		String ret = "";
		if( tlist.size() ==0 ) {
			return "";
		} else {
			switch(column) {
			    case 0:
			    	ret = String.valueOf( row + 1 );
			    	break;
			    case 1:
			    	ret = String.valueOf( tlist.get(row).getX() );
			    	break;
			    case 2:
			    	ret = String.valueOf( tlist.get(row).getY() );
			    	break;
			    case 3:
			    	ret = String.valueOf( tlist.get(row).getOriginal() );
			    	break;
			    case 4:
			    	ret = String.valueOf( tlist.get(row).getTranslated() );
			    	break;
			}
		}
		return ret;
	}
}

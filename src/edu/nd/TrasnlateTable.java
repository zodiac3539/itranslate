package edu.nd;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;

import org.json.JSONArray;
import org.json.JSONObject;


public class TrasnlateTable extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1620446442626241316L;
	private String[] columnNames = {"Num",
            "X",
            "Y",
            "original",
            "translated"};
	private List<TranslateVO> tlist = new LinkedList<TranslateVO>();
	private boolean isChange = false;
	
	public TrasnlateTable() {
		super();
	}
	
	public boolean isChange() {
		return isChange;
	}
	
	public void addToList(TranslateVO vo) {
		vo.setNum(tlist.size()+1);
		tlist.add(vo);
		isChange = true;
	}
	
	public TranslateVO getVO(int num) {
		return tlist.get(num);
	}
	
	public void setValueAt(int num, TranslateVO vo) {
		tlist.set(num, vo);
		isChange = true;
	}
	
	public void editWindow(int num) {
		JFrame jf = new JFrame();
		SubtitleEditor se = new SubtitleEditor(num, tlist.get(num), this, jf);
		jf.setContentPane(se);
		jf.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				jf.dispose();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		jf.setBounds(200, 200, 500, 500);
		jf.setVisible(true);
	}
	
	public void remove(int num) {
		tlist.remove(tlist.get(num));
		isChange = true;
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
		isChange = false;
		tlist = new LinkedList<TranslateVO>();
		
    	File jsonfile = new File(filename);
    	if(!jsonfile.exists()) { 
    		return -2;
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
			isChange = false;
			Logger.debug("Successfully saved: " + filename);
			return 0;
		} catch(Exception ex) {
			Logger.err(ex.getMessage(), ex);
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
	public void copyToClipboard(int row) {
		String myString = tlist.get(row).getOriginal();
		StringSelection stringSelection = new StringSelection(myString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}
	
	public void reposition(int row, int x, int y) {
		int newx = tlist.get(row).getX() + x;
		if (newx < 0) newx = 0;
		tlist.get(row).setX(newx);
		tlist.get(row).setY(tlist.get(row).getY() + y);		
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

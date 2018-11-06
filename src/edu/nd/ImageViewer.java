package edu.nd;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.json.JSONObject;
import org.json.JSONArray;

import javafx.scene.image.ImageView;

public class ImageViewer {
	public static boolean autocontour = false;
	public static String tesseract = "tesseract.exe";
	public static String option = "-l;jpn";
	public static String lowhsv1 = "0,0,0";
	public static String highhsv1 = "255,255,255";
	public static String lowhsv2 = "0,0,0";
	public static String highhsv2 = "255,255,255";
	public static String lowhsv3 = "0,0,0";
	public static String highhsv3 = "255,255,255";
	public static int default_width = 1200;
	public static int default_height = 800;
    public static JFrame f = new JFrame("Imageviewer");
    public static ScrollImage p = null;
    public static double zoom = 1.0;
    public static EnumCollection.SelectionShape defaultShape = EnumCollection.SelectionShape.Box;
    public static EnumCollection.ImageProcess defaultImageProcess = EnumCollection.ImageProcess.Normal;
    public static EnumCollection.SpecialZoom zoom_mode = EnumCollection.SpecialZoom.Normal;
    public static int kernel_width = 2;
    public static int kernel_height = 2;
    public static int image_iterate = 1;
    
    public static int font_size = 0;
    public static String font_family = "Arial";
    
    public static List<AmbiguousVO> ambiguous = new ArrayList<AmbiguousVO>();
    public static String fromlan = "ja";
    public static String tolan = "en";    
    public static int linebreak = 7;
    public static String fontcolor = "#000000";
    public static double scrollRate = 0.1;
    
    public static boolean isAutoTranslate = true;
    
    public static void checkAndLoadAmbiguous() {
		String settingFile = System.getProperty("user.home") + File.separator + "ambiguous.json";
		File file = new File(settingFile);
		if(!file.exists()) {
			try {
				CreateInit initSetting = new CreateInit();
				
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(initSetting.retAmb().getBytes());
				fos.flush();
				fos.close();
			} catch (Exception _e) {
				_e.printStackTrace();
			}
			
		}
		try {
			JLoader jl = new JLoader();
			JSONObject jobj = jl.loadJson(settingFile);
			JSONArray ar = jobj.getJSONArray("list");
			
			Iterator<Object> it = ar.iterator();
			while(it.hasNext()) {
				JSONObject jo = (JSONObject) it.next();
				String from = jo.getString("from");
				String to = jo.getString("to");
				AmbiguousVO vo = new AmbiguousVO();
				vo.setFrom(from);
				vo.setTo(to);
				//System.out.println(from);
				ambiguous.add(vo);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
    	
    }
    
    public static Color getFontColor() 
    {
    	String hex = fontcolor;
        hex = hex.replace("#", "");
        switch (hex.length()) {
            case 6:
                return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
            case 8:
                return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16),
                Integer.valueOf(hex.substring(6, 8), 16));
        }
        return null;
    }
    
	public static void checkAndLoadInit() {
		String settingFile = System.getProperty("user.home") + File.separator + "Setting.json";
		File file = new File(settingFile);
		if(!file.exists()) {
			try {
				CreateInit initSetting = new CreateInit();
				
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(initSetting.retSetting().getBytes());
				fos.flush();
				fos.close();

			} catch (Exception _e) {
				_e.printStackTrace();
			}
			
		}
		try {
			JLoader jl = new JLoader();
			JSONObject jobj = jl.loadJson(settingFile);
			tesseract = jobj.getString("tesseract");
			option = jobj.getString("option");
			default_width = jobj.getInt("defaultWidth");
			default_height = jobj.getInt("defaultHeight");
			lowhsv1 = jobj.getString("lowhsv1");
			lowhsv2 = jobj.getString("lowhsv2");
			lowhsv3 = jobj.getString("lowhsv3");
			highhsv1 = jobj.getString("highhsv1");
			highhsv2 = jobj.getString("highhsv2");
			highhsv3 = jobj.getString("highhsv3");
			font_size = jobj.getInt("font_size");
			font_family = jobj.getString("font_family");
			kernel_width = jobj.getInt("kernel_width");
			kernel_height = jobj.getInt("kernel_height");
			image_iterate = jobj.getInt("repeat");

			fromlan = jobj.getString("fromlan");
			tolan = jobj.getString("tolan");
			linebreak = jobj.getInt("linebreak");
			fontcolor = jobj.getString("fontcolor");
		} catch(Exception ex) {
			
			System.err.println("====== Problem in reading setting file! ======");
			System.err.println("====== Please check Settings.json in your home directory! ======");			
			ex.printStackTrace();
		}
	}
    public static void main( String[] args )
    {
    	checkAndLoadInit();
    	checkAndLoadAmbiguous();
    	try {
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    		
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
        
        javax.swing.JFrame frame = new javax.swing.JFrame( "FileDrop" );
        //javax.swing.border.TitledBorder dragBorder = new javax.swing.border.TitledBorder( "Drop 'em" );
        final javax.swing.JTextArea text = new javax.swing.JTextArea();
        frame.getContentPane().add( 
            new javax.swing.JScrollPane( text ), 
            java.awt.BorderLayout.CENTER );
        text.append("Please drag and drop the file:\n");
        
        new FileDrop( System.out, text, /*dragBorder,*/ new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   
        	    for( int i = 0; i < files.length; i++ )
                {   
        	    	try
                    {   
        	    		text.append( files[i].getCanonicalPath() + "\n" );
        	    		p = new ScrollImage(files[i].getCanonicalPath());
        	    		//JFrame f = new JFrame("Imageviewer");
                        f.setContentPane(p);
                      
                        JMenuBar menubar = new JMenuBar();
                        
                        //menubar.setFont(ImgFront.getFont(15));
                        JMenu menu0 = new JMenu("File");
                        JMenuItem next_file = new JMenuItem("Next file");
                        next_file.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
                        next_file.addActionListener(p);
                        JMenuItem previous_file = new JMenuItem("Previous file");
                        previous_file.addActionListener(p);
                        
                        JMenu menu = new JMenu("Zoom");
                        JMenuItem full_zoom = new JMenuItem("Normal zoom");
                        full_zoom.addActionListener(p);
                        JMenuItem width_zoom = new JMenuItem("Always width fit");
                        width_zoom.addActionListener(p);
                        JMenuItem height_zoom = new JMenuItem("Always height fit");
                        height_zoom.addActionListener(p);
                        
                        JMenuItem zoomin = new JMenuItem("Zoom in");
                        zoomin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0));
                        zoomin.addActionListener(p);
                        
                        JMenuItem zoomout = new JMenuItem("Zoom out");
                        zoomout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0));
                        zoomout.addActionListener(p);

                        JMenu menu2 = new JMenu("Selection");
                        JMenuItem box = new JMenuItem("Box");
                        box.addActionListener(p);
                        box.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
                        JMenuItem circle = new JMenuItem("Circle");
                        circle.addActionListener(p);
                        circle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

                        //Rounded rectangle
                        JMenuItem rr = new JMenuItem("Rounded rectangle");
                        rr.addActionListener(p);
                        rr.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
                        
                        JMenuItem leftex = new JMenuItem("Expand left");
                        leftex.addActionListener(p);
                        leftex.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0));
                        JMenuItem rightex = new JMenuItem("Expand right");
                        rightex.addActionListener(p);     
                        rightex.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0));
                        JMenuItem upex = new JMenuItem("Expand up");
                        upex.addActionListener(p);     
                        upex.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0));
                        JMenuItem downex = new JMenuItem("Expand down");
                        downex.addActionListener(p);     
                        downex.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
                        JMenuItem lefts = new JMenuItem("Shrink left");
                        lefts.addActionListener(p);
                        lefts.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, ActionEvent.SHIFT_MASK));
                        JMenuItem rights = new JMenuItem("Shrink right");
                        rights.addActionListener(p);     
                        rights.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, ActionEvent.SHIFT_MASK));
                        JMenuItem ups = new JMenuItem("Shrink up");
                        ups.addActionListener(p);
                        ups.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.SHIFT_MASK));
                        JMenuItem downs = new JMenuItem("Shrink down");
                        downs.addActionListener(p);     
                        downs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.SHIFT_MASK));

                        
                        JMenuItem autobound = new JMenuItem("Find contour");
                        autobound.addActionListener(p);     
                        autobound.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));

                        JMenuItem cselection = new JMenuItem("Cancel selection");
                        cselection.addActionListener(p);     
                        cselection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));

                        MenuSelection imp = new MenuSelection();
                        ButtonGroup directionGroup1 = new ButtonGroup();
                        JRadioButtonMenuItem autoon = new JRadioButtonMenuItem("Auto find contour on");
                        autoon.addActionListener(imp);     
                        JRadioButtonMenuItem autooff = new JRadioButtonMenuItem("Auto find contour off", true);
                        autooff.addActionListener(imp);     
                        directionGroup1.add(autoon);
                        directionGroup1.add(autooff);

                        JMenu menu3 = new JMenu("Image process");
                        ButtonGroup directionGroup = new ButtonGroup();
                      
                        JRadioButtonMenuItem i_normal = new JRadioButtonMenuItem("Normal image process", true);
                        i_normal.addActionListener(imp);
                        
                        JRadioButtonMenuItem i_dialate = new JRadioButtonMenuItem("Dialate image process");
                        i_dialate.addActionListener(imp);
                        JRadioButtonMenuItem i_erode = new JRadioButtonMenuItem("Erode image process");
                        i_erode.addActionListener(imp);
                        directionGroup.add(i_normal);
                        directionGroup.add(i_dialate);
                        directionGroup.add(i_erode);

                        JMenu menu4 = new JMenu("OCR");
                        JMenuItem justocr = new JMenuItem("OCR");
                        justocr.addActionListener(p);     
                        justocr.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0));
                        JMenuItem otsu = new JMenuItem("OCR with Ostu");
                        otsu.addActionListener(p);     
                        otsu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
                        JMenuItem otsui = new JMenuItem("OCR with Ostu Inverse");
                        otsui.addActionListener(p);     
                        otsui.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0));
                        JMenuItem hsv1 = new JMenuItem("OCR with HSV filter1");
                        hsv1.addActionListener(p);     
                        hsv1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0));
                        JMenuItem hsv2 = new JMenuItem("OCR with HSV filter2");
                        hsv2.addActionListener(p);     
                        hsv2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
                        JMenuItem hsv3 = new JMenuItem("OCR with HSV filter3");
                        hsv3.addActionListener(p);     
                        hsv3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0));
                        JMenuItem testhsv = new JMenuItem("Test HSV");
                        testhsv.addActionListener(p);    
                        
                        JMenu menu5 = new JMenu("Subtitle");
                        JMenuItem showsubtitle = new JMenuItem("Show subtitle");
                        showsubtitle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
                        showsubtitle.addActionListener(p);
                        JMenuItem hidesubtitle = new JMenuItem("Hide subtitle");
                        hidesubtitle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
                        hidesubtitle.addActionListener(p);
                        JMenuItem savesubtitle = new JMenuItem("Save subtitle");
                        savesubtitle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
                        savesubtitle.addActionListener(p);

                        ButtonGroup translateGroup = new ButtonGroup();
                        JRadioButtonMenuItem autotranslateon = new JRadioButtonMenuItem("Auto translate on", true);
                        autotranslateon.addActionListener(imp);
                        JRadioButtonMenuItem autotranslateoff = new JRadioButtonMenuItem("Auto translate off");
                        autotranslateoff.addActionListener(imp);
                        translateGroup.add(autotranslateon);
                        translateGroup.add(autotranslateoff);                        
                        
                        JMenu menu6 = new JMenu("Scroll");
                        JMenuItem moveup = new JMenuItem("Move up");
                        moveup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
                        moveup.addActionListener(p);
                        JMenuItem movedown = new JMenuItem("Move down");
                        movedown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
                        movedown.addActionListener(p);
                        
                        menu0.add(next_file);
                        menu0.add(previous_file);
                        
                        menu.add(full_zoom);
                        menu.add(width_zoom);
                        menu.add(height_zoom);                        
                        menu.addSeparator();
                        menu.add(zoomin);
                        menu.add(zoomout);
                        
                        menu2.add(box);
                        menu2.add(circle);
                        menu2.add(rr);
                        menu2.addSeparator();
                        menu2.add(leftex);
                        menu2.add(rightex);
                        menu2.add(upex);
                        menu2.add(downex);
                        menu2.addSeparator();
                        menu2.add(lefts);
                        menu2.add(rights);   
                        menu2.add(ups);   
                        menu2.add(downs);   
                        
                        menu2.addSeparator();
                        menu2.add(autobound);
                        menu2.addSeparator();
                        menu2.add(autoon);
                        menu2.add(autooff);
                        menu2.addSeparator();
                        menu2.add(cselection);
                        
                        menu3.add(i_normal);
                        menu3.add(i_dialate);
                        menu3.add(i_erode);
                        
                        menu4.add(justocr);
                        menu4.add(otsu);
                        menu4.add(otsui);
                        menu4.addSeparator();
                        menu4.add(hsv1);
                        menu4.add(hsv2);
                        menu4.add(hsv3);
                        menu4.add(testhsv);
                        
                        menu5.add(showsubtitle);
                        menu5.add(hidesubtitle);
                        menu5.add(savesubtitle);
                        menu5.addSeparator();
                        menu5.add(autotranslateon);
                        menu5.add(autotranslateoff);                        
                        
                        menu6.add(moveup);
                        menu6.add(movedown);
                        
                        menubar.add(menu0);
                        menubar.add(menu);
                        menubar.add(menu2);
                        menubar.add(menu3);
                        menubar.add(menu4);
                        menubar.add(menu5);                        
                        menubar.add(menu6); 
                        
                        f.setJMenuBar(menubar);

                        f.setSize(default_width, default_height);
                        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        f.setVisible(true);

                    }   // end try
                    catch( java.io.IOException e ) {}
                }   // end for: through each dropped file
            }   // end filesDropped
        }); // end FileDrop.Listener
        
        frame.setBounds( 100, 100, 500, 500 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible(true);
    }   // end main
    public static void setTitle(String input) {
    	f.setTitle(input);
    }
    public static int getMainFrameWidth() {
    	int frm = 0;
    	
    	frm = f.getWidth();
    	
    	return frm;
    }

    public static int getMainFrameHeight() {
    	int frm = 0;
    	
    	frm = f.getHeight();
    	
    	return frm;
    }

}

package edu.nd;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
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
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.json.JSONObject;
import org.json.JSONArray;

import javafx.scene.image.ImageView;

public class ImageViewer {
	public static boolean autocontour = false;
	public static String tesseract = "";
	public static String option = "";
	public static String lowhsv1 = "";
	public static String highhsv1 = "";
	public static String lowhsv2 = "";
	public static String highhsv2 = "";
	public static String lowhsv3 = "";
	public static String highhsv3 = "";
	public static int default_width = 0;
	public static int default_height = 0;
    public static JFrame f = new JFrame("Imageviewer");
    public static ScrollImage p = null;
    public static double zoom = 1.0;
    public static EnumCollection.SelectionShape defaultShape = EnumCollection.SelectionShape.Box;
    public static EnumCollection.ImageProcess defaultImageProcess = EnumCollection.ImageProcess.Normal;
    public static int kernel_width = 2;
    public static int kernel_height = 2;
    public static int image_iterate = 1;
    
    public static int font_size = 0;
    public static String font_family = "";
    
    public static List<AmbiguousVO> ambiguous = new ArrayList<AmbiguousVO>();
   
    public static void checkAndLoadAmbiguous() {
		String settingFile = System.getProperty("user.home") + File.separator + "ambiguous.json";
		File file = new File(settingFile);
		if(!file.exists()) {
			try {
				InputStream is = Test.class.getResourceAsStream("./ambiguous.json");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = "";
				String ks = "";
				while( (line = br.readLine()) != null) {
					ks = ks + line;
				}
				br.close();
				is.close();
				
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(ks.getBytes());
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
	public static void checkAndLoadInit() {
		String settingFile = System.getProperty("user.home") + File.separator + "Setting.json";
		File file = new File(settingFile);
		if(!file.exists()) {
			try {
				InputStream is = Test.class.getResourceAsStream("./Setting.json");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = "";
				String ks = "";
				while( (line = br.readLine()) != null) {
					ks = ks + line;
				}
				br.close();
				is.close();
				
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(ks.getBytes());
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
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
    public static void main( String[] args )
    {
    	checkAndLoadInit();
    	checkAndLoadAmbiguous();
    	
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
                        f.addKeyListener(p);
                        JMenuBar menubar = new JMenuBar();
                        JMenu menu = new JMenu("Menu");
                        JMenuItem full_zoom = new JMenuItem("Full");
                        full_zoom.addActionListener(p);
                        JMenuItem width_zoom = new JMenuItem("Width");
                        width_zoom.addActionListener(p);
                        JMenuItem zoomin = new JMenuItem("Zoom in");
                        zoomin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
                        zoomin.addActionListener(p);
                        
                        JMenuItem zoomout = new JMenuItem("Zoom out");
                        zoomout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
                        zoomout.addActionListener(p);

                        JMenuItem st= new JMenuItem("Load Subtitle");
                        st.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
                        st.addActionListener(p);

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

                        JMenuItem autobound = new JMenuItem("Find contour");
                        autobound.addActionListener(p);     
                        autobound.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));

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
                        
                        menu.add(full_zoom);
                        menu.add(width_zoom);
                        menu.add(zoomin);
                        menu.add(zoomout);
                        menu.addSeparator();
                        menu.add(st);
                        
                        menu2.add(box);
                        menu2.add(circle);
                        menu2.add(rr);
                        menu2.addSeparator();
                        menu2.add(leftex);
                        menu2.add(rightex);
                        menu2.add(upex);
                        menu2.add(downex);
                        menu2.addSeparator();
                        menu2.add(autobound);
                        menu2.addSeparator();
                        menu2.add(autoon);
                        menu2.add(autooff);
                        
                        menu3.add(i_normal);
                        menu3.add(i_dialate);
                        menu3.add(i_erode);
                        menubar.add(menu);
                        menubar.add(menu2);
                        menubar.add(menu3);
                        
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
}

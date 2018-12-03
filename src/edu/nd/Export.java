package edu.nd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONObject;

public class Export extends JPanel {
	List<TranslateVO> tlist = new ArrayList<TranslateVO>();
	JTextArea status = new JTextArea();
	
	public Export() {
		super();
		status.setEditable(true);
		status.setMinimumSize(new Dimension (500, 1500));
		JScrollPane jc = new JScrollPane(status);
		jc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setLayout(new BorderLayout());
		this.add(jc, BorderLayout.CENTER);
		//status.setPreferredSize(new Dimension (500, 5000));
		jc.setPreferredSize(new Dimension (500, 500));
		this.setPreferredSize(new Dimension (500, 500));
	}
	
	public void export(String dir, String targetdir) throws Exception {
		File folder = new File(dir);
		if(!folder.isDirectory()) {
			Logger.err("" + dir + " is not directory!", null);
			return;
		}
		Logger.debug( "Current Path: " + dir );
		
        File[] listOfFiles = folder.listFiles((
        new FilenameFilter() {
        	@Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg") ||
                		name.toLowerCase().endsWith(".jpeg") ||
                		name.toLowerCase().endsWith(".png") ||
                		name.toLowerCase().endsWith(".gif");
          
            }
        }));
        Arrays.sort(listOfFiles);
        List<File> filelist = Arrays.asList(listOfFiles);
        Thread t = new Thread() {
        	public void run() {
                for(File target : filelist) {
                	BufferedImage img =  null;
                	try {
                		img = ImageIO.read(target);
                		status.append(target.getName() + " is read.\n");
                	} catch(Exception ex) {
                		ex.printStackTrace();
                		status.append("Read Error: " + target.getName() + "\n");
                		continue;
                	}
                	//BufferedImage img = ImageIO.read(target);
                	
                	int stat = buildListFromJSON(target.getPath() + ".json");
                	if(stat == -2) {
                		status.append("There is NO subtitle file.\n");
                	} else if (stat == -1) {
                		status.append("There is a subtitle file, but ran into error.\n");
                	} else {
                		drawSubtitle((Graphics2D)img.getGraphics());
                		status.append("There is a subtitle file.\n");
                	}
                	String targetnm = targetdir + File.separator + target.getName();
                	File outpic = new File(targetnm);
                	try {
                    	ImageIO.write(img, "jpg", outpic);
                       	status.append(targetnm + " is written.\n");                           	
                		
                	} catch (Exception ex) {
                       	status.append(targetnm + " is NOT written.\n" + ex.getMessage() + "\n");
                	}
                }
                status.append("\n\nFinished.\n");
                status.append("Please, close thie window.\n");
        	}
        };
        t.start();
	}

	private void drawSubtitle(Graphics2D g2d) {

    	Iterator<TranslateVO> it = tlist.iterator();
    	getSubtitleFont(g2d);
    	while(it.hasNext()) {
    		TranslateVO obj = it.next();
    		String output = obj.getTranslated();
    		int x = obj.getX();
    		int y = obj.getY();
    		
    		x = (int)(x * ImageViewer.zoom);
    		y = (int)(y * ImageViewer.zoom);
    		int original_y = y;
    		output = StringUtil.reverseEscape(output);
    		output = getLineBreak(output);
    		int totalwidth = 0;
    		for( int tmpWidth: g2d.getFontMetrics().getWidths() ) {
        		if(tmpWidth > totalwidth) {
        			totalwidth = tmpWidth;
        		}
        	}
        	for(int i=1;i<=3;i++) {
        		y = original_y;
        		for (String line : output.split("\n")) {
                	g2d.setColor(new Color(210, 210, 210, 80));
                	

                	int boxWidth = (int)((double)totalwidth * 0.4 * (double)line.getBytes().length);
        			g2d.fillRect(x - 5, 
        					     y - (int)(g2d.getFontMetrics().getHeight() * 0.8 ),
        					     boxWidth, 
        					     (int)(g2d.getFontMetrics().getHeight() * 1.05));
        			//g2d.drawString(line, x, y += g2d.getFontMetrics().getHeight());        			
                	g2d.setColor(Color.WHITE);

        			g2d.drawString(line, x+i, y+i);
            		g2d.drawString(line, x-i, y-i);
            		g2d.drawString(line, x+i, y-i);
            		g2d.drawString(line, x-i, y+i);
            		g2d.drawString(line, x+i, y);
            		g2d.drawString(line, x, y+i);
            		y = y + g2d.getFontMetrics().getHeight() + 1;

        		}
        		
        	}
        	g2d.setColor( ImageViewer.getFontColor() );
        	y = original_y;
        	for (String line : output.split("\n")) {
        		g2d.drawString(line, x, y);
        		y = y +  g2d.getFontMetrics().getHeight() + 1;
        	}
        	//g2d.drawString(output, x, y);
    	}

    }

    public void getSubtitleFont(Graphics2D g2d) {

    	g2d.setFont(ImgFront.getFont( ImageViewer.font_size ));
    }

    private String getLineBreak(String input) {
    	String output = "";
    	int lineBreakLimit = ImageViewer.linebreak;
    	lineBreakLimit = lineBreakLimit * 2;
	    int len = input.length();
	    int z = 0;
	    
	    for(int i=0;i<len;i++) {
	    	String ks = input.substring(i, i+1);
	    	if(z == 0 && ks.equals(" ")) {
	    		
	    	} else {
	    		output = output+ks;
	    	}
	    	
	    	
	    	if(z > lineBreakLimit) {
	    		
	    		output = output + "\n";
	    		z = 0;
	    	}
	    	z++;
	    	if(ks.getBytes().length >= 2) z = z + ks.getBytes().length - 1;
	    }
    	return output;
    }

	public int buildListFromJSON(String filename) {
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

}

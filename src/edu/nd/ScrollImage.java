package edu.nd;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class ScrollImage extends JPanel implements KeyListener, ActionListener  {
    private JFrame subtitle = null;
	private static final long serialVersionUID = 1L;
    private BufferedImage originalimage;
    private BufferedImage image;
    private JPanel canvas;
    private List<ChoiShape> selection = new ArrayList<ChoiShape>();
    private List<File> filelist = new ArrayList<File>();
    boolean global_clicked = false;
    
    private int startX = 0;
    private int startY = 0;
    private int tempX = 0;
    private int tempY = 0;
    
    private int crtnum = 0;
    private JScrollPane sp;
    private String msg = "";
    private ImageProcess ip = new ImageProcess();
    final static float dash1[] = {10.0f};
    final static BasicStroke thickStroke =
        new BasicStroke(2.0f);
    final static BasicStroke normalStroke =
            new BasicStroke(1.0f);
    private TrasnlateTable ttable = new TrasnlateTable();
    
    private boolean layover = false;
    private boolean subtitleOn = false;
    
    public void presentMessage(String _msg) {
    	_msg = StringUtil.BlackListFilter(_msg);
		this.msg = _msg;
		this.canvas.repaint();
		this.canvas.revalidate();
		this.sp.repaint();
		this.sp.revalidate();	
    }
    
    public void presentSysMessage(String _msg) {
		this.msg = _msg;
		this.canvas.repaint();
		this.canvas.revalidate();
		this.sp.repaint();
		this.sp.revalidate();	
		Thread t = new Thread() {
			public void run() {
				try {
					sleep(3000);
					msg = "";
					canvas.repaint();
					canvas.revalidate();
					sp.repaint();
					sp.revalidate();					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		t.start();
    }
    
    
	@Override
	public void keyPressed(KeyEvent arg0) {
		System.out.println(arg0.getKeyCode());
		if(arg0.getKeyCode() == 82) { //R - revert to initial state. 
			selection = new ArrayList<ChoiShape>();
			presentMessage("");
		}
		else if(arg0.getKeyCode() == 40 || arg0.getKeyCode() == 38) {
			if(arg0.getKeyCode() == 40) {
				sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getValue() + 35);
			} else {
				int newvertscroll = sp.getVerticalScrollBar().getValue() - 35;
				if(newvertscroll < 0) newvertscroll = 0;
				sp.getVerticalScrollBar().setValue(newvertscroll);
			}
		}
		else if(arg0.getKeyCode() == 10) { //Enter - JSON SAVE
			File current = filelist.get(crtnum);
			String fname = current.getName() + ".json";
			String dir = current.getParent();
			
			String fullname = dir + File.separator + fname;
			
			System.out.println("Targetfile: " + fullname);
			int status = ttable.save( fullname );
			if(status == 0) presentSysMessage("Saved.");
			else if (status == -2) presentSysMessage("Your translation list is empty.");
			
		}
		else if(arg0.getKeyCode() == 32) { //SPACE - Next File
			subtitleOn = false;
			selection = new ArrayList<ChoiShape>();
			msg = "";
			crtnum ++;
			if(crtnum >= filelist.size()) {
				this.presentMessage("End of the file list.");
				return;
			}
			File next = filelist.get(crtnum);
			try {
				//this.image = ImageIO.read(next);
				File tmp = this.fileload(next.getPath());
				canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
				canvas.revalidate();
				canvas.repaint();
				sp.revalidate();
				sp.getVerticalScrollBar().setValue(0);
			} catch(Exception ex) {
				System.err.println("File doesn't exist.");
				msg = "Next file does not exist.";
				canvas.repaint();
			}
		} 
		/*
		else if (arg0.getKeyCode() == 88 || //x
				arg0.getKeyCode() == 90 || //z
				arg0.getKeyCode() == 65 ||  //a
				arg0.getKeyCode() == 83 ||  //s
				arg0.getKeyCode() == 73 ||  //i
				
				arg0.getKeyCode() == 68   //d
				
				) {
			doOCR(arg0.getKeyCode());
		}*/

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
    	
    public class MoveListener implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent arg0) {
			if(global_clicked == true) {
				tempX = arg0.getX();
				tempY = arg0.getY();
				canvas.repaint();
				canvas.revalidate();
			}
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			
		}
    	
    }
    public class SpecialListener implements MouseListener {

		@Override
		public void mouseEntered(MouseEvent arg0) {
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			//System.out.println("mouse pressed");
			//System.out.println(arg0.getX());
			//System.out.println(arg0.getY());
			
			startX = arg0.getX();
			startY = arg0.getY();
			global_clicked = true;
						
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			//System.out.println("mouse released");
			//System.out.println(arg0.getX());
			//System.out.println(arg0.getY());
			global_clicked = false;
			
			int boxx = 0;
			int boxy = 0;
			int boxwidth = 0;
			int boxheight = 0;
			
			if(startX < arg0.getX()) {
				boxx = startX;
				boxwidth = arg0.getX() - startX;
			} else {
				boxx = arg0.getX();
				boxwidth =  startX - arg0.getX();
			}

			if(startY < arg0.getY()) {
				boxy = startY;
				boxheight = arg0.getY() - startY;
			} else {
				boxy = arg0.getY();
				boxheight =  startY - arg0.getY();
			}
			
			if( ImageViewer.defaultShape == EnumCollection.SelectionShape.Box ) {
				Shape rect = new Rectangle2D.Double(boxx, boxy, boxwidth, boxheight);
				ChoiShape vo = new ChoiShape();
				
				vo.setShape(rect);
				vo.setForm(EnumCollection.SelectionShape.Box);
				selection.add( vo );			
			} else if(ImageViewer.defaultShape == EnumCollection.SelectionShape.RRectangle) {
				Shape rect = new RoundRectangle2D.Double(boxx, boxy, boxwidth, boxheight, 50, 50);
				ChoiShape vo = new ChoiShape();
				vo.setShape(rect);
				vo.setForm(EnumCollection.SelectionShape.RRectangle);
				selection.add( vo );
			} else {
				Shape rect = new Ellipse2D.Double(boxx, boxy, boxwidth, boxheight);
				ChoiShape vo = new ChoiShape();
				vo.setShape(rect);
				vo.setForm(EnumCollection.SelectionShape.Circle);				
				selection.add(vo);		
			}
			startX = 0;
			startY = 0;
			tempX = 0;
			tempY = 0;
			if( ImageViewer.autocontour ) {
				findContour();
			}
			canvas.repaint();
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}
    	
    }
    public ScrollImage() {
    }
    
    public void filereload() {
    	
    }
    public File fileload(String filename) {
        File current = new File(filename);
        ImageViewer.setTitle(filename);
        
        ttable.buildListFromJSON( filename + ".json" );
    	try {
        	//System.out.println(filename + " is presented.");
        	//presentSysMessage(filename + " is presented.");
            this.originalimage = ImageIO.read(current);
            int w = (int)(originalimage.getWidth() * ImageViewer.zoom);
            int h = (int)(originalimage.getHeight() * ImageViewer.zoom);
            
            if( ImageViewer.zoom != 0.0 ) {
                image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                AffineTransform at = new AffineTransform();
                at.scale(ImageViewer.zoom, ImageViewer.zoom);
                AffineTransformOp scaleOp = 
                   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                image = scaleOp.filter(originalimage, image);            	
            } else {
            	image = originalimage.getSubimage(0, 0, w, h);
            }
            
        }catch(IOException ex) {
            ex.printStackTrace();
        	//Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    	return current;
    }
    
    //===========================
    //Initialization
    //===========================
    public ScrollImage(String img) {
    	super();
        
        //String folder_string = img.substring(0, img.lastIndexOf( File.pathSeparator ));
        File folder;
		try {
			File current = fileload(img);
			folder = new File(current.getParent());
			if(!folder.isDirectory()) {
				System.err.print(current.getParent() + " is not directory!");
				return;
			}
			System.out.println("Current Path: " + current.getParent());
			
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
	        filelist = Arrays.asList(listOfFiles);
	        
	        crtnum = filelist.indexOf(current);

		} catch (Exception e) {
			e.printStackTrace();
		}
        
		subtitle = new JFrame();
		JTable table = new JTable(ttable);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setFont( ImgFront.getFont(12) );
        table.setRowHeight(50);
		final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem layover = new JMenuItem("See in the picture.");

        deleteItem.addActionListener(new ActionListener()   {
        	@Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println( table.getSelectedRow() );
                ttable.remove(table.getSelectedRow());
                table.repaint();
                table.revalidate();
                subtitle.repaint();
                subtitle.revalidate();
            	//JOptionPane.showMessageDialog(subtitle, "Right-click performed on table and choose DELETE");
            }

        });
        
        popupMenu.add(deleteItem);    
        popupMenu.add(layover);
        JScrollPane scrollPane2 = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		table.setComponentPopupMenu(popupMenu);
		 
		subtitle.add(scrollPane2);
		subtitle.setTitle("Subtitle management");
		subtitle.setBounds( 50, 50, 600, 600 );
		subtitle.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		subtitle.setVisible(true);
		
        this.canvas = new JPanel() {
        
            private static final long serialVersionUID1 = 1L;
            @Override
            protected void paintComponent(Graphics g) {
            	Graphics2D g2d = (Graphics2D) g;
                super.paintComponent(g);
  
                g2d.drawImage(image, 0, 0, null);                	
                
                drawSubtitle(g2d);
                if( ImageViewer.defaultShape == EnumCollection.SelectionShape.Box ) {
                	drawTempBox(g2d);
                } else if( ImageViewer.defaultShape == EnumCollection.SelectionShape.RRectangle ) {
                	DrawTempRoundRectangle(g2d);
                } else if( ImageViewer.defaultShape == EnumCollection.SelectionShape.Circle ) {
                	drawTempCircle(g2d);
                }

                if(!selection.isEmpty()) {
                	g2d.setStroke(thickStroke);
                	for(ChoiShape rect : selection ) {
                		g2d.setColor(Color.MAGENTA);
                		g2d.draw(rect.getShape());
                	
                	}
                	g2d.setStroke(normalStroke);
                }
                if(!msg.equals("")) {
                	Font myFont = ImgFront.getFont( ImageViewer.font_size );

                	int msgx = 5 + sp.getHorizontalScrollBar().getValue();
                	int msyy = 40 + sp.getVerticalScrollBar().getValue();
                	g2d.setFont(myFont);
                	g2d.setColor(Color.WHITE);
                	for(int i=1;i<=3;i++) {
                		g2d.drawString(msg, msgx+i, msyy+i);
                		g2d.drawString(msg, msgx-i, msyy-i);
                		g2d.drawString(msg, msgx+i, msyy-i);
                		g2d.drawString(msg, msgx-i, msyy+i);
                		g2d.drawString(msg, msgx+i, msyy);
                		g2d.drawString(msg, msgx, msyy+i);
                	}
                	g2d.setColor(Color.BLACK);
                	g2d.drawString(msg, msgx, msyy);

                }
            }
        };
        //canvas.add(new JButton("Currently I do nothing"));
        canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        canvas.addMouseListener(new SpecialListener());
        canvas.addMouseMotionListener(new MoveListener());
        //canvas.addKeyListener(this);
        sp = new JScrollPane(canvas);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension (30, 0));
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension (0, 30));
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
    }
    
    private void DrawTempRoundRectangle(Graphics2D g2d) {
        if(startX > 0 && startY >0 && tempX > 0 && tempY >0 && global_clicked == true) {
    		//g2d.setStroke(dashed);
        	
    		int kkx = 0;
    		int kky = 0;
    		
    		if(startX < tempX) kkx = startX;
    		else kkx = tempX;
    		if(startY < tempY) kky = startY;
    		else kky = tempY;

    		g2d.setColor(Color.white);            		
    		g2d.draw(new RoundRectangle2D.Double(kkx-1, 
    				kky-1, 
                    Math.abs( tempX - startX ), 
                    Math.abs(tempY - startY), 
                    50, 50
    				));

    		g2d.draw(new RoundRectangle2D.Double(kkx-1, 
    				kky+1, 
                    Math.abs( tempX - startX ), 
                    Math.abs(tempY - startY),
                    50, 50
    				));

    		g2d.setColor(Color.RED);            		
    		g2d.draw(new RoundRectangle2D.Double(kkx, 
    										kky, 
    				                        Math.abs( tempX - startX ), 
    				                        Math.abs(tempY - startY), 
    				50, 50                        
    				));
    		
        	
        }    	

    }
    
    private void drawTempBox(Graphics2D g2d) {
        if(startX > 0 && startY >0 && tempX > 0 && tempY >0 && global_clicked == true) {
    		//g2d.setStroke(dashed);
        	
    		int kkx = 0;
    		int kky = 0;
    		
    		if(startX < tempX) kkx = startX;
    		else kkx = tempX;
    		if(startY < tempY) kky = startY;
    		else kky = tempY;

    		g2d.setColor(Color.white);            		
    		g2d.draw(new Rectangle2D.Double(kkx-1, 
    				kky-1, 
                    Math.abs( tempX - startX ), 
                    Math.abs(tempY - startY)));

    		g2d.draw(new Rectangle2D.Double(kkx+1, 
    				kky+1, 
                    Math.abs( tempX - startX ), 
                    Math.abs(tempY - startY)));

    		g2d.setColor(Color.RED);            		
    		g2d.draw(new Rectangle2D.Double(kkx, 
    										kky, 
    				                        Math.abs( tempX - startX ), 
    				                        Math.abs(tempY - startY)));
    		
        	
        }    	
    }
    private void drawTempCircle(Graphics2D g2d) {
        if(startX > 0 && startY >0 && tempX > 0 && tempY >0 && global_clicked == true) {
    		//g2d.setStroke(dashed);
        	
    		int kkx = 0;
    		int kky = 0;
    		
    		if(startX < tempX) kkx = startX;
    		else kkx = tempX;
    		if(startY < tempY) kky = startY;
    		else kky = tempY;

    		g2d.setColor(Color.white);            		
    		g2d.draw(new Ellipse2D.Double(kkx-1, 
    				kky-1, 
                    Math.abs( tempX - startX ), 
                    Math.abs(tempY - startY)));

    		g2d.draw(new Ellipse2D.Double(kkx+1, 
    				kky+1, 
                    Math.abs( tempX - startX ), 
                    Math.abs(tempY - startY)));

    		g2d.setColor(Color.RED);            		
    		g2d.draw(new Ellipse2D.Double(kkx, 
    										kky, 
    				                        Math.abs( tempX - startX ), 
    				                        Math.abs(tempY - startY)));
    		
        	
        }    	
    }

    private void drawSubtitle(Graphics2D g2d) {
    	if(!subtitleOn) return;

    	//File current = filelist.get(crtnum);
		//String fname = current.getName() + ".json";
		//String dir = current.getParent();
		
		//String fullname = dir + File.separator + fname;
		
    	Iterator it = ttable.getTranslateList().iterator();
    	getSubtitleFont(g2d);
    	while(it.hasNext()) {
    		TranslateVO obj = (TranslateVO) it.next();
    		String output = obj.getTranslated();
    		int x = obj.getX();
    		int y = obj.getY();
    		
    		x = (int)(x * ImageViewer.zoom);
    		y = (int)(y * ImageViewer.zoom);
    		int original_y = y;
    		output = StringUtil.reverseEscape(output);
    		output = getLineBreak(output);
    		//System.out.println(output);
    		
        	for(int i=1;i<=3;i++) {
        		y = original_y;
        		for (String line : output.split("\n")) {
                	g2d.setColor(new Color(200, 200, 200, 70));
                	int totalwidth = 0;
                	System.out.println( );
                	for( int tmpWidth: g2d.getFontMetrics().getWidths() ) {
                		if(tmpWidth > totalwidth) {
                			totalwidth = tmpWidth;
                		}
                	}
        			g2d.fillRect(x - 5, 
        					     y - (int)(g2d.getFontMetrics().getHeight() * 0.8 ),
        					     totalwidth * 7, 
        					     (int)(g2d.getFontMetrics().getHeight() * 1.1));
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
        	g2d.setColor(Color.BLACK);
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
    	
	    int len = input.length();
	    int z = 0;
	    
	    for(int i=0;i<len;i++) {
	    	String ks = input.substring(i, i+1);
	    	if(z == 0 && ks.equals(" ")) {
	    		
	    	} else {
	    		output = output+ks;
	    	}
	    	
	    	
	    	if(z > 15) {
	    		
	    		output = output + "\n";
	    		z = 0;
	    	}
	    	z++;
	    	if(ks.getBytes().length >= 2) z = z + ks.getBytes().length - 1;
	    }
    	return output;
    }
    
    private void findContour() {
		BufferedImage cropped_target = null;
		if(selection.isEmpty()) return;
		for (ChoiShape targetshape : selection) {
			int cropx = (int) targetshape.getShape().getBounds2D().getX();
			int cropy = (int) targetshape.getShape().getBounds2D().getY();
			int tmp_width = (int) targetshape.getShape().getBounds2D().getWidth();
			int tmp_height = (int) targetshape.getShape().getBounds2D().getHeight();
			if(tmp_width == 0) {
				JOptionPane.showMessageDialog(sp,
					    "There is something wrong with the selection box.");
				presentMessage("");
				return;
			}

			
			cropped_target = image.getSubimage(cropx, 
													  cropy, 
													  tmp_width, 
													  tmp_height);
		}
	
		Rectangle2D bounds = ip.findContour(cropped_target);
		System.out.println(bounds.getWidth() + " " + bounds.getHeight());
		ChoiShape cs = selection.get(selection.size()-1);
		selection.set(selection.size()-1, reboundary(cs, bounds));
		redrawAll();
		//tmpHeight = tmpHeight + 5;    	
    }
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JMenuItem src = (JMenuItem) arg0.getSource();
		String target = src.getText();
		
		if(target.equals("Width")) {
			if( originalimage.getWidth() > ImageViewer.getMainFrameWidth()) {
				ImageViewer.zoom = (double)(ImageViewer.getMainFrameWidth()-50) / (double) originalimage.getWidth();
				System.out.println( ImageViewer.getMainFrameWidth() );
				System.out.println( originalimage.getWidth() );
				System.out.println(ImageViewer.zoom);
				//filereload
				File next = filelist.get(crtnum);
				File tmp = this.fileload(next.getPath());
				canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
				//sp.setPreferredSize(preferredSize);
				redrawAll();
			}
		} else if(target.equals("Zoom out")) {
			ImageViewer.zoom = ImageViewer.zoom - 0.05;
			if (ImageViewer.zoom < 0.05) ImageViewer.zoom = 0.05; 
			File next = filelist.get(crtnum);
			File tmp = this.fileload(next.getPath());
			canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			//sp.setPreferredSize(preferredSize);
			redrawAll();

		} else if(target.equals("Zoom in")) {
			ImageViewer.zoom = ImageViewer.zoom + 0.05;
			//if (ImageViewer.zoom < 0.1) ImageViewer.zoom = 0.1; 
			File next = filelist.get(crtnum);
			File tmp = this.fileload(next.getPath());
			canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			//sp.setPreferredSize(preferredSize);
			redrawAll();
		} else if(target.equals("OCR")) {
			doOCR(90);
		} else if(target.equals("OCR with Ostu")) {
			doOCR(0);

		} else if(target.equals("OCR with Ostu Inverse")) {
			doOCR(73);

		} else if(target.equals("Load Subtitle")) {
			if(subtitleOn) {
				subtitleOn = false;
			} else {
				subtitleOn = true;				
			}
			//System.out.println("Let's draw!");
			redrawAll();
		} else if(target.equals("Box")) {
			ImageViewer.defaultShape = EnumCollection.SelectionShape.Box;
		} else if(target.equals("Circle")) {
			ImageViewer.defaultShape = EnumCollection.SelectionShape.Circle;
		} else if(target.equals("Rounded rectangle")) {
			ImageViewer.defaultShape = EnumCollection.SelectionShape.RRectangle;
		} else if (target.equals("Find contour")) {
			System.out.println("Find contour is called");
			findContour();

		} else if(target.equals("Expand left")
				|| target.equals("Expand up")
				|| target.equals("Expand down")				
				|| target.equals("Expand right")) {
			if(!selection.isEmpty()) {
				ChoiShape shape = selection.get(selection.size()-1);
				
				double tmpX = shape.getShape().getBounds2D().getX();
				double tmpY = shape.getShape().getBounds2D().getY();
				double tmpWidth = shape.getShape().getBounds2D().getWidth();
				double tmpHeight = shape.getShape().getBounds2D().getHeight();

				Shape newShape = null;
				
				if(target.equals("Expand left")) {
					tmpX = tmpX - 5;
					tmpWidth = tmpWidth + 5;					
				} else if (target.equals("Expand right")) {
					tmpWidth = tmpWidth + 5;
				} else if (target.equals("Expand up")) {
					tmpY = tmpY - 5;
					double prevTmpHeight = tmpHeight;
					tmpHeight = tmpHeight + 5;
					if(tmpY < 0) {
						tmpY = 0;
						tmpHeight = prevTmpHeight;
					}
				} else if (target.equals("Expand down")) {
					tmpHeight = tmpHeight + 5;
				}
				
				if ( shape.getForm() == EnumCollection.SelectionShape.Box ) {
					newShape = new Rectangle2D.Double(tmpX, tmpY, tmpWidth, tmpHeight);
				} else if( shape.getForm() == EnumCollection.SelectionShape.Circle ) {
					newShape = new Ellipse2D.Double(tmpX, tmpY, tmpWidth, tmpHeight);
				} else if( shape.getForm() == EnumCollection.SelectionShape.RRectangle ) {
					newShape = new RoundRectangle2D.Double(tmpX, tmpY, tmpWidth, tmpHeight, 50, 50);
				}
				
				ChoiShape vo = new ChoiShape();
				vo.setShape(newShape);
				vo.setForm(shape.getForm());
				selection.set(selection.size()-1, vo);
				redrawAll();
			}
			
		}
	}
	
	private ChoiShape reboundary(ChoiShape cs, Rectangle2D bounds) {
		ChoiShape ret = new ChoiShape();
		ret.setForm(cs.getForm());
		double tmpX = bounds.getX() + cs.getShape().getBounds2D().getX();
		double tmpY = bounds.getY() + cs.getShape().getBounds2D().getY();
		double tmpWidth = bounds.getWidth();
		double tmpHeight = bounds.getHeight();
		Shape newShape = null;
		
		if ( cs.getForm() == EnumCollection.SelectionShape.Box ) {
			newShape = new Rectangle2D.Double(tmpX, tmpY, tmpWidth, tmpHeight);
		} else if( cs.getForm() == EnumCollection.SelectionShape.Circle ) {
			newShape = new Ellipse2D.Double(tmpX, tmpY, tmpWidth, tmpHeight);
		} else if( cs.getForm() == EnumCollection.SelectionShape.RRectangle ) {
			newShape = new RoundRectangle2D.Double(tmpX, tmpY, tmpWidth, tmpHeight, 50, 50);
		}		
		ret.setShape(newShape);
		return ret;
	}
	
	public void doOCR(int arg) {
		if(selection.isEmpty()) {
			presentSysMessage("Please select the shape first.");
			return;
		}
		presentMessage("OCR...");
		final List<ChoiShape> trlist = new ArrayList<ChoiShape>(selection);
		selection = new ArrayList<ChoiShape>();

		Thread t1 = new Thread() {
			public void run() {
				String consolidated = "";
				for (ChoiShape targetshape : trlist) {
					int cropx = (int) targetshape.getShape().getBounds2D().getX();
					int cropy = (int) targetshape.getShape().getBounds2D().getY();
					int tmp_width = (int) targetshape.getShape().getBounds2D().getWidth();
					int tmp_height = (int) targetshape.getShape().getBounds2D().getHeight();
					if(tmp_width == 0) {
						JOptionPane.showMessageDialog(sp,
							    "There is something wrong with the selection box.");
						presentMessage("");
						return;
					}

					BufferedImage cropped_target = image.getSubimage(cropx, 
															  cropy, 
															  tmp_width, 
															  tmp_height);
				
					BufferedImage cropped = new BufferedImage(tmp_width, tmp_height, BufferedImage.TYPE_INT_ARGB);
					Graphics gg = cropped.getGraphics();
					
					if (arg == 73) gg.setColor(Color.BLACK); //I
					else gg.setColor(Color.WHITE);
					
					gg.fillRect(0, 0, tmp_width, tmp_height);
					if(targetshape.getForm() == EnumCollection.SelectionShape.Circle) {
						gg.setClip(new Ellipse2D.Double(0, 0, tmp_width, tmp_height));
					} else if (targetshape.getForm() == EnumCollection.SelectionShape.RRectangle) {
						gg.setClip(new RoundRectangle2D.Double(0, 0, tmp_width, tmp_height, 50, 50));
					}
					
					gg.drawImage(cropped_target, 0, 0, null);
					String tmpname = System.getProperty("user.home") + File.separator + "temp.png";
					JTesseract jt = new JTesseract();
						
					if(arg == 65) { //a
						ip.hsvfilter(cropped, tmpname, 1);
					} else if(arg == 83) { // s
						ip.hsvfilter(cropped, tmpname, 2);
						
					} else if(arg == 68) { // d
						ip.hsvfilter(cropped, tmpname, 3);

					} else if(arg == 90) { // z
						ip.doitWithoutOstu(cropped, tmpname);
					} else if(arg == 73) { // i
						ip.doitInverse(cropped, tmpname); 
						//ip.doitWithoutOstu(cropped, tmpname);
					} else { // Normally x
						ip.doit(cropped, tmpname);
					}

					String ret = jt.doOCR(tmpname);
					ret = ret.replace("", "");
					ret = StringUtil.BlackListFilter(ret);
					consolidated = consolidated + " " +ret;
				}
				presentMessage("Translating...");
				final String target_translate = consolidated;
				redrawAll();
				GTranslator gt = new GTranslator();
				String translated = gt.translate(target_translate);
				presentMessage( translated );
				
				if(translated != null && translated.length() > 0) {
					TranslateVO vo = new TranslateVO();
					vo.setX( (int) ( (trlist.get(trlist.size()-1).getShape().getBounds2D().getX())*(1/ImageViewer.zoom) ) );
					vo.setY( (int) ( (trlist.get(trlist.size()-1).getShape().getBounds2D().getY())*(1/ImageViewer.zoom) ) );
					vo.setOriginal( target_translate );
					vo.setTranslated( translated );
					ttable.addToList(vo);
					subtitle.repaint();
					subtitle.revalidate();
				}
				
				redrawAll();				
			}
		};
		t1.start();
	}
	public void redrawAll() {
		canvas.repaint();
		canvas.revalidate();
		sp.repaint();
		sp.revalidate();
		//sp.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	}
}
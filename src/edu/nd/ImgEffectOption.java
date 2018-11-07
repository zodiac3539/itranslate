package edu.nd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ImgEffectOption extends JPanel {
	public static void main(String[] args) {
		new ImgEffectOption(null);
	}
	BufferedImage original = null;
	BufferedImage bf = null;
    private JPanel canvas = new JPanel();
    private JPanel inputarea = new JPanel();
    
    //JLabel kernelWidth = new JLabel("Kernel Width: ");
    //JLabel kernelHeight = new JLabel("Kernel Height: ");
    //JLabel kerneliterate = new JLabel("Iterate: ");

    JLabel lowH = new JLabel("Lower-band H: ");
    JLabel lowS = new JLabel("Lower-band S: ");
    JLabel lowV = new JLabel("Lower-band V: ");

    JLabel highH = new JLabel("Upper-band H: ");
    JLabel highS = new JLabel("Upper-band S: ");
    JLabel highV = new JLabel("Upper-band V: ");
    
    JButton btn = new JButton("Save");
    JLabel exp1 = new JLabel("Expected output: ");

    JFrame self = null;
    JTextField lowH_txt = new JTextField("0");
    JTextField lowS_txt = new JTextField("0");
    JTextField lowV_txt = new JTextField("0");
    
    JTextField highH_txt = new JTextField("255");
    JTextField highS_txt = new JTextField("255");
    JTextField highV_txt = new JTextField("255");    
    
    
    private void init() {
		GridLayout experimentLayout = new GridLayout(0,2);

		inputarea.add(lowH);
		inputarea.add(lowH_txt);
		inputarea.add(lowS);
		inputarea.add(lowS_txt);
		inputarea.add(lowV);
		inputarea.add(lowV_txt);
		inputarea.add(highH);
		inputarea.add(highH_txt);
		inputarea.add(highS);
		inputarea.add(highS_txt);
		inputarea.add(highV);
		inputarea.add(highV_txt);
		
		inputarea.add(btn);
		inputarea.add(exp1);		
		
		inputarea.setPreferredSize(new Dimension(500, 200));
		inputarea.setLayout(experimentLayout);
		
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ImageProcess ip = new ImageProcess();
				int[] low = new int[3];
				int[] high = new int[3];
				low[0] = Integer.parseInt(lowH_txt.getText());
				low[1] = Integer.parseInt(lowS_txt.getText());
				low[2] = Integer.parseInt(lowV_txt.getText());
				high[0] = Integer.parseInt(highH_txt.getText());
				high[1] = Integer.parseInt(highS_txt.getText());
				high[2] = Integer.parseInt(highV_txt.getText());
				try {
					bf = ip.hsvFilterTest(original, low, high);
					canvas.repaint();
				}catch (Exception ex) {
					ex.printStackTrace();
				}
				
			}
			
		});
		this.setLayout(new BorderLayout(10, 10));
		this.add(inputarea, BorderLayout.PAGE_START);
		
		canvas.setPreferredSize(new Dimension(500, 300));

		this.add(canvas, BorderLayout.CENTER);
		//this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		/*
		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int i=JOptionPane.showConfirmDialog(null, "Do you want to close?");
                if(i==0)
                    self.dispose();
            }
        });*/    	
    }
	public ImgEffectOption() {
		super();
		init();
		//this.setSize(new Dimension(500, 200));
	}
		
	public ImgEffectOption(BufferedImage _bf) {
		super();
		this.bf = _bf;
        this.original = bf.getSubimage(0, 0, bf.getWidth(), bf.getHeight());
        
		this.canvas = new JPanel() {
	        protected void paintComponent(Graphics g) {
	        	Graphics2D g2d = (Graphics2D) g;
	            super.paintComponent(g);
	    		if(bf != null) {
	    			int w = bf.getWidth();
	    			int h = bf.getHeight();
	    			
	    			g2d.drawImage(bf, 0, 0, w, h, null);
	    		}			            
	        }
        };
        
		init();	
		this.canvas.setPreferredSize(new Dimension( bf.getWidth(), bf.getHeight() ));
		canvas.repaint();
		canvas.revalidate();
	}
	
	public void show() {
		//this.setVisible(true);
	}
}

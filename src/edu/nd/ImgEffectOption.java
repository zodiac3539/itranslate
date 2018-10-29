package edu.nd;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ImgEffectOption extends JFrame {
	public static void main(String[] args) {
		new ImgEffectOption(null);
	}
	
	BufferedImage bf = null;
    private JPanel canvas = new JPanel();
    private JPanel inputarea = new JPanel();
    
    JLabel kernelWidth = new JLabel("Kernel Width: ");
    JLabel kernelHeight = new JLabel("Kernel Height: ");
    JLabel kerneliterate = new JLabel("Iterate: ");
    JButton btn = new JButton("Save");
    JLabel exp1 = new JLabel("Expected output: ");

    
    JTextField kWidth = new JTextField();
    JTextField kHeight = new JTextField();
    JTextField kIterate = new JTextField();
    
	public ImgEffectOption() {
		super();
	}
	
	public ImgEffectOption(BufferedImage bf) {
		super();
		this.setSize(new Dimension(500, 200));
		GridLayout experimentLayout = new GridLayout(0,2);
		inputarea.add(kernelWidth);
		inputarea.add(kWidth);
		inputarea.add(kernelHeight);
		inputarea.add(kHeight);
		inputarea.add(kerneliterate);
		inputarea.add(kIterate);
		inputarea.add(btn);
		inputarea.add(exp1);		
		
		inputarea.setPreferredSize(new Dimension(500, 200));
		inputarea.setLayout(experimentLayout);
		
		//canvas.setPreferredSize(new Dimension(500, 300));
		this.add(inputarea);
		//this.add(canvas);
		this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		this.setVisible(true);
		
	}
	
	public void show() {
		//this.setVisible(true);
	}
}

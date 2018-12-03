package edu.nd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class MenuSelection implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		JMenuItem src = (JMenuItem) arg0.getSource();
		String target = src.getText();
		
		if(target.equals("Normal image process")) {
			ImageViewer.defaultImageProcess = EnumCollection.ImageProcess.Normal;
		} else if(target.equals("Dialate image process")) {
			ImageViewer.defaultImageProcess = EnumCollection.ImageProcess.Dialation;
		} else if(target.equals("Erode image process")) {
			ImageViewer.defaultImageProcess = EnumCollection.ImageProcess.Erosion;
		} else if(target.equals("Auto find contour on")) {
			ImageViewer.autocontour = true;
		} else if(target.equals("Auto find contour off")) {
			ImageViewer.autocontour = false;
		} else if(target.equals("Auto translate on")) {
			ImageViewer.isAutoTranslate = true;
		} else if(target.equals("Auto translate off")) {
			ImageViewer.isAutoTranslate = false;
		} else if(target.equals("Tesseract Option 1")) {
			ImageViewer.tesser_option = 1;
		} else if(target.equals("Tesseract Option 2")) {
			ImageViewer.tesser_option = 2;
		} else if(target.equals("Export with JPG")) {
			JFileChooser chooser;
	        
		    chooser = new JFileChooser(); 
		    chooser.setCurrentDirectory(new java.io.File("."));
		    chooser.setDialogTitle("File export");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    chooser.setAcceptAllFileFilterUsed(false);
		    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
		    	System.out.println("getCurrentDirectory(): " 
		         +  chooser.getCurrentDirectory());
		    	System.out.println("getSelectedFile() : " 
		         +  chooser.getSelectedFile());
				BorderLayout experimentLayout = new BorderLayout();
		    	JFrame jf = new JFrame("status");
		    	Export ex = new Export();
		    	jf.setLayout(experimentLayout);
		    	jf.setSize(new Dimension (520, 520));
		    	jf.setContentPane(ex);
		    	jf.addWindowListener(new WindowListener() {

					@Override
					public void windowActivated(WindowEvent arg0) {	}

					@Override
					public void windowClosed(WindowEvent arg0) { }

					@Override
					public void windowClosing(WindowEvent arg0) {
						// TODO Auto-generated method stub
						int ret = JOptionPane.showConfirmDialog(jf, "Do you really want to close it?");
						if (ret == JOptionPane.OK_OPTION) {
							jf.dispose();
						}
						
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
		    	
		    	jf.setVisible(true);
		    	try {
					ex.export(ImageViewer.getCurrentDirectory(), chooser.getSelectedFile().getPath());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    } else {
		    	System.out.println("No Selection ");
		    }
		 }
	}

}

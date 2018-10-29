package edu.nd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JMenuItem;

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
		}

	}

}

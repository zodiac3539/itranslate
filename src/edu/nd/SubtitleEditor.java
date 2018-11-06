package edu.nd;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SubtitleEditor extends JPanel {
	TrasnlateTable table = null;
	JFrame mother = null;
	JLabel lbl_rownum = new JLabel("RowNum: ");
	JLabel txt_rownum = new JLabel("");
	
	JLabel lbl_x = new JLabel("X: ");
	JTextField txt_x = new JTextField("");

	JLabel lbl_y = new JLabel("Y: ");
	JTextField txt_y = new JTextField("");

	JLabel lbl_original = new JLabel("Original: ");
	JTextArea txt_original = new JTextArea("");

	JLabel lbl_translate = new JLabel("Translated: ");
	JTextArea txt_translate = new JTextArea("");

	JButton btn_translate = new JButton("Translate again");
	JButton btn_save = new JButton("Save");
	
	public SubtitleEditor() {
		super();
	}
	
	public SubtitleEditor(int num, TranslateVO vo, TrasnlateTable input, JFrame input2) {
		super();
		this.table = input;
		this.mother = input2;
		this.setPreferredSize(new Dimension(500, 500));
		GridLayout experimentLayout = new GridLayout2(0,2, 10, 10);
		txt_rownum.setText("" + num);
		txt_x.setText("" + vo.getX());
		txt_y.setText("" + vo.getY());
		txt_x.setPreferredSize(new Dimension(300, 30));
		txt_y.setPreferredSize(new Dimension(300, 30));
		
		txt_original.setFont(ImgFront.getFont(15));
		txt_translate.setFont(ImgFront.getFont(15));
		txt_translate.setLineWrap(true);
		txt_original.setLineWrap(true);
		txt_original.setPreferredSize(new Dimension(300, 100));
		txt_translate.setPreferredSize(new Dimension(300, 100));
		
		txt_original.setText("" + vo.getOriginal());
		txt_translate.setText("" + vo.getTranslated());
	
		this.add(lbl_rownum);
		this.add(txt_rownum);
		this.add(lbl_x);	
		this.add(txt_x);
		this.add(lbl_y);			
		this.add(txt_y);
		this.add(lbl_original);
		this.add(txt_original);
		this.add(lbl_translate);
		this.add(txt_translate);
		this.add(btn_translate);
		this.add(btn_save);
		btn_translate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btn_translate.setEnabled(false);
				Thread t = new Thread() {
					public void run() {
						GTranslator gt = new GTranslator();
						String org = gt.translate(txt_original.getText());
						txt_translate.setText(org);						
						btn_translate.setEnabled(true);
					}
				};
				t.run();				

			}
		});
		
		btn_save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				TranslateVO vo = new TranslateVO();
				vo.setX(Integer.parseInt(txt_x.getText()));
				vo.setY(Integer.parseInt(txt_y.getText()));
				vo.setOriginal( txt_original.getText() );
				vo.setTranslated( txt_translate.getText() );
				table.setValueAt(num, vo);
				//JOptionPane.showMessageDialog(null, "Saved!");
				mother.dispose();
			}
			
		});
		
		this.setLayout(experimentLayout);
	}
	
}

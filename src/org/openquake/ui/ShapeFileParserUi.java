package org.openquake.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ShapeFileParserUi extends OqPanel {

	private static final long serialVersionUID = -908668014130704045L;
	
	private JFileChooser directoryChooser;
	
	//input fields
	private JTextField sourceFilePath;
	private JTextField targetFilePath;
	private JTextField  minMagnitudeField;
	private JComboBox types;
	
	public ShapeFileParserUi() {
		super();
		initialize();
	}

	private void initialize() {
		setLayout(null);
		
		directoryChooser = new JFileChooser();
		directoryChooser.setMultiSelectionEnabled(false);
		directoryChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		sourceFilePath = new JTextField();
		sourceFilePath.setBounds(addLabel(20, 20, "Source File"), 20, 200, 30);
		sourceFilePath.setEditable(false);
		sourceFilePath.setDisabledTextColor(Color.BLACK);
		add(sourceFilePath);
		
		JButton browse = new JButton("Browse");
		browse.setBounds((int)sourceFilePath.getBounds().getMaxX() + 10, 20, 100, 30);
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = directoryChooser.showSaveDialog(sourceFilePath);
				if(result == JFileChooser.APPROVE_OPTION) {
					File selectedDir = directoryChooser.getSelectedFile();
					sourceFilePath.setText(selectedDir.getAbsolutePath());
				}
			}
		});
		add(browse);
		
		targetFilePath = new JTextField();
		targetFilePath.setBounds(addLabel(20, 60, "Target File"), 60, 200, 30);
		targetFilePath.setEditable(false);
		targetFilePath.setDisabledTextColor(Color.BLACK);
		add(targetFilePath);
		
		browse = new JButton("Browse");
		browse.setBounds((int)targetFilePath.getBounds().getMaxX() + 10, 60, 100, 30);
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = directoryChooser.showSaveDialog(targetFilePath);
				if(result == JFileChooser.APPROVE_OPTION) {
					File selectedDir = directoryChooser.getSelectedFile();
					targetFilePath.setText(selectedDir.getAbsolutePath());
				}
			}
		});
		add(browse);
		
		minMagnitudeField = new JTextField();
		minMagnitudeField.setBounds(addLabel(20, 100, "Min. Mag."), 100, 100, 30);
		add(minMagnitudeField);
		
		types = new JComboBox(new DefaultComboBoxModel());
		types.setBounds(addLabel(20, 140, "Type"), 140, 150, 30);
		fillTypes(types);
		add(types);
		
		JButton execute = new JButton("Execute");
		execute.setBounds(types.getBounds().x, 190, 100, 30);
		execute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int type = types.getSelectedIndex();
				String sourceFile = sourceFilePath.getText().trim();
				String targetFile = targetFilePath.getText().trim();
				
				double minMagnitude = 5.0;
				try {
					minMagnitude = Double.parseDouble(minMagnitudeField.getText().trim());
				} catch (Exception e2) {
				}
				
				if(sourceFile.length()>0 && targetFile.length() > 0) {
					try {
						if(type == 0) { //ASC
							ShapeFileParserController.parseAsc(sourceFile, targetFile, minMagnitude);
						} else if(type == 1) {//inslab
							ShapeFileParserController.parseInslab(sourceFile, targetFile, minMagnitude);
						} else if(type == 2) {//interface
							ShapeFileParserController.parseInterface(sourceFile, targetFile, minMagnitude);
						} else {
							throw new Exception("Invalid type!");
						}
						
						JOptionPane.showMessageDialog(sourceFilePath.getParent(), "Success!", "Success!", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(sourceFilePath.getParent(), exc.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		add(execute);
	}

	private void fillTypes(JComboBox types2) {
		DefaultComboBoxModel model = (DefaultComboBoxModel)types.getModel();
		model.addElement("ASC");
		model.addElement("Inslab");
		model.addElement("Interface");
	}

	@Override
	public void clean() {
		sourceFilePath.setText("");
		targetFilePath.setText("");
	}

}

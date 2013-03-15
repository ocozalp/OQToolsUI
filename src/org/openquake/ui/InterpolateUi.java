package org.openquake.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.openquake.postProcessing.utils.InterpolationCalc;

public class InterpolateUi extends OqPanel{

	private static final long serialVersionUID = -4008263079928399827L;

	private JFileChooser fileChooser;
	
	private JTextField source, poes;
	private JButton calculate;

	private JButton browse;
	
	private JCheckBox isLogInterpolation;
	
	public InterpolateUi() {
		super();
		initialize();
	}
	
	private void initialize() {
		this.setLayout(null);
		
		source = new JTextField();
		source.setBounds(addLabel(20, 20, "Source File"), 20, 150, 30);
		source.setEditable(false);
		source.setDisabledTextColor(Color.BLACK);
		add(source);
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(false);
		
		browse = new JButton("Browse");
		browse.setBounds((int) source.getBounds().getMaxX() + 10, 20, 100, 30);
		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showOpenDialog(browse);
				
				if(result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					source.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		add(browse);
		
		poes = new JTextField();
		poes.setBounds(addLabel(20, 60, "PoE Values"), 60, 150, 30);
		add(poes);
		
		isLogInterpolation = new JCheckBox();
		isLogInterpolation.setBounds(addLabel(20, 100, "Logarithmic?"), (int)poes.getBounds().getMaxY() + 10, 30, 30);
		add(isLogInterpolation);
		
		calculate = new JButton("Run");
		calculate.setBounds((int)poes.getBounds().getMaxX() - 100, (int)isLogInterpolation.getBounds().getMaxY() + 10, 100, 30);
		calculate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					StringTokenizer st = new StringTokenizer(poes.getText());
					double [] poeValues = new double[st.countTokens()];
					boolean isLog = isLogInterpolation.isSelected();
					
					for(int i = 0; i<poeValues.length; i++) {
						poeValues[i] = Double.parseDouble(st.nextToken());
						poeValues[i] = Math.round((-50 / Math.log(1 - (poeValues[i]))));
					}
					
					String path = source.getText();
					
					if(path.trim().length() == 0) {
						throw new Exception("Enter source file path");
					}
					
					File f = new File(path);
					if(f.isDirectory()) {
						for(File c : f.listFiles()) {
							if(c.getName().endsWith(".txt")) {
								InterpolationCalc.calculate(c.getAbsolutePath(), poeValues, isLog);
							}
						}
					} else {
						InterpolationCalc.calculate(path, poeValues, isLog);
					}
					JOptionPane.showMessageDialog(poes.getParent(), "Success!", "Success!", JOptionPane.INFORMATION_MESSAGE);
				} catch(Exception exc) {
					JOptionPane.showMessageDialog(poes.getParent(), exc.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		add(calculate);
	}
	@Override
	public void clean() {
		
	}

}

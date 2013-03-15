package org.openquake.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class OqToolsUi extends JFrame {

	private static final long serialVersionUID = -6282487180223051451L;
	
	private InputFileGeneratorsUi inputFileGenerator;
	private ShapeFileParserUi shapeFileParser;
	private InterpolateUi interpolate;
	
	public OqToolsUi() {
		super("OpenQuake Tools");
		initWidgets();
	}

	private void initWidgets() {
		setLayout(null);
		
		inputFileGenerator = new InputFileGeneratorsUi();
		inputFileGenerator.setSize(1024, 768);
		inputFileGenerator.setVisible(false);
		add(inputFileGenerator);
		
		shapeFileParser = new ShapeFileParserUi();
		shapeFileParser.setSize(1024, 768);
		shapeFileParser.setVisible(false);
		add(shapeFileParser);
		
		interpolate = new InterpolateUi();
		interpolate.setSize(1024, 768);
		interpolate.setVisible(false);
		add(interpolate);
		
		JMenu menu = new JMenu("New");
		JMenuItem menuItem = new JMenuItem("Input File Generator");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPanel(inputFileGenerator);
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Shape File Parser");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPanel(shapeFileParser);
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Interpolation");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPanel(interpolate);
			}
		});
		menu.add(menuItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		
		setJMenuBar(menuBar);
		setSize(1024, 768);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void showPanel(OqPanel panel) {
		panel.clean();
		inputFileGenerator.setVisible(panel == inputFileGenerator);
		shapeFileParser.setVisible(panel == shapeFileParser);
		interpolate.setVisible(panel == interpolate);
	}

	public static void main(String[] args) {
		OqToolsUi ui = new OqToolsUi();
		ui.setVisible(true);
	}
}

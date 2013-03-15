package org.openquake.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openquake.config.ConfigFileOperations;
import org.openquake.config.ConfigParameter;
import org.openquake.config.Coordinate;
import org.openquake.config.ParameterDeserializer;
import org.openquake.config.ParameterSerializer;
import org.openquake.generator.Area;
import org.openquake.generator.Attenuation;
import org.openquake.generator.SourceModel;

public class InputFileGeneratorsUi extends OqPanel {

	private static final long serialVersionUID = -253969101501512316L;
	
	private JFileChooser directoryChooser;
	private JFileChooser fileChooser;
	private HashMap<String, SourceModel> listData;
	
	//input fields
	private JTextField rootPath;
	private JTextField directoryName;
	
	private JList sourceModels, areas, attenuations;
	private JComboBox sourceModelCombo;
	private JComboBox attenuationsCombo;
	private JComboBox areaTypeCombo;
	private JList coordinates;
	private JTextField lat, lon;
	private JTextField gridSpacing;
	
	private JTextField periodInput;
	private JList periodList;
	
	private JTextField poes;
	private JTextField investigationTime;
	
	private JCheckBox oqVersion;
	private JTextField quantiles;
	
	public InputFileGeneratorsUi() {
		super();
		listData = new HashMap<String, SourceModel>();
		initialize();
	}

	private void initialize() {
		setLayout(null);
		
		directoryChooser = new JFileChooser();
		directoryChooser.setMultiSelectionEnabled(false);
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		attenuationsCombo = new JComboBox(new DefaultComboBoxModel());
		fillAttenuationsCombo();
		
		sourceModelCombo = new JComboBox(new DefaultComboBoxModel());
		fillSourceModelCombo();
		
		areaTypeCombo = new JComboBox(new DefaultComboBoxModel());
		fillAreaTypeCombo();
		
		initializeDirectoryParameters();
		
		initializeLists();
		
		initializePopupMenus();
		
		initializeCoordinates();
		
		initializePeriods();
		
		initializeOtherParameters();
		
		initializeGlobalButtons();
	}
	
	private void initializeOtherParameters() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder("Other Parameters"));
		
		poes = new JTextField();
		poes.setBounds(addLabel(panel, 10, 20, "PoE"), 20, 150, 30);
		panel.add(poes);
		
		investigationTime = new JTextField();
		investigationTime.setBounds(addLabel(panel, 10, 60, "Invest. Time"), 60, 100, 30);
		panel.add(investigationTime);
		
		oqVersion = new JCheckBox();
		oqVersion.setBounds(addLabel(panel, 10, 100, "Version 0.8.*?"), 100, 30, 30);
		panel.add(oqVersion);
		
		quantiles = new JTextField();
		quantiles.setBounds(addLabel(panel, 10, 140, "Quantiles"), 140, 100, 30);
		panel.add(quantiles);
		
		panel.setBounds(640, 130, 350, 180);
		add(panel);
	}

	private void initializePeriods() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder("Period"));
		
		periodInput = new JTextField();
		periodInput.setBounds(addLabel(panel, 10, 20, "Period"), 20, 100, 30);
		panel.add(periodInput);
		
		JButton addButton = new JButton("Add");
		addButton.setBounds(10, 60, 80, 30);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel model = (DefaultListModel) periodList.getModel();
				
				try {
					Double number = Double.parseDouble(periodInput.getText().trim());
					if(!model.contains(number)) {
						model.addElement(number);
					}
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(coordinates.getParent(), "Incorrect period data : " + exc.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		panel.add(addButton);
		
		JButton removeButton = new JButton("Remove");
		removeButton.setBounds((int) periodInput.getBounds().getMinX(), 60, 80, 30);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = periodList.getSelectedIndex();
				if(index > -1) {
					DefaultListModel model = (DefaultListModel) periodList.getModel();
					if(index < model.getSize()) {
						model.removeElementAt(index);
					}
				}
			}
		});
		panel.add(removeButton);
		
		periodList = new JList(new DefaultListModel());
		JScrollPane scroll = new JScrollPane(periodList);
		scroll.setBounds((int)periodInput.getBounds().getMaxX() + 10, 20, 100, 70);
		panel.add(scroll);
		
		panel.setBounds(640, 10, 350, 100);
		add(panel);
	}

	private void initializeGlobalButtons() {
		JButton save = new JButton("Save");
		save.setBounds(650, 540, 100, 30);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showSaveDialog(coordinates.getParent());
				if(result == JFileChooser.APPROVE_OPTION) {
					File targetFile = fileChooser.getSelectedFile();
					HashMap<ConfigParameter, String> parameters = prepareInputParameters();
					
					try {
						ConfigFileOperations.save(targetFile, parameters);
						JOptionPane.showMessageDialog(coordinates.getParent(), "Success!", "Success!", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(coordinates.getParent(), exc.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		add(save);
		
		JButton load = new JButton("Load");
		load.setBounds(650, 580, 100, 30);
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showOpenDialog(coordinates.getParent());
				if(result == JFileChooser.APPROVE_OPTION) {
					File sourceFile = fileChooser.getSelectedFile();
					
					try {
						HashMap<ConfigParameter, String> parameters = ConfigFileOperations.read(sourceFile);
						
						loadParameters(parameters);
						JOptionPane.showMessageDialog(coordinates.getParent(), "Success!", "Success!", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(coordinates.getParent(), exc.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}

		});
		add(load);
		
		JButton execute = new JButton("Execute");
		execute.setBounds(650, 620, 100, 30);
		execute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HashMap<ConfigParameter, String> parameters = prepareInputParameters();
				parameters.put(ConfigParameter.ROOT_PATH, rootPath.getText().trim());
				
				try {
					InputFileGeneratorController.generateInputFiles(parameters, listData);
					
					JOptionPane.showMessageDialog(coordinates.getParent(), "Input file generation successfully finished!", "Success!", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(coordinates.getParent(), exc.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		add(execute);
	}
	
	private void loadParameters(HashMap<ConfigParameter, String> parameters) {
		directoryName.setText(parameters.get(ConfigParameter.DIRECTORY_NAME));
		gridSpacing.setText(parameters.get(ConfigParameter.REGION_GRID_SPACING));
		poes.setText(parameters.get(ConfigParameter.POES));
		investigationTime.setText(parameters.get(ConfigParameter.INVESTIGATION_TIME));
		quantiles.setText(parameters.get(ConfigParameter.QUANTILE_LEVELS));
		
		if(parameters.containsKey(ConfigParameter.WORKAROUND_1027041)) {
			oqVersion.setSelected(true);
		}
		
		Vector<Double> periodListData = ParameterDeserializer.getPeriodList(parameters.get(ConfigParameter.PERIOD));
		DefaultListModel periodListModel = (DefaultListModel) periodList.getModel();
		periodListModel.clear();
		for(int i = 0; i<periodListData.size(); ++i) {
			periodListModel.addElement(periodListData.get(i));
		}
		listData = ParameterDeserializer.getAlgorithmMap(parameters.get(ConfigParameter.ALGORITHMS));
		clearLists();
		Vector<Coordinate> coordinateList = ParameterDeserializer.getCoordinateList(parameters.get(ConfigParameter.REGION_VERTEX));
		
		DefaultListModel model = (DefaultListModel) coordinates.getModel();
		model.removeAllElements();
		for(int i = 0; i<coordinateList.size(); ++i) {
			model.addElement(coordinateList.get(i));
		}
	}
	
	private HashMap<ConfigParameter, String> prepareInputParameters() {
		HashMap<ConfigParameter, String> parameters = new HashMap<ConfigParameter, String>();
		
		parameters.put(ConfigParameter.DIRECTORY_NAME, directoryName.getText().trim());
		parameters.put(ConfigParameter.REGION_GRID_SPACING, gridSpacing.getText().trim());
		parameters.put(ConfigParameter.ALGORITHMS, ParameterSerializer.getRawListData(listData));
		parameters.put(ConfigParameter.PERIOD, ParameterSerializer.getRawArray(((DefaultListModel)periodList.getModel()).toArray()));
		parameters.put(ConfigParameter.POES, poes.getText().trim());
		parameters.put(ConfigParameter.INVESTIGATION_TIME, investigationTime.getText().trim());
		parameters.put(ConfigParameter.QUANTILE_LEVELS, quantiles.getText().trim());
		
		if(oqVersion.isSelected()) {
			parameters.put(ConfigParameter.WORKAROUND_1027041, "true");
		}
	
		Vector<Coordinate> inputCoordinates = new Vector<Coordinate>();
		DefaultListModel model = (DefaultListModel) coordinates.getModel();
		for(int i = 0; i<model.getSize(); ++i) {
			inputCoordinates.add((Coordinate)model.elementAt(i));
		}
		parameters.put(ConfigParameter.REGION_VERTEX, ParameterSerializer.getRawCoordinates(inputCoordinates));
		
		return parameters;
	}

	private void initializeDirectoryParameters() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Paths"));
		panel.setLayout(null);
		
		rootPath = new JTextField();
		rootPath.setBounds(addLabel(panel, 20, 20, "Target Path"), 20, 200, 30);
		rootPath.setEditable(false);
		rootPath.setDisabledTextColor(Color.BLACK);
		panel.add(rootPath);
		
		JButton browse = new JButton("Browse");
		browse.setBounds((int)rootPath.getBounds().getMaxX() + 10, 20, 100, 30);
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = directoryChooser.showSaveDialog(rootPath);
				if(result == JFileChooser.APPROVE_OPTION) {
					File selectedDir = directoryChooser.getSelectedFile();
					rootPath.setText(selectedDir.getAbsolutePath());
				}
			}
		});
		panel.add(browse);
		
		directoryName = new JTextField();
		directoryName.setBounds(addLabel(panel, 20, 60, "Directory Name"), 60, 200, 30);
		panel.add(directoryName);
		
		panel.setBounds(20, 10, 600, 100);
		
		add(panel);
	}
	
	private void initializeCoordinates() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder("Region"));
		
		lat = new JTextField();
		lat.setBounds(addLabel(panel, 20, 20, "Lat."), 20, 100, 30);
		panel.add(lat);
		
		lon = new JTextField();
		lon.setBounds(addLabel(panel, 20, 60, "Lon."), 60, 100, 30);
		panel.add(lon);
		
		JButton button = new JButton("Add");
		button.setBounds(120, 100, 80, 30);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double latVal, lonVal;
				try {
					latVal = Double.parseDouble(lat.getText().trim());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(lat, "Invalid latitude value", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					lonVal = Double.parseDouble(lon.getText().trim());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(lon, "Invalid longitude value", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Coordinate c = new Coordinate();
				c.latitude = latVal;
				c.longitude = lonVal;
				
				((DefaultListModel)(coordinates.getModel())).addElement(c);
				lat.setText("");
				lon.setText("");
			}
		});
		panel.add(button);
		
		coordinates = new JList(new DefaultListModel());
		JScrollPane scroll = new JScrollPane(coordinates);
		scroll.setBounds(250, 20, 200, 150);
		panel.add(scroll);
		
		JPopupMenu popup = new JPopupMenu();
		JMenuItem popupMenuItem = new JMenuItem("Remove Coordinate");
		popupMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = coordinates.getSelectedIndex();
				if(selectedIndex >= 0) {
					((DefaultListModel)(coordinates.getModel())).remove(selectedIndex);
				}
			}
		});
		popup.add(popupMenuItem);
		coordinates.addMouseListener(new PopupListener(popup));
		popup.addMouseListener(new PopupListener(popup));
		
		gridSpacing = new JTextField();
		gridSpacing.setBounds(addLabel(panel, 20, 180, "Grid Spacing"), 180, 100, 30);
		panel.add(gridSpacing);
		
		panel.setBounds(20, 420, 600, 230);
		add(panel);
	}

	private void initializeLists() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		
		JLabel label = new JLabel("Source Model");
		label.setBounds(20, 20, 150, 30);
		panel.add(label);
		
		sourceModels = new JList(new DefaultListModel());
		sourceModels.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fillAreaList();
			}
		});
		
		JScrollPane scroll = new JScrollPane(sourceModels);
		scroll.setBounds(20, 50, 150, 200);
		panel.add(scroll);
		
		label = new JLabel("Area");
		label.setBounds(220, 20, 150, 30);
		panel.add(label);
		areas = new JList(new DefaultListModel());
		areas.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fillAttenuationList();
			}
		});	
		scroll = new JScrollPane(areas);
		scroll.setBounds(220, 50, 150, 200);
		panel.add(scroll);
		
		label = new JLabel("Attenuation");
		label.setBounds(420, 20, 150, 30);
		panel.add(label);
		attenuations = new JList(new DefaultListModel());
		scroll = new JScrollPane(attenuations);
		scroll.setBounds(420, 50, 150, 200);
		panel.add(scroll);
		
		panel.setBounds(20, 130, 600, 270);
		
		add(panel);
	}
	
	private void initializePopupMenus() {
		initializeSourceModelPopupMenu();
		
		initializeAreaPopupMenu();
		
		initializeAttenuationPopupMenu();
	}
	
	private void initializeSourceModelPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem popupMenuItem = new JMenuItem("Add Model");
		popupMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String data = JOptionPane.showInputDialog("Source Model Name : ");
				if(data != null) {
					data = data.trim();
					sourceModelCombo.setSelectedIndex(0);
					int res = JOptionPane.showConfirmDialog(attenuations, sourceModelCombo, "Select Source Model Type : ", JOptionPane.OK_CANCEL_OPTION);
					
					if(res == JOptionPane.OK_OPTION) {
						String type = sourceModelCombo.getSelectedItem().toString();
						SourceModel model = new SourceModel(data, type);
						
						if(data.length() > 0 && !listData.containsKey(data)) {
							listData.put(data, model);
							((DefaultListModel) sourceModels.getModel()).addElement(model);
						}
					}
					
				}
			}
		});
		popupMenu.add(popupMenuItem);
		
		popupMenuItem = new JMenuItem("Remove Model");
		popupMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SourceModel selectedModel = (SourceModel) sourceModels.getSelectedValue();
				if(selectedModel != null) {
					listData.remove(selectedModel.getName());
					clearLists();
				}
			}
		});
		popupMenu.add(popupMenuItem);
		sourceModels.addMouseListener(new PopupListener(popupMenu));
		popupMenu.addMouseListener(new PopupListener(popupMenu));
	}
	
	private void initializeAreaPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Add Area");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SourceModel model = (SourceModel) sourceModels.getSelectedValue();
				
				if(model != null) {
					String data = JOptionPane.showInputDialog("Area Name : ");
					
					if(data != null) {
						data = data.trim();
						
						if(data.length() > 0) {
							areaTypeCombo.setSelectedIndex(0);
							int res = JOptionPane.showConfirmDialog(areas, areaTypeCombo, "Select Area Type: ", JOptionPane.OK_CANCEL_OPTION);
							
							if(res == JOptionPane.OK_OPTION) {
								String areaType = areaTypeCombo.getSelectedItem().toString();
								Area area = new Area();
								area.setName(data);
								area.setType(areaType);
								
								model.getAreas().put(data, area);
								((DefaultListModel)areas.getModel()).addElement(area);
							}
							
						}
					}
				}
			}
		});
		popupMenu.add(item);
		
		item = new JMenuItem("Remove Area");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SourceModel model = (SourceModel) sourceModels.getSelectedValue();
				Area area = (Area) areas.getSelectedValue();
				
				if(model != null && area != null) {
					model.getAreas().remove(area.getName());
					clearLists();
				}
			}
		});
		
		popupMenu.add(item);
		areas.addMouseListener(new PopupListener(popupMenu));
		popupMenu.addMouseListener(new PopupListener(popupMenu));
	}
	
	private void initializeAttenuationPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Add Attenuation");
		
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(attenuations, attenuationsCombo, "Select Attenuation : ", JOptionPane.OK_CANCEL_OPTION);
				if(res == JOptionPane.OK_OPTION) {
					SourceModel source = (SourceModel)sourceModels.getSelectedValue();
					Area area = (Area)areas.getSelectedValue();
					String data = (String)attenuationsCombo.getSelectedItem();
					if(source != null && area != null) {
						Attenuation atten = area.getAttenuations().get(data);
						if(atten == null) {
							atten = new Attenuation();
							atten.setName(data);
							area.getAttenuations().put(data, atten);
							
							((DefaultListModel)attenuations.getModel()).addElement(atten);
						}
					}
				}
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Remove Attenuation");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Area area = (Area) areas.getSelectedValue();
				Attenuation atten = (Attenuation)attenuations.getSelectedValue();
				
				if(area != null && atten != null) {
					area.getAttenuations().remove(atten.getName());
					((DefaultListModel) attenuations.getModel()).removeElement(atten);
				}
			} 
		});
		menu.add(item);
		
		menu.add(item);
		attenuations.addMouseListener(new PopupListener(menu));
		menu.addMouseListener(new PopupListener(menu));
	}
	
	private void fillAreaTypeCombo() {
		DefaultComboBoxModel model = (DefaultComboBoxModel) areaTypeCombo.getModel();
		model.addElement("Point Sources");
		model.addElement("Line Sources (random or given strike)");
	}
	
	private void fillSourceModelCombo() {
		DefaultComboBoxModel model = (DefaultComboBoxModel) sourceModelCombo.getModel();
		model.addElement("Active Shallow Crust");
		model.addElement("Stable Shallow Crust"); 
		model.addElement("Subduction Interface");
		model.addElement("Subduction IntraSlab");
	}
	
	private void fillAttenuationsCombo() {
		DefaultComboBoxModel model = (DefaultComboBoxModel) attenuationsCombo.getModel();
		model.addElement("AB_2003_AttenRel");
		model.addElement("AS_1997_AttenRel");
		model.addElement("AS_2008_AttenRel");
		model.addElement("AW_2010_AttenRel");
		model.addElement("Abrahamson_2000_AttenRel");
		model.addElement("AkB_2010_AttenRel");
		model.addElement("BA_2008_AttenRel");
		model.addElement("BC_2004_AttenRel");
		model.addElement("BJF_1997_AttenRel");
		model.addElement("BS_2003_AttenRel");
		model.addElement("BW_1997_AttenRel");
		model.addElement("CB_2003_AttenRel");
		model.addElement("CB_2008_AttenRel");
		model.addElement("CF_2008_AttenRel");
		model.addElement("CS_2005_AttenRel");
		model.addElement("CY_2008_AttenRel");
		model.addElement("Campbell_1997_AttenRel");
		model.addElement("DahleEtAl_1995_AttenRel");
		model.addElement("Field_2000_AttenRel");
		model.addElement("GouletEtAl_2006_AttenRel");
		model.addElement("LL_2008_AttenRel");
		model.addElement("McVerryetal_2000_AttenRel");
		model.addElement("SEA_1999_AttenRel");
		model.addElement("SadighEtAl_1997_AttenRel");
		model.addElement("Sokolov_2008_AttenRel");
		model.addElement("YoungsEtAl_1997_AttenRel");
		model.addElement("ZhaoEtAl_2006_AttenRel");
	}

	private void fillAttenuationList() {
		SourceModel sourceModel = (SourceModel) sourceModels.getSelectedValue();
		Area area = (Area) areas.getSelectedValue();
		DefaultListModel attenuationsListModel = (DefaultListModel) attenuations.getModel();
		attenuationsListModel.clear();
		
		if(sourceModel != null && area != null) {
			Iterator<String> iter = area.getAttenuations().keySet().iterator();
			
			while(iter.hasNext()) {
				attenuationsListModel.addElement(area.getAttenuations().get(iter.next()));
			}
		}
	}
	
	private void fillAreaList() {
		((DefaultListModel)areas.getModel()).clear();
		((DefaultListModel)attenuations.getModel()).clear();
		
		SourceModel sourceModel = (SourceModel) sourceModels.getSelectedValue();
		DefaultListModel areaListModel = (DefaultListModel) areas.getModel();
		
		if(sourceModel != null) {
			Iterator<String> iter = sourceModel.getAreas().keySet().iterator();
			while(iter.hasNext()) {
				areaListModel.addElement(sourceModel.getAreas().get(iter.next()));
			}
		}
	}
	
	//TODO: duzeltilecek
	private void clearLists() {
		((DefaultListModel)sourceModels.getModel()).clear();
		((DefaultListModel)areas.getModel()).clear();
		((DefaultListModel)attenuations.getModel()).clear();
		
		for(Iterator<String> iter = listData.keySet().iterator(); iter.hasNext(); ) {
			String sourceModelName = iter.next();
			((DefaultListModel)(sourceModels.getModel())).addElement(listData.get(sourceModelName));
		}
	}

	@Override
	public void clean() {
		rootPath.setText("");
		directoryName.setText("");
		lat.setText("");
		lon.setText("");
		gridSpacing.setText("");
		periodInput.setText("");
		poes.setText("");
		
		listData = new HashMap<String, SourceModel>();
		clearLists();
	}
	
	private static class PopupListener extends MouseAdapter {
		private JPopupMenu popup;
		public PopupListener(JPopupMenu popup) {
			this.popup = popup;
		}
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            popup.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}
	
}

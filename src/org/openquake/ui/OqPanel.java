package org.openquake.ui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class OqPanel extends JPanel {
	
	private static final long serialVersionUID = 3577981116298775685L;

	protected int addLabel(int x, int y, String labelStr) {
		return addLabel(this, x, y, labelStr);
	}
	
	protected int addLabel(JComponent comp, int x, int y, String labelStr) {
		JLabel label = new JLabel(labelStr); 
		label.setBounds(x, y, 100, 30);
		comp.add(label);
		
		return x + 100;
	}

	public abstract void clean();
	
}

package gr.keeper.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.ImageIcon;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.border.MatteBorder;

public class GraphicalInterface {

	private JFrame frame;
	private JPanel panel;
	private JComboBox modelComboBox;
	private JComboBox resolutionComboBox;
	private JComboBox framesComboBox;
	private JComboBox bitRateComboBox;
	private JButton insertButton = new JButton(
			"\u03A0\u03C1\u03BF\u03C3\u03B8\u03AE\u03BA\u03B7");
	private JTextField quantityTextField;
	private JLabel calculationLabel;
	private JLabel totalLbl;
	private JTable table;
	private Long totalMB = (long) 0;
	private DataLoader loader;
	protected static DataCopyWorker dataCopyWorker;
	private JProgressBar progressBar;
	private JPanel imagePanel;
	private JLabel imageLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					for (LookAndFeelInfo info : UIManager
							.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (Exception e) {
					// If Nimbus is not available, you can set the GUI to
					// another look and feel.
				}
				try {
					GraphicalInterface window = new GraphicalInterface();
					window.frame.pack();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GraphicalInterface() {
		dataCopyWorker = new DataCopyWorker();
		dataCopyWorker.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO Auto-generated method stub
				if ("progress".equals(evt.getPropertyName())) {
	                 progressBar.setValue((Integer)evt.getNewValue());
	             }
			}
			
		});
		dataCopyWorker.execute();		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("KEEPER camera video size calculator");
		frame.setBounds(100, 100, 903, 605);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(192, 192, 192), null));
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		progressBar = new JProgressBar(0,100);
		progressBar.setOpaque(true);
		/*progressBar.setStringPainted(true);*/

		table = new JTable(0, 7);
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
				"A/A", "\u039C\u03BF\u03BD\u03C4\u03AD\u03BB\u03BF",
				"\u0391\u03BD\u03AC\u03BB\u03C5\u03C3\u03B7", "Frames",
				"Bit Rate", "\u03A0\u03BF\u03C3\u03CC\u03C4\u03B7\u03C4\u03B1",
				"MB/24\u03C9\u03C1\u03BF" }) {
			Class[] columnTypes = new Class[] { String.class, String.class,
					Object.class, Object.class, Object.class, Object.class,
					Object.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
		table.getColumnModel().getColumn(0).setMaxWidth(75);
		table.getColumnModel().getColumn(1).setPreferredWidth(105);
		table.getColumnModel().getColumn(1).setMaxWidth(155);
		table.getColumnModel().getColumn(2).setPreferredWidth(105);
		table.getColumnModel().getColumn(2).setMaxWidth(155);
		table.getColumnModel().getColumn(3).setPreferredWidth(55);
		table.getColumnModel().getColumn(3).setMaxWidth(65);
		table.getColumnModel().getColumn(4).setMaxWidth(155);
		table.getColumnModel().getColumn(5).setPreferredWidth(70);
		table.getColumnModel().getColumn(5).setMaxWidth(75);
		table.getColumnModel().getColumn(6).setPreferredWidth(95);
		table.getColumnModel().getColumn(6).setMinWidth(75);
		table.getColumnModel().getColumn(6).setMaxWidth(155);
		table.setAutoCreateRowSorter(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JTableHeader header = table.getTableHeader();
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) header
				.getDefaultRenderer();
		renderer.setHorizontalAlignment(SwingConstants.LEFT);

		JScrollPane scroller = new JScrollPane(table);
		scroller.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		scroller.getViewport().setBackground(Color.darkGray);
		/* table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); */

		JLabel label = new JLabel(
				" \u039C\u03BF\u03BD\u03C4\u03AD\u03BB\u03BF ");
		label.setFont(new Font("SansSerif", Font.BOLD, 14));
		JLabel label_1 = new JLabel(
				" \u0391\u03BD\u03AC\u03BB\u03C5\u03C3\u03B7 ");
		label_1.setFont(new Font("SansSerif", Font.BOLD, 14));

		JLabel lblFrames = new JLabel(" Frames ");
		lblFrames.setFont(new Font("SansSerif", Font.BOLD, 14));

		JLabel lblBitRate = new JLabel(" Bit Rate ");
		lblBitRate.setFont(new Font("SansSerif", Font.BOLD, 14));

		JLabel label_2 = new JLabel(
				" \u03A0\u03BF\u03C3\u03CC\u03C4\u03B7\u03C4\u03B1 ");
		label_2.setFont(new Font("SansSerif", Font.BOLD, 14));

		JLabel lblMb = new JLabel(" MB/24\u03C9\u03C1\u03BF ");
		lblMb.setFont(new Font("SansSerif", Font.BOLD, 14));
		insertButton.setFont(new Font("SansSerif", Font.PLAIN, 14));

		insertButton.setEnabled(false);

		modelComboBox = new JComboBox();
		modelComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
		modelComboBox.setName("modelComboBox");

		modelComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent ev) {
				// TODO Auto-generated method stub
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					String tag = "enableResolutions";
					String selectedModel = (String) modelComboBox
							.getSelectedItem();
					updateValues(tag, loader.findResolutionRange(selectedModel));
				}
			}
		});

		resolutionComboBox = new JComboBox();
		resolutionComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
		resolutionComboBox.setName("resolutionComboBox");

		resolutionComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				// TODO Auto-generated method stub
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					String tag = "enableFrames";
					String selectedResolution = (String) resolutionComboBox
							.getSelectedItem();
					String selectedModel = (String) modelComboBox
							.getSelectedItem();
					updateValues(tag, loader.findFrameRange(selectedModel,
							selectedResolution));
				}
			}

		});
		resolutionComboBox.setEnabled(false);

		framesComboBox = new JComboBox();
		framesComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
		framesComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				// TODO Auto-generated method stub
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					String tag = "enableBitrate";
					String selectedResolution = (String) resolutionComboBox
							.getSelectedItem();
					String selectedFps = (String) framesComboBox
							.getSelectedItem();
					updateValues(tag, loader.findBitrateRange(selectedFps,
							selectedResolution));
				}
			}

		});
		framesComboBox.setEnabled(false);

		bitRateComboBox = new JComboBox();
		bitRateComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
		bitRateComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				// TODO Auto-generated method stub
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					String tag = "enableQuantity";
					String selectedBitrate = (String) bitRateComboBox
							.getSelectedItem();
					ArrayList<String> values = new ArrayList<String>();
					if (selectedBitrate == " -- ") {
						values = null;
					}
					updateValues(tag, values);
				}
			}

		});

		bitRateComboBox.setEnabled(false);

		calculationLabel = new JLabel("--");
		calculationLabel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED,
				null, null, null, null));
		calculationLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		quantityTextField = new JTextField("1");
		quantityTextField.setFont(new Font("SansSerif", Font.PLAIN, 14));
		quantityTextField.setHorizontalAlignment(SwingConstants.CENTER);
		quantityTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent ev) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent ev) {
				// TODO Auto-generated method stub

				String kbpsString = (String) bitRateComboBox.getSelectedItem();

				if (Character.valueOf(ev.getKeyChar()).equals(null)) {
					calculationLabel.setText("--");
				} else if (Character.isDigit(ev.getKeyChar())) {
					long kbpsNum = Long.parseLong(kbpsString);
					if (Integer.parseInt(quantityTextField.getText()) <= 128) {
						double mbPerDay = (kbpsNum * 3600 * 24 * Long
								.parseLong(quantityTextField.getText()))
								/ (8 * 1024);
						calculationLabel.setText(String.valueOf(Math
								.round(mbPerDay)));
					} else {
						quantityTextField.setText("128");
						double mbPerDay = (kbpsNum * 3600 * 24 * Long
								.parseLong(quantityTextField.getText()))
								/ (8 * 1024);
						calculationLabel.setText(String.valueOf(Math
								.round(mbPerDay)));
					}

				} else {
					quantityTextField.setText("");
					calculationLabel.setText("--");
				}
			}

			@Override
			public void keyTyped(KeyEvent ev) {
				// TODO Auto-generated method stub

			}

		});

		quantityTextField.setEnabled(false);

		insertButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				ArrayList<String> tableRow = new ArrayList<String>();
				DefaultTableModel model = (DefaultTableModel) table.getModel();

				tableRow.add(Integer.toString(model.getRowCount() + 1));
				tableRow.add((String) modelComboBox.getSelectedItem());
				tableRow.add((String) resolutionComboBox.getSelectedItem());
				tableRow.add((String) framesComboBox.getSelectedItem());
				tableRow.add((String) bitRateComboBox.getSelectedItem());
				tableRow.add(quantityTextField.getText());
				tableRow.add(calculationLabel.getText());
				String tag = "insertToTable";
				updateValues(tag, tableRow);
				updateValues("enableResolutions",
						loader.findResolutionRange(" -- "));
				modelComboBox.setSelectedIndex(0); // It causes reseting the
													// comboboxes
				updateTotalMB();
			}

		});

		JButton removeButton = new JButton(
				"\u0394\u03B9\u03B1\u03B3\u03C1\u03B1\u03C6\u03AE");
		removeButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				if (table.getSelectedRow() >= 0) {
					model.removeRow(table.getSelectedRow());
					for (int i = 0; i < model.getRowCount(); i++) {
						model.setValueAt(i + 1, i, 0);
					}
					updateTotalMB();
				}
			}

		});

		totalLbl = new JLabel("--");
		totalLbl.setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		totalLbl.setFont(new Font("Tahoma", Font.BOLD, 16));

		JButton removeAllButton = new JButton(
				"\u0394\u03B9\u03B1\u03B3\u03C1\u03B1\u03C6\u03AE \u03CC\u03BB\u03C9\u03BD");
		removeAllButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
		removeAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = model.getRowCount() - 1; i >= 0; i--) {
					model.removeRow(i);
				}
				updateTotalMB();
			}

		});

		JLabel lblMb_1 = new JLabel(
				"\u03A3\u03C5\u03BD\u03BF\u03BB\u03B9\u03BA\u03AC MB \u03B1\u03BD\u03AC 24 \u03CE\u03C1\u03B5\u03C2:");
		lblMb_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		lblMb_1.setFont(new Font("Tahoma", Font.BOLD, 16));			
		

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(35)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblMb_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(totalLbl))
						.addComponent(scroller, GroupLayout.PREFERRED_SIZE, 676, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(modelComboBox, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
								.addComponent(label))
							.addGap(18)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(label_1)
								.addComponent(resolutionComboBox, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(framesComboBox, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblFrames))
							.addGap(32)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(bitRateComboBox, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblBitRate))
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(17)
									.addComponent(label_2))
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(18)
									.addComponent(quantityTextField, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)))
							.addGap(33)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(calculationLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblMb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(36)
							.addComponent(removeButton))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(34)
							.addComponent(insertButton))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(18)
							.addComponent(removeAllButton)))
					.addContainerGap(35, Short.MAX_VALUE))
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(721, Short.MAX_VALUE)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(26))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel.createSequentialGroup()
								.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblFrames)
									.addComponent(lblBitRate)
									.addComponent(label_2)
									.addComponent(lblMb))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
									.addComponent(framesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(bitRateComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(quantityTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(calculationLabel)))
							.addGroup(gl_panel.createSequentialGroup()
								.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
									.addComponent(label_1)
									.addComponent(label))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
									.addComponent(resolutionComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(modelComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addComponent(insertButton))
					.addGap(19)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(12)
							.addComponent(scroller, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(131)
							.addComponent(removeButton)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(removeAllButton)))
					.addGap(18)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMb_1)
						.addComponent(totalLbl))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(4))
		);
		gl_panel.setHonorsVisibility(false);
		panel.setLayout(gl_panel);		
		
		imagePanel = new JPanel();
		imagePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.LIGHT_GRAY, null));
		imagePanel.setBackground(Color.darkGray);
		frame.getContentPane().add(imagePanel, BorderLayout.NORTH);
		
		imageLabel = new JLabel("");
		imagePanel.add(imageLabel);
		imageLabel.setIcon(new ImageIcon(GraphicalInterface.class.getResource("/images/keeper_logo.png")));
	}

	public void updateValues(String tag, ArrayList<String> values) {

		switch (tag) {
		case "enableResolutions":
			if (values == null) {
				resolutionComboBox.removeAllItems();
				resolutionComboBox.setEnabled(false);
				framesComboBox.removeAllItems();
				framesComboBox.setEnabled(false);
				bitRateComboBox.removeAllItems();
				bitRateComboBox.setEnabled(false);
				quantityTextField.setText("1");
				quantityTextField.setEnabled(false);
				insertButton.setEnabled(false);
				return;
			}

			resolutionComboBox.removeAllItems();
			resolutionComboBox.addItem(" -- ");
			for (String value : values) {
				resolutionComboBox.addItem(value);
			}
			resolutionComboBox.setEnabled(true);
			break;
		case "enableFrames":
			if (values == null) {
				framesComboBox.removeAllItems();
				framesComboBox.setEnabled(false);
				bitRateComboBox.removeAllItems();
				bitRateComboBox.setEnabled(false);
				quantityTextField.setText("1");
				quantityTextField.setEnabled(false);
				insertButton.setEnabled(false);
				return;
			}

			framesComboBox.removeAllItems();
			framesComboBox.addItem(" -- ");
			for (String value : values) {
				framesComboBox.addItem(value);
			}
			framesComboBox.setEnabled(true);
			break;
		case "enableBitrate":
			if (values == null) {
				bitRateComboBox.removeAllItems();
				bitRateComboBox.setEnabled(false);
				quantityTextField.setText("1");
				quantityTextField.setEnabled(false);
				insertButton.setEnabled(false);
				return;
			}
			bitRateComboBox.removeAllItems();
			bitRateComboBox.addItem(" -- ");
			for (String value : values) {
				bitRateComboBox.addItem(value);
			}
			bitRateComboBox.setEnabled(true);
			break;
		case "enableQuantity":
			if (values == null) {
				quantityTextField.setText("1");
				quantityTextField.setEnabled(false);
				insertButton.setEnabled(false);
				return;
			}
			quantityTextField.setEnabled(true);
			insertButton.setEnabled(true);

			String kbpsString = (String) bitRateComboBox.getSelectedItem();
			int kbpsNum = Integer.parseInt(kbpsString);
			int mbPerDay = (kbpsNum * 3600 * 24 * Integer
					.parseInt(quantityTextField.getText())) / (8 * 1024);
			calculationLabel.setText(String.valueOf(mbPerDay));
			break;
		case "insertToTable":
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			Vector<String> valuesVector = new Vector<String>(values);
			model.addRow(valuesVector);

		}
	}

	public void updateTotalMB() {
		if (table.getRowCount() == 0) {
			totalLbl.setText(" -- ");
		} else {
			String value = "";
			totalMB = (long) 0;
			for (int i = 0; i < table.getRowCount(); i++) {
				value = (String) table.getValueAt(i, 6);
				totalMB += Long.parseLong(value);
			}
			totalLbl.setText(String.valueOf(totalMB));
		}
	}

	protected class DataCopyWorker extends SwingWorker<DataLoader, Void> {
		@Override
		protected DataLoader doInBackground() throws Exception {
			// TODO Auto-generated method stub
			DataLoader loader = null;
			try {
				setProgress(20);
				loader = new DataLoader();
				setProgress(50);
			} catch (Exception e) {

			}
			return loader;
		}

		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
			try {
				setProgress(70);
				loader = get();
				String[] models = loader.getModels();  // When the database is done getting copied
				setProgress(80);
				int prog = 80;
				// populate the model combo box
				for (String value : models) {
					modelComboBox.addItem(value);
					setProgress(prog ++);
				}				
				setProgress(100);
				
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								/*setProgress(0);*/
								progressBar.setVisible(false);
							}							
						});
					}
					
				}, 500);				
				
			} catch (Exception e) {

			}
		}
	}
}

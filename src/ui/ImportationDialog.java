package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Identifiant;
import util.SKUtils;

public class ImportationDialog extends JDialog {

	public static final int OK_BTN = 1;
	public static final int CANCEL_BTN = 2;// même valeur que DISPOSE_ON_CLOSE
	public static final int DEFAULT_BTN = 0;
	
	private static final int SEP_VIRGULE = 0;
	private static final int SEP_POINT_VIRGULE = 1;
	private static final int SEP_TABULATION = 2;
	
	private String filePath = null;
	private ArrayList<Identifiant> identifiants = null;
	private String separateur = ";";
	private boolean existingHeader = true;
	
	private static Object[] objToReturn = {DEFAULT_BTN, null, ";", true};
	
	private final String TITLE = "Importation des données";
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	private JPanel btnPane;
	private JPanel contentPane;
	
	
	private ImportationDialog(JFrame parent) {
		this.setTitle(TITLE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		
		initComponents();
		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		this.getContentPane().add(btnPane, BorderLayout.SOUTH);
		
		this.pack();
	}
	
	private void initComponents() {
		initContentPane();
		initBtnPane();
	}


	private void initContentPane() {
		contentPane = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JButton jb = new JButton("Cliquez pour sélectionner un fichier...");
//		jtf.setEditable(false);
		jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// initialise le jfilechooser
				JFileChooser jfc = new JFileChooser();
				FileNameExtensionFilter fneFilter = new FileNameExtensionFilter("Extensions .CSV et .TXT", "csv", "txt");
				jfc.setFileFilter(fneFilter);
				// affiche le jfilechooser et récupère le bouton cliqué
				int btnClicked = jfc.showOpenDialog(jb);
				
				if (btnClicked == JFileChooser.APPROVE_OPTION) {
					filePath = jfc.getSelectedFile().getAbsolutePath();
					jb.setText(filePath);
				}
			}
		});
		
		
		String[] separateurs = {"virgule", "point-virgule", "tabulation"};
		JComboBox<String> sepList = new JComboBox<>(separateurs);
		sepList.setSelectedIndex(SEP_POINT_VIRGULE );
		sepList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = sepList.getSelectedIndex();
				System.out.println(index);
				
				switch(index) {
					case SEP_VIRGULE:
						separateur = ",";
						break;
					case SEP_POINT_VIRGULE:
						separateur = ";";
						break;
					case SEP_TABULATION:
						separateur = "\t";
						break;
					default:
						System.out.println("l'indice dépasse le nombre de séparateurs.");
						break;
				}
			}
		});
		
		JCheckBox checkbox = new JCheckBox("(coché = oui)");
		checkbox.setSelected(true);
		checkbox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(((JCheckBox) e.getSource()).isSelected());
				existingHeader = ((JCheckBox) e.getSource()).isSelected();
			}
		});
		
		// champ fileChooser
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new JLabel("Choisir un fichier"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(jb, gbc);
		
		// champ Séparateur
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new JLabel("Séparateur"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(sepList, gbc);
		
		// champ Header
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new JLabel("Première ligne comme en-tête"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(checkbox, gbc);
	}


	private void initBtnPane() {
		btnPane = new JPanel();
		JButton btnOK = new JButton("Valider");
		JButton btnCancel = new JButton("Annuler");
		
		this.getRootPane().setDefaultButton(btnOK);
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				objToReturn[0] = OK_BTN;
				objToReturn[1] = filePath;
				objToReturn[2] = separateur;
				objToReturn[3] = existingHeader;
				
				if (objToReturn[1] == null || objToReturn[1].equals("")) {
					return;
				}
				else {
					ImportationDialog.this.dispose();
				}
			}				
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				objToReturn[0] = CANCEL_BTN;
				objToReturn[1] = null;
				objToReturn[2] = ";";
				objToReturn[3] = true;
				ImportationDialog.this.dispose();// ferme et détruit la boîte de dialogue
			}
		});
		
		btnPane.add(btnOK);
		btnPane.add(btnCancel);
	}


	public static Object[] open(JFrame parent) {
		objToReturn[0] = DEFAULT_BTN;
		objToReturn[1] = null;
		objToReturn[2] = ";";
		objToReturn[3] = true;
		ImportationDialog dial = new ImportationDialog(parent);
		dial.setVisible(true);
		return objToReturn;
	}
}

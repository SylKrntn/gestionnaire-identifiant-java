package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CsvExportationDialog extends JDialog {

	public static final int OK_BTN = 1;
	public static final int CANCEL_BTN = 2;// même valeur que DISPOSE_ON_CLOSE
	public static final int DEFAULT_BTN = 0;
	
	private static final int SEP_VIRGULE = 0;
	private static final int SEP_POINT_VIRGULE = 1;
	private static final int SEP_TABULATION = 2;
	
	private String filePath = null;
	private String separateur = ";";
	private String header = null;
	
	private static Object[] objToReturn = {DEFAULT_BTN, null, ";", null};
	
	private final String TITLE = "Exportation des données au format CSV";
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	private JPanel btnPane;
	private JPanel contentPane;
	private JTextField headerJtf;
	
	
	private CsvExportationDialog(JFrame parent) {
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
		
		// JFileChooser
		JButton jb = new JButton("Cliquez pour sélectionner un fichier...");
//		jtf.setEditable(false);
		jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// initialise le jfilechooser
				JFileChooser jfc = new JFileChooser();
				FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Extension .CSV", "csv");
				jfc.setFileFilter(txtFilter);
				// affiche le jfilechooser et récupère le bouton cliqué
				int btnClicked = jfc.showSaveDialog(jb);
				
				if (btnClicked == JFileChooser.APPROVE_OPTION) {
					filePath = jfc.getSelectedFile().getAbsolutePath();
					jb.setText(filePath);
				}
			}
		});
		
		// Séparateur
		String[] separateurs = {"virgule", "point-virgule", "tabulation"};
		JComboBox<String> sepList = new JComboBox<String>(separateurs);
		sepList.setSelectedIndex(SEP_POINT_VIRGULE );
		sepList.addActionListener(new ActionListener() {
			
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
		
		// champ header
		headerJtf  = new JTextField(12);
		
		// champ fileChooser
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new JLabel("Enregistrer dans un fichier"), gbc);
		
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
		contentPane.add(new JLabel("Définir les en-têtes de colonnes"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(headerJtf, gbc);
	}


	private void initBtnPane() {
		btnPane = new JPanel();
		JButton btnOK = new JButton("Valider");
		JButton btnCancel = new JButton("Annuler");
		
		this.getRootPane().setDefaultButton(btnOK);
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				header = headerJtf.getText();
				objToReturn[0] = OK_BTN;
				objToReturn[1] = filePath;
				objToReturn[2] = separateur;
				objToReturn[3] = header;
				
				if (objToReturn[1] == null || objToReturn[1].equals("")) {
					return;
				}
				else {
					CsvExportationDialog.this.dispose();
				}
			}				
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				objToReturn[0] = CANCEL_BTN;
				objToReturn[1] = null;
				objToReturn[2] = ";";
				objToReturn[3] = null;
				CsvExportationDialog.this.dispose();// ferme et détruit la boîte de dialogue
			}
		});
		
		btnPane.add(btnOK);
		btnPane.add(btnCancel);
	}


	public static Object[] open(JFrame parent) {
		objToReturn[0] = DEFAULT_BTN;
		objToReturn[1] = null;
		objToReturn[2] = ";";
		objToReturn[3] = null;
		CsvExportationDialog dial = new CsvExportationDialog(parent);
		dial.setVisible(true);
		return objToReturn;
	}
}

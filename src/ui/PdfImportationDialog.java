package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PdfImportationDialog extends JDialog {

	public static final int OK_BTN = 1;
	public static final int CANCEL_BTN = 2;// même valeur que DISPOSE_ON_CLOSE
	public static final int DEFAULT_BTN = 0;
	
	private static final int SEP_VIRGULE = 0;
	private static final int SEP_POINT_VIRGULE = 1;
	private static final int SEP_TABULATION = 2;
	
	private String filePath = null;
	private boolean existingHeader = true;
	
	private static Object[] objToReturn = {DEFAULT_BTN, null, true};
	
	private final String TITLE = "Importation des identifiants à partir d'un fichier PDF";
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	private JPanel btnPane;
	private JPanel contentPane;
	
	
	private PdfImportationDialog(JFrame parent) {
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
		jb.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// initialise le jfilechooser
				JFileChooser jfc = new JFileChooser();
				FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("Extensions .PDF", "pdf");
				jfc.setFileFilter(pdfFilter);
				// affiche le jfilechooser et récupère le bouton cliqué
				int btnClicked = jfc.showOpenDialog(jb);
				
				if (btnClicked == JFileChooser.APPROVE_OPTION) {
					filePath = jfc.getSelectedFile().getAbsolutePath();
					jb.setText(filePath);
				}
			}
		});
		
		JCheckBox checkbox = new JCheckBox("(coché = oui)");
		checkbox.setSelected(true);
		checkbox.addActionListener(new ActionListener() {
			
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
		
		// champ Header
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new JLabel("Première ligne comme en-tête"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
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
				objToReturn[2] = existingHeader;
				
				if (objToReturn[1] == null || objToReturn[1].equals("")) {
					return;
				}
				else {
					PdfImportationDialog.this.dispose();
				}
			}				
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				objToReturn[0] = CANCEL_BTN;
				objToReturn[1] = null;
				objToReturn[2] = true;
				PdfImportationDialog.this.dispose();// ferme et détruit la boîte de dialogue
			}
		});
		
		btnPane.add(btnOK);
		btnPane.add(btnCancel);
	}


	public static Object[] open(JFrame parent) {
		objToReturn[0] = DEFAULT_BTN;
		objToReturn[1] = null;
		objToReturn[2] = true;
		PdfImportationDialog dial = new PdfImportationDialog(parent);
		dial.setVisible(true);
		return objToReturn;
	}
}

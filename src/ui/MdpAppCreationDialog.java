package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import util.SKUtils;

public class MdpAppCreationDialog extends JDialog {
	public static final int OK_BTN = 1;
	public static final int CANCEL_BTN = 2;// même valeur que DISPOSE_ON_CLOSE
	public static final int DEFAULT_BTN = 0;
	private static Object[] objToReturn = {DEFAULT_BTN, null};
	private final String TITLE = "Creation du mot de passe applicatif";
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	private JPanel btnPane;
	private JPanel contentPane;
	private JPasswordField mdpJpf;
	private JPasswordField mdpConfJpf;
	
	private MdpAppCreationDialog() {
		this.setTitle(TITLE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		initComponents();
		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		this.getContentPane().add(btnPane, BorderLayout.SOUTH);
		
		this.pack();// dimensionne à la taille minimum nécessaire pour afficher les composants
	}

	private void initComponents() {
		initContentPane();
		initBtnPane();
	}

	private void initContentPane() {
		mdpJpf = new JPasswordField(12);
		mdpConfJpf = new JPasswordField(12);
		GridBagConstraints gbc = new GridBagConstraints();
		contentPane = new JPanel(new GridBagLayout());
		
		// Site
		gbc.gridx = 0;// numero cellule dans ligne
		gbc.gridy = 0;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new Label("Mot de passe"), gbc);
		
		gbc.gridx = 1;// numero cellule dans ligne
		gbc.gridy = 0;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(mdpJpf, gbc);
		
		// Login
		gbc.gridx = 0;// numero cellule dans ligne
		gbc.gridy = 1;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new Label("Confirmation du mot de passe"), gbc);
		
		gbc.gridx = 1;// numero cellule dans ligne
		gbc.gridy = 1;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(mdpConfJpf, gbc);
		
	}

	private void initBtnPane() {
		btnPane = new JPanel();
		JButton btnOK = new JButton("Valider");
		JButton btnCancel = new JButton("Annuler");
		
		this.getRootPane().setDefaultButton(btnOK);
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// récupère les valeurs des champs
				char[] mdp = mdpJpf.getPassword();
				char[] mdpConf = mdpConfJpf.getPassword();
				
				if (mdp.length == 0 || mdpConf.length == 0) {
					return;
				}
				System.out.println(mdp);
				System.out.println(mdpConf);
				// transforme les tableaux de caractère en String
				String mdpStr = SKUtils.charTabToString(mdp);
				String mdpConfStr = SKUtils.charTabToString(mdpConf);
				
				// Teste si les paramètres sont valides
				if (!SKUtils.isValid(mdpStr) || !SKUtils.isValid(mdpConfStr)) {
					System.out.println("ERREUR : paramètres invalides ! Impossible d'enregistrer la saisie");
					return;
				}
				
				// Si les paramètres sont valides, on vérifie qu'ils sont identiques
				if (!mdpStr.equals(mdpConfStr)) {
					System.out.println("ERREUR : les mots de passe saisis ne sont pas égaux ! Impossible d'enregistrer la saisie");
					return;
				}
				else {
					objToReturn[0] = OK_BTN;
					objToReturn[1] = mdpStr;
					dispose();
				}
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				objToReturn[0] = CANCEL_BTN;
				objToReturn[1] = null;
				MdpAppCreationDialog.this.dispose();// ferme et détruit la boîte de dialogue
			}
		});
		
		btnPane.add(btnOK);
		btnPane.add(btnCancel);
	}
	
	public static Object[] open() {
		objToReturn[0] = DEFAULT_BTN;
		objToReturn[1] = null;
		MdpAppCreationDialog dial = new MdpAppCreationDialog();
		dial.setVisible(true);
		return objToReturn;
	}
}

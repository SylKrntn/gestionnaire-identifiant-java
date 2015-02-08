package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import util.AppUtils;
/**
 * Classe qui est une fenêtre de dialogue permettant à l'utilisateur de modifier son mot de passe applicatif
 * @author Sainsain
 *
 */
public class AppPasswordAlterationDialog extends JDialog {
	public static final int OK_BTN = 1;
	public static final int CANCEL_BTN = 2;// même valeur que DISPOSE_ON_CLOSE
	public static final int DEFAULT_BTN = 0;

	private static Object[] objToReturn = {DEFAULT_BTN, null, null};
	private final String TITLE = "Modification du mot de passe applicatif";
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	private JPasswordField oldMdpJpf;
	private JPasswordField currentMdpJpf;
	private JPasswordField currentMdpConfJpf;
	private JPanel contentPane;
	private JPanel btnPane;

	private AppPasswordAlterationDialog() {
		this.setTitle(TITLE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		initComponents();
		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		this.getContentPane().add(btnPane, BorderLayout.SOUTH);
		
		this.pack();
	}

	private AppPasswordAlterationDialog(JFrame parent) {
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
	
	/**
	 * 
	 */
	private void initComponents() {
		initContentPane();
		initBtnPane();
	}
	
	/**
	 * 
	 */
	private void initContentPane() {
		oldMdpJpf = new JPasswordField(12);
		currentMdpJpf = new JPasswordField(12);
		currentMdpConfJpf = new JPasswordField(12);
		this.contentPane = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Ancien mot de passe
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		this.contentPane.add(new JLabel("Ancien mot de passe"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		this.contentPane.add(oldMdpJpf, gbc);
		
		// Nouveau mot de passe
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		this.contentPane.add(new JLabel("Nouveau mot de passe"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		this.contentPane.add(currentMdpJpf, gbc);
		
		// Confirmation du nouveau mot de passe
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 5, 5, 5);
		this.contentPane.add(new JLabel("Confirmation du nouveau mot de passe"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 5, 5, 5);
		this.contentPane.add(currentMdpConfJpf, gbc);
	}

	/**
	 * 
	 */
	private void initBtnPane() {
		this.btnPane = new JPanel();
		JButton btnOK = new JButton("Valider");
		JButton btnCancel = new JButton("Annuler");
		
		this.getRootPane().setDefaultButton(btnOK);
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// récupère les valeurs des champs
				char[] oldPass = oldMdpJpf.getPassword();
				char[] currentPass = currentMdpJpf.getPassword();
				char[] currentPassConf = currentMdpConfJpf.getPassword();
				// convertit les valeurs en String
				String oldMdp = AppUtils.charTabToString(oldPass);
				String currentMdp = AppUtils.charTabToString(currentPass);
				String currentMdpConf = AppUtils.charTabToString(currentPassConf);
				
				// Teste si les variables ne sont pas valides
				if (!AppUtils.isValid(oldMdp) && !AppUtils.isValid(currentMdp) && !AppUtils.isValid(currentMdpConf)) {
					System.out.println("Au moins un champ est mal saisi.");
					return;
				}
				
				// si les variables sont valides, on teste si le nouveau mot de passe et le mot de passe de confirmation sont différents
				if (!currentMdp.equals(currentMdpConf)) {
					System.out.println("le nouveau mot de passe et le mot de passe de confirmation sont différents.");
					return;
				}
				
				// s'ils sont identiques, on vérifie que l'ancien mot de passe au mot de passe aplicatif
				
				// sinon, tout est bon et on retourne le nouveau mot de passe
				objToReturn[0] = OK_BTN;
				objToReturn[1] = oldMdp;
				objToReturn[2] = currentMdp;
				AppPasswordAlterationDialog.this.dispose();
				
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				objToReturn[0] = CANCEL_BTN;
				objToReturn[1] = null;
				objToReturn[2] = null;
				AppPasswordAlterationDialog.this.dispose();// ferme et détruit la boîte de dialogue
			}
		});
		
		btnPane.add(btnOK);
		btnPane.add(btnCancel);
	}

	public static Object[] open(JFrame parent) {
		objToReturn[0] = DEFAULT_BTN;
		objToReturn[1] = null;
		AppPasswordAlterationDialog dial = new AppPasswordAlterationDialog(parent);
		dial.setVisible(true);
		return objToReturn;
	}
}// END class AppPasswordAlterationDialog

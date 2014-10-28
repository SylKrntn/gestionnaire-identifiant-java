package ui;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import util.MessageType;
import util.AppParams;
import util.AppUtils;
import model.Identifiant;
import model.Observable;
import model.Observer;
import model.dao.SQLiteDAO;

public class AddIdentifiantDialog extends JDialog  {
	private final String TITLE = "Ajout d'un identifiant";
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	private JTextField siteJtf;
	private JTextField loginJtf;
	private JPasswordField mdpJpf;
	private JPanel contentPane;
	private JPanel btnPane;
	
	public AddIdentifiantDialog() {
		this.setTitle(this.TITLE);
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		initComponents();
		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		this.getContentPane().add(btnPane, BorderLayout.SOUTH);
		
		this.pack();
		this.setVisible(true);
	}
	
	public AddIdentifiantDialog(JFrame parent) {
		super(parent);
		this.setTitle(this.TITLE);
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		initComponents();
		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		this.getContentPane().add(btnPane, BorderLayout.SOUTH);
		
		this.pack();
		this.setVisible(true);
	}

	private void initComponents() {
		initContentPane();
		initBtnPane();
	}

	private void initBtnPane() {
		btnPane = new JPanel();
		JButton btnOK = new JButton("Valider");
		JButton btnCancel = new JButton("Annuler");
		
		this.getRootPane().setDefaultButton(btnOK);
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// récupère les valeurs des champs
				String site = siteJtf.getText();
				String login = loginJtf.getText();
				char[] mdp = mdpJpf.getPassword();
				String mdpStr = AppUtils.charTabToString(mdp);
				
				// Teste si les paramètres ne sont pas valides
				if (!AppUtils.isValid(site) || !AppUtils.isValid(login) || !AppUtils.isValid(mdpStr)) {
					AppUtils.setConsoleMessage("ERREUR : paramètres invalides ! Impossible d'enregistrer la saisie", AddIdentifiantDialog.class, MessageType.ERROR, 88, AppParams.DEBUG_MODE);
					return;
				}
				
				// Si les paramètres sont valides, on les enregistre en BDD
				SQLiteDAO.getInstance().save(site, login, mdpStr);
				if (SQLiteDAO.getInstance().isDataSaved()) {
					AddIdentifiantDialog.this.dispose();// ferme et détruit la boîte de dialogue
				}
				
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddIdentifiantDialog.this.dispose();// ferme et détruit la boîte de dialogue
			}
		});
		
		btnPane.add(btnOK);
		btnPane.add(btnCancel);
	}

	private void initContentPane() {
		siteJtf = new JTextField(12);
		loginJtf = new JTextField(12);
		mdpJpf = new JPasswordField(12);
		GridBagConstraints gbc = new GridBagConstraints();
		contentPane = new JPanel(new GridBagLayout());
		
		// Site
		gbc.gridx = 0;// numero cellule dans ligne
		gbc.gridy = 0;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new Label("Site"), gbc);
		
		gbc.gridx = 1;// numero cellule dans ligne
		gbc.gridy = 0;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(siteJtf, gbc);
		
		// Login
		gbc.gridx = 0;// numero cellule dans ligne
		gbc.gridy = 1;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new Label("Login"), gbc);
		
		gbc.gridx = 1;// numero cellule dans ligne
		gbc.gridy = 1;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(loginJtf, gbc);
		
		// Mot de passe
		gbc.gridx = 0;// numero cellule dans ligne
		gbc.gridy = 2;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(new Label("Mot de passe"), gbc);
		
		gbc.gridx = 1;// numero cellule dans ligne
		gbc.gridy = 2;// numero ligne
		gbc.insets = new Insets(5, 5, 5, 5);
		contentPane.add(mdpJpf, gbc);
	}
	
}// END class AddIdentifiantDialog

package ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import util.AppParams;

public class NoteVersionDialog extends JDialog {

	private final String TITLE = "Notes de versions";
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	private JTextPane textPane;
	
	public NoteVersionDialog(JFrame parent) {
		this.setTitle(TITLE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		initComponents();
		this.getContentPane().add(new JScrollPane(textPane), BorderLayout.CENTER);
		
		this.setVisible(true);
	}

	private void initComponents() {
		textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setBackground(new Color(240, 240, 240));
		textPane.setEditable(false);
		textPane.setSize(WIDTH - 2, HEIGHT - 2);
		
		String content = "<p><strong>Version</strong> : " + AppParams.VERSION;// v1.3
		content += "<br/><strong>Date de mise à jour</strong> : " + AppParams.RELEASE_DATE;// 27/10/2014
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>Laisser le choix, à l'utilisateur, du répertoire d'enregistrer des fichiers</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajout des boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
		content += "<li>Ajouter des info-bulles sur certains boutons</li>";
		content += "<li>Annuler la suppression d'un identifiant</li>";
		content += "<li>Exporter les données aux formats CSV et TXT</li>";
		content += "<li>Ajouter la possibilité de changer le mot de passe utilisateur</li>";
		content += "</ul></p><hr>";// END maj
		
		content += "<p><strong>Version</strong> : 1.2";// v1.2
		content += "<br/><strong>Date de mise à jour</strong> : 26/10/2014";// 26/10/2014
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>Importation d'identifiants à partir de fichiers aux formats CSV et TXT</li>";
		content += "<li>Suppression du champ \"alias\" dans la boîte de dialogue d'ajout d'identifiant (et dans le modèle)</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajout des boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
		content += "<li>Ajouter des info-bulles sur certains boutons</li>";
		content += "<li>Annuler la suppression d'un identifiant</li>";
		content += "<li>Exporter les données aux formats CSV et TXT</li>";
		content += "<li>Laisser le choix, à l'utilisateur, du répertoire d'enregistrer des fichiers</li>";
		content += "<li>Ajouter la possibilité de changer le mot de passe utilisateur</li>";
		content += "</ul></p><hr>";// END maj
		
		content += "<p><strong>Version</strong> : 1.1";// v1.1
		content += "<br/><strong>Date de mise à jour</strong> : 25/10/2014";// 25/10/2014
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>Enregistrement des données après modification d'un identifiant de connexion</li>";
		content += "<li>Exportation des données au format PDF</li>";
		content += "<li>Suppression d'un identifiant de connexion (menu)</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajouter les boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
		content += "<li>Exporter les données aux formats CSV et TXT</li>";
		content += "<li>Ajouter la possibilité de changer le mot de passe utilisateur</li>";
		content += "<li>Importer des identifiants à partir de fichiers aux formats CSV et TXT</li>";
		content += "</ul></p><hr>";// END maj
		
		content += "<p><strong>Version</strong> : 1.0";// v1.0
		content += "<br/><strong>Date de mise à jour</strong> : 24/10/2014";// 24/10/2014
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>Création et vérification du mot de passe applicatif</li>";
		content += "<li>Action d'ajout d'un identifiant (menu)</li>";
		content += "<li>Actions d'ajout et suppression d'un identifiant (boutons)</li>";
		content += "<li>Boîtes de dialogues \"A propos\" et \"Notes de versions\"</li>";
		content += "<li>Sauvegarde (ajout et suppression) des identifiants de connexion aux sites Web</li>";
		content += "<li>La table contenant les identifiants de connexion peut être triée par colonne</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajouter les boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
		content += "<li>Exporter les données aux formats CSV, PDF et TXT</li>";
		content += "<li>Supprimer un identifiant de connexion (menu)</li>";
		content += "<li>Ajouter la possibilité de changer le mot de passe utilisateur</li>";
		content += "<li>Enregistrer les nouvelles valeurs après modification d'un identifiant de connexion</li>";
		content += "</ul></p><hr>";// END maj
		
		textPane.setText(content);
		textPane.setCaretPosition(0);// affiche le début du texte
	}
	
}

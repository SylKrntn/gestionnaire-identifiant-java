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
		
		String content = "<p><strong>Version</strong> : " + AppParams.VERSION;// v1.7
		content += "<br/><strong>Date de mise à jour</strong> : " + AppParams.RELEASE_DATE;// 08/02/2015
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>Les fichiers de configuration et de base de données s'enregistrent dans le répertoire de l'application "
					+ "(et non plus dans le dossier personnel de l'utilisateur) pour les utilisateurs de LINUX, "
					+ "en utilisant le terminal et la commande \"java -jar\"</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajout des boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
		content += "<li>Nettoyer et factoriser le code</li>";
		content += "<li>Améliorer les algorithmes</li>";
		content += "</ul></p><hr>";// END maj
		
		content += "<p><strong>Version</strong> : " + AppParams.VERSION;// v1.6
		content += "<br/><strong>Date de mise à jour</strong> : " + AppParams.RELEASE_DATE;// 02/11/2014
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>Exportation des données au format CSV</li>";
		content += "<li>Exportation des données au format XLS</li>";
		content += "<li>Importation des données au format XLS</li>";
		content += "<li>Importation des données au format PDF</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajout des boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
		content += "<li>Nettoyer et factoriser le code</li>";
		content += "<li>Améliorer les algorithmes</li>";
		content += "</ul></p><hr>";// END maj
		
		content += "<p><strong>Version</strong> : 1.5";// v1.5
		content += "<br/><strong>Date de mise à jour</strong> : 31/10/2014";// 31/10/2014
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>Affichage des identifiants triés par ordre croissant des sites</li>";
		content += "<li>Exportation des données au format TXT</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajout des boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
		content += "<li>Importer les données au format PDF</li>";
		content += "<li>Exporter les données au format CSV</li>";
		content += "</ul></p><hr>";// END maj
		
		content += "<p><strong>Version</strong> : 1.4";// v1.4
		content += "<br/><strong>Date de mise à jour</strong> : 28/10/2014";// 28/10/2014
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>Les données affichées dans la table, après l'importation d'identifiants à partir d'un fichier, reflètent celles présentes en BDD</li>";
		content += "<li>Possibilité d'annuler la dernière suppression d'identifiant</li>";
		content += "<li>Possibilité de changer le mot de passe de l'application</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajout des boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
		content += "<li>Exporter les données aux formats CSV et TXT</li>";
		content += "</ul></p><hr>";// END maj
		
		content += "<p><strong>Version</strong> : 1.3";// v1.3
		content += "<br/><strong>Date de mise à jour</strong> : 27/10/2014";// 27/10/2014
		
		content += "<br /><strong>Nouveautés</strong> : ";
		content += "<ul>";
		content += "<li>L'utilisateur a maintenant le choix du répertoire d'enregistrement des fichiers (PDF)</li>";
		content += "<li>Mise à jour de l'importation des identifiants à partir d'un fichier (CSV et TXT)</li>";
		content += "<li>Ajout d'une info-bulle sur le bouton de suppression d'identifiants</li>";
		content += "</ul>";
		
		content += "<br /><strong>Fonctionnalités à venir</strong> : ";
		content += "<ul>";
		content += "<li>Ajout des boîtes de dialogues \"Manuel utilisateur\" et \"Options\"</li>";
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

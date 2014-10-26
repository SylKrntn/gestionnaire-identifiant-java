package launcher;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import model.MdpApp;
import model.dao.FileDAO;
import model.dao.SQLiteDAO;

import org.apache.commons.codec.digest.DigestUtils;

import ui.Fenetre;
import ui.MdpAppCreationDialog;
import ui.MdpAppRecuperationDialog;
import util.MessageType;
import util.SKConfig;
import util.SKUtils;

public class Launcher {

	public static void main(String[] args) {
		File file = new File("mdpmngr.cfg");// fichier de configuration de l'application
		
		Connection connexion = SQLiteDAO.getInstance().openDB();
		
		if (!file.exists()) {// Si c'est le tout premier lancement de l'application...
			if (connexion != null) {// Si la connexion a réussi...
				String query = "CREATE TABLE IF NOT EXISTS identifiants ("+
						"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
						"site VARCHAR(64) UNIQUE NOT NULL, "+
						"login VARCHAR(64) NOT NULL, "+
						"mdp VARCHAR(64) NOT NULL, "+
						"alias VARCHAR(255) )";
				
				boolean tableCreated = SQLiteDAO.getInstance().createTable(connexion, query);
				
				if (tableCreated) {// Si la table a été créée dans la BDD
					Object[] obj = {0, null};
					do {
						obj = MdpAppCreationDialog.open();
						if (obj[1] != null) {
							SQLiteDAO.getInstance().setKey((String) obj[1]);// affecte le mdp en tant que clé de cryptage
							String sha256 = DigestUtils.sha256Hex(((String) obj[1]).getBytes());// chiffre le mot de passe
							FileDAO.getInstance().saveAppPassword(new MdpApp(sha256), file);// enregistre le mot de passe applicatif (crypté)
							new Fenetre();
						}
					} while ((int) obj[0] == MdpAppCreationDialog.OK_BTN && obj[1] == null);
				}
				else {// sinon, la table n'a pas été créée dans la BDD...
					SKUtils.setConsoleMessage("Erreur lors de la création de la table dans la BDD.", Launcher.class, MessageType.ERROR, 53, SKConfig.DEBUG_MODE);
					System.exit(0);
				}
			}
			else {// sinon, la connexion à la BDD a échoué
				SKUtils.setConsoleMessage("Echec de la connexion à la BDD.", Launcher.class, MessageType.ERROR, 58, SKConfig.DEBUG_MODE);
				System.exit(0);
			}
		}
		else {// Sinon l'application a déjà été lancé et on récupère les infos
			if (connexion != null) {
				// récupère le mot de passe applicatif
				MdpApp mdpApp = FileDAO.getInstance().getAppPassword(file);
				String mdp = mdpApp.getMdpSha256();
				
				String consoleMsg = "*** MdpApp ***\n";
				consoleMsg += mdp + "\n";
				SKUtils.setConsoleMessage(consoleMsg, Launcher.class, MessageType.INFORMATION, 70, SKConfig.DEBUG_MODE);
				Object[] obj = {0, null};
				int mdpErrorNb = 0;
				String sha256 = "";// mot de passe saisi par l'utilisteur, qui sera crypté
				boolean arePasswordsNotEqual = true;
				
				do {
					// Ouvre la boîte de dialogue qui retourne l'object contenant le bouton cliqué et le mot de passe saisi par l'utilisateur
					obj = MdpAppRecuperationDialog.open();
					if (obj[1] != null) {
						sha256 = DigestUtils.sha256Hex(((String) obj[1]).getBytes());
						SKUtils.setConsoleMessage("mdp applicatif (crypté) : " + mdp, Launcher.class, MessageType.INFORMATION, 81, SKConfig.DEBUG_MODE);
						SKUtils.setConsoleMessage("mdp utilisateur crypté : " + sha256, Launcher.class, MessageType.INFORMATION, 82, SKConfig.DEBUG_MODE);
						
						// Si le mot de passe applicatif est bon
						if (sha256.equals(mdpApp.getMdpSha256())) {// si le mdp crypté est le même que celui enregistré dans le fichier de conf...
							arePasswordsNotEqual = false;// les mots de passe sont identiques
							SQLiteDAO.getInstance().setKey((String) obj[1]);// affecte ce mot de passe (non-crypté) à la clef qui servira à crypter et décrypter les mdp.
							new Fenetre();
						}
						// Sinon, le mot de passe n'est pas bon
						else {
							mdpErrorNb++;// incrémente le compteur d'erreur de mot de passe applicatif
							SKUtils.setConsoleMessage("ERREUR : Le mot de passe saisi, une fois crypté, n'est pas le même que celui du fichier de conf.", Launcher.class, MessageType.ERROR, 93, SKConfig.DEBUG_MODE);
							SKUtils.showUserMessage("Le mot de passe saisi (après chiffrement) est différent du mot de passe applicatif.", JOptionPane.WARNING_MESSAGE);
							
							if (mdpErrorNb == 3) {// Si l'utilisateur a mal saisi 3 fois de suite le mot de passe applicatif...
								SKUtils.showUserMessage("3 mots de passe successifs invalides. L'application va être arrêtée.", JOptionPane.ERROR_MESSAGE);
								SKUtils.setConsoleMessage("ERREUR : 3 mots de passe successifs invalides. Application arrêtée.", Launcher.class, MessageType.ERROR, 98, SKConfig.DEBUG_MODE);
								try {
									connexion.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
								System.exit(0);
							}
							
//							mdpJpf.setText("");// remet le champ du mot de passe vide
						}
						
					}
				} while ((int) obj[0] == MdpAppRecuperationDialog.OK_BTN && arePasswordsNotEqual);
			}
			// si la connexion à la BDD a échoué...
			else {
				SKUtils.showUserMessage("Echec de la connexion à la BDD SQLite.", JOptionPane.ERROR_MESSAGE);
				SKUtils.setConsoleMessage("ERREUR : Echec de la connexion à la BDD SQLite.", Launcher.class, MessageType.ERROR, 116, SKConfig.DEBUG_MODE);
				System.exit(0);
			}
		}// END else (fichier existe)
	}// END main()
}// END class Launcher

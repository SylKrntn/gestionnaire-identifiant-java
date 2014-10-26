package util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import util.MessageType;

public class SKUtils {

	/**
	 * Convertit un tableau de caractères en une chaîne de caractères
	 * @param tab {char[]} : tableau de caractères à convertir
	 * @return une chaîne de caractère. Renvoie NULL sinon.
	 */
	public static String charTabToString(char[] tab) {
		String mdpStr = "";
		
		if (tab.length > 0) {
			for (int i = 0; i < tab.length; i++) {
				mdpStr += tab[i];
			}
			
			if (mdpStr.trim().length() > 0) { return mdpStr; }
		}
		
		return null;
	}
	
	/**
	 * Affiche les messages dans la console, dans le format "message. [classe - ligne 0]"
	 * @param message
	 * @param classe
	 * @param messageType
	 * @param numeroLigne
	 * @param mode
	 */
	public static void setConsoleMessage(String message, Class<?> classe, MessageType messageType, int numeroLigne, boolean mode) {
		if (mode) {
			switch (messageType) {
				case INFORMATION:
					System.out.println(message + " [" + classe + " - ligne " + numeroLigne + "]");
					break;
				case ERROR:
					System.err.println(message + " [" + classe + " - ligne " + numeroLigne + "]");
					break;
				default:
					System.out.println("Le type de message est inconnu. [" + classe + " - ligne " + numeroLigne + "]");
					break;
			}
		}
//		else {
//			System.out.println("SKConfig.DEBUG_MODE = false");
//		}
	}// END setConsoleMessage(String message, Class<?> classe, MessageType messageType, int numeroLigne, boolean mode)
	
	/**
	 * @deprecated
	 * @param message
	 */
	public static void showInformationMessage(String message) {
		JDialog.setDefaultLookAndFeelDecorated(true);
		JOptionPane.showMessageDialog(null, message, "Enregistrement des données", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * 
	 * @param message
	 * @param errorMessage
	 */
	public static void showUserMessage(String message, int errorMessage) {
		JDialog.setDefaultLookAndFeelDecorated(false);
		
		switch(errorMessage) {
			case JOptionPane.ERROR_MESSAGE:
				JOptionPane.showMessageDialog(null, message, "ERREUR", errorMessage);
				break;
			case JOptionPane.INFORMATION_MESSAGE:
				JOptionPane.showMessageDialog(null, message, "INFOS", errorMessage);
				break;
			case JOptionPane.QUESTION_MESSAGE:
				JOptionPane.showMessageDialog(null, message, "QUESTION", errorMessage);
				break;
			case JOptionPane.WARNING_MESSAGE:
				JOptionPane.showMessageDialog(null, message, "ATTENTION", errorMessage);
				break;
		}
	}// END showUserMessage(String message, int errorMessage)

	
	/**
	 * Crypte le mot de passe de connexion au site web
	 * @param mdp : le mot de passe de connexion au site web à crypter
	 * @param key : la clef (symétrique) servant à crypter le mot de passe de connexion au site web
	 * @return le mot de passe de connexion crypté sous forme d'un tableau d'octets
	 */
	public static byte[] encrypt(String mdp, String key) {
		 Key secretKey = new SecretKeySpec(key.getBytes(), "Blowfish");
		 
		try {
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return cipher.doFinal(mdp.getBytes());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Décrypte le mot de passe de connexion au site web
	 * @param mdp : le mot de passe de connexion au site web à décrypter
	 * @param key : la clef (symétrique) servant à décrypter le mot de passe de connexion au site web
	 * @return le mot de passe de connexion décrypté sous forme d'une chaîne de caractères
	 */
	public static String decrypt(byte[] mdp, String key) {
		Key secretKey = new SecretKeySpec(key.getBytes(), "Blowfish");
		try {
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(mdp));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			System.out.println("Erreur : La clef de décryptage n'est pas la même que celle qui a servi au cryptage.");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Teste la validité des paramètres
	 * @param param {String} : le paramètre à tester
	 * @return TRUE si le paramètre est valide, FALSE sinon.
	 */
	public static boolean isValid(String param) {
		return param != null && !param.equals("") && param.trim().length() > 0;
	}
}

package model.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.MdpApp;
import util.MessageType;
import util.SKConfig;
import util.SKUtils;

public class FileDAO {

	private FileDAO() { }
	
	private static class FileDAOSingletonHolder {
		private static final FileDAO INSTANCE = new FileDAO();
	}
	
	public static FileDAO getInstance() {
		return FileDAOSingletonHolder.INSTANCE;
	}
	
	/**
	 * Enregistre le mot de passe applicatif dans un fichier
	 * @param mdpApp {MdpApp} : le mot de passe applicatif à enregistrer
	 * @param file {File} : le fichier dans lequel enregistrer ce mot de passe
	 */
	public void saveAppPassword(MdpApp mdpApp, File file) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(
					new BufferedOutputStream(
						new FileOutputStream(file)));
			
			oos.writeObject(mdpApp);
			oos.flush();
			oos.close();
			SKUtils.setConsoleMessage("Succès de l'enregistrement du mot de passe applicatif", FileDAO.class, MessageType.INFORMATION, 45, SKConfig.DEBUG_MODE);
		} catch (IOException e) {
			SKUtils.setConsoleMessage("Echec de l'enregistrement du mot de passe applicatif", FileDAO.class, MessageType.ERROR, 47, SKConfig.DEBUG_MODE);
			
			try { oos.close(); }
			catch (IOException e1) { e1.printStackTrace(); }
			e.printStackTrace();
		}
	}
	
	/**
	 * Méthode qui récupère le mot de passe applicatif
	 * @param file {File} : fichier dans lequel récupérer le mot de passe
	 * @return l'objet contenant le mot de passe. NULL sinon.
	 */
	public MdpApp getAppPassword(File file) {
		ObjectInputStream ois = null;
		MdpApp mdpApp = null;
		
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			mdpApp = (MdpApp) ois.readObject();
			SKUtils.setConsoleMessage("*** FileDAO ***\n mdp crypté : " + mdpApp.getMdpSha256(), FileDAO.class, MessageType.INFORMATION, 67, SKConfig.DEBUG_MODE);
			SKUtils.setConsoleMessage("Succès de la récupération du mot de passe applicatif.", FileDAO.class, MessageType.INFORMATION, 68, SKConfig.DEBUG_MODE);
		} catch (FileNotFoundException e) {
			SKUtils.setConsoleMessage("Erreur : le chemin d'accès au fichier est incorrect.", FileDAO.class, MessageType.ERROR, 70, SKConfig.DEBUG_MODE);
			e.printStackTrace();
		} catch (IOException e) {
			SKUtils.setConsoleMessage("Erreur : une exception est survenue lors de la tentative d'accès au fichier.", FileDAO.class, MessageType.ERROR, 73, SKConfig.DEBUG_MODE);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			SKUtils.setConsoleMessage("Erreur : le type d'objet retourné n'est pas celui attendu.", FileDAO.class, MessageType.ERROR, 76, SKConfig.DEBUG_MODE);
			e.printStackTrace();
		} 
		finally {
			try {
				ois.close();
			} catch (IOException e) {
				SKUtils.setConsoleMessage("Erreur : une exception est survenue lors de la tentative de fermeture du flux de lecture.", FileDAO.class, MessageType.ERROR, 83, SKConfig.DEBUG_MODE);
				e.printStackTrace();
			}
		}
		return mdpApp;
	}
}

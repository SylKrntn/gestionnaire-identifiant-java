package model.dao;


import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import model.Identifiant;
import model.Observable;
import model.Observer;
import util.MessageType;
import util.AppParams;
import util.AppUtils;


public class SQLiteDAO implements Observable {
	private boolean dataSaved = false;
	private boolean dataDeleted = false;
	private ArrayList<Observer> observateurs = new ArrayList<Observer>();
	private Identifiant identifiant;
	private String clef = null;
//	private final Key SECRET_KEY = new SecretKeySpec(clef.getBytes(), "Blowfish");
	private Statement stmt;
	
	/**
	 * Constructeur privé
	 */
	private SQLiteDAO() {}
	
	private static class SQLiteDAOSingletonHolder {
		private static final SQLiteDAO INSTANCE = new SQLiteDAO();
	}
	
	public static SQLiteDAO getInstance() {
		return SQLiteDAOSingletonHolder.INSTANCE;
	}
	
	public boolean isDataSaved() {
		return dataSaved;
	}
	
	public boolean isDataDeleted() {
		return dataDeleted;
	}
	
	public void setKey(String mdp) {
		if (!mdp.isEmpty()) {
			this.clef = mdp;// affecte le mdp comme clef
		}
	}
	
	public Connection openDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			return DriverManager.getConnection("jdbc:sqlite:mdpmngr.db");
		} catch (ClassNotFoundException e) {
			AppUtils.setConsoleMessage("Erreur : Driver JDBC manquant.", SQLiteDAO.class, MessageType.ERROR, 71, AppParams.DEBUG_MODE);
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			AppUtils.setConsoleMessage("Impossible de se connecter à la BDD.", SQLiteDAO.class, MessageType.ERROR, 75, AppParams.DEBUG_MODE);
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean createTable(Connection connexion, String query) {
		try {
			stmt = connexion.createStatement();
			stmt.executeUpdate(query);
			
			stmt.close();
			connexion.close();
			
			return true;
		} catch (SQLException e) {
			AppUtils.setConsoleMessage("Echec de la requête de création de table.", SQLiteDAO.class, MessageType.ERROR, 91, AppParams.DEBUG_MODE);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param site
	 * @param login
	 * @param mdp
	 */
	public void save(String site, String login, String mdp) {
		dataSaved = false;
		identifiant = new Identifiant();
		
		if (site.isEmpty()) {
			site = "NA";
		}
		if (login.isEmpty()) {
			login = "NA";
		}
		if (mdp.isEmpty() || mdp == null) {
			mdp = "NA";
		}
		
		byte[] loginEncrypted = this.encrypt(login, clef);
		byte[] mdpEncrypted = this.encrypt(mdp, clef);
		
		AppUtils.setConsoleMessage("mdp: " + mdp + " ; login: " + login, SQLiteDAO.class, MessageType.INFORMATION, 120, AppParams.DEBUG_MODE);
		AppUtils.setConsoleMessage("mdp crypté: " + mdpEncrypted + " ; login crypté: " + loginEncrypted, SQLiteDAO.class, MessageType.INFORMATION, 121, AppParams.DEBUG_MODE);
		
		Connection connexion = null;
		PreparedStatement prepstmt = null;
		
		try {
			connexion = this.openDB();
			connexion.setAutoCommit(false);
			
			AppUtils.setConsoleMessage("Succès de la connexion à la BDD SQLite.", SQLiteDAO.class, MessageType.INFORMATION, 130, AppParams.DEBUG_MODE);
			
			prepstmt = connexion.prepareStatement("INSERT INTO identifiants (site, login, mdp) VALUES (?, ?, ?);");
			prepstmt.setString(1, site);
//			prepstmt.setString(2, login);
//			prepstmt.setString(3, mdp);
			prepstmt.setBytes(2, loginEncrypted);
			prepstmt.setBytes(3, mdpEncrypted);
			prepstmt.executeUpdate();
			
			AppUtils.setConsoleMessage("Succès de l'enregistrement des données.", SQLiteDAO.class, MessageType.INFORMATION, 144, AppParams.DEBUG_MODE);
			
//			SKUtils.showUserMessage("Succès de l'enregistrement des données.", JOptionPane.INFORMATION_MESSAGE);
			identifiant.setSite(site);
			identifiant.setLogin(login);
			identifiant.setMdp(mdp);
			AppUtils.setConsoleMessage(identifiant.toString(), SQLiteDAO.class, MessageType.INFORMATION, 150, AppParams.DEBUG_MODE);
//			this.notifyObservers();
			this.notifyObservers(identifiant);
			dataSaved = true;
			
//			prepstmt.close();
//			connexion.commit();
//			connexion.close();
		} catch (SQLException e) {
			System.out.println("e.getClass() : " + e.getClass());
			System.out.println("e.getClass().getName() : " + e.getClass().getName());
			System.out.println("e.getErrorCode() : " + e.getErrorCode());
			System.out.println("e.getSQLState() : " + e.getSQLState());
			System.out.println("e.getMessage() : " + e.getMessage());
			AppUtils.setConsoleMessage(e.getClass().getName() + ": " + e.getMessage(), SQLiteDAO.class, MessageType.ERROR, 138, AppParams.DEBUG_MODE);
//			System.exit(0);
		}
		finally {
			try {
				prepstmt.close();
				connexion.commit();
				connexion.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}// END save()
	
	/**
	 * 
	 */
	public ArrayList<Identifiant> fetchAll() {
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<Identifiant> logins = new ArrayList<Identifiant>();
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:mdpmngr.db");
	      c.setAutoCommit(false);
	      AppUtils.setConsoleMessage("Succès de la connexion à la BDD SQLite.", SQLiteDAO.class, MessageType.INFORMATION, 186, AppParams.DEBUG_MODE);
	      
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery("SELECT * FROM identifiants ORDER BY site");
	      while ( rs.next() ) {
	         int id = rs.getInt("id");
	         String site = rs.getString("site");
//	         String login = rs.getString("login");
//	         String mdp = rs.getString("mdp");
	         byte[] loginEncrypted = rs.getBytes("login");
	         String loginDecrypted = this.decrypt(loginEncrypted, this.clef);
	         byte[] mdpEncrypted = rs.getBytes("mdp");
	         String mdpDecrypted = this.decrypt(mdpEncrypted, this.clef);
	         
	         String consoleMsg = "ID = " + id + "\n";
	         consoleMsg += "SITE = " + site + "\n";
	         consoleMsg += "LOGINENCCRYPTED = " + loginEncrypted + "\n";
	         consoleMsg += "LOGINDECCRYPTED = " + loginDecrypted + "\n";
	         consoleMsg += "MDPENCRYPTED = " + mdpEncrypted + "\n";
	         consoleMsg += "MDPDECRYPTED = " + mdpDecrypted + "\n";
	         AppUtils.setConsoleMessage(consoleMsg, SQLiteDAO.class, MessageType.INFORMATION, 206, AppParams.DEBUG_MODE);
	         
//	         logins.add(new Identifiant(site, login, mdp));
	         logins.add(new Identifiant(site, loginDecrypted, mdpDecrypted));
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	      
	      AppUtils.setConsoleMessage("Succès de la lecture des données.", SQLiteDAO.class, MessageType.INFORMATION, 215, AppParams.DEBUG_MODE);
	      
	    } catch ( Exception e ) {
	    	AppUtils.setConsoleMessage(e.getClass().getName() + ": " + e.getMessage(), SQLiteDAO.class, MessageType.ERROR, 218, AppParams.DEBUG_MODE);
	    	System.out.println("Raisons possibles de l'erreur :");
	    	System.out.println("- La connexion à la BDD a échoué,");
	    	System.out.println("- La requête de récupération des données a échoué,");
	    	System.out.println("- La clef de décryptage n'est pas celle qui a servi au cryptage.");
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() + "[SqliteDAO.java - fetchAll() - ligne 126]");
	    	System.exit(0);
	    }
	    finally {
	    	try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		return logins;
	}// END fetchAll()
	
	/**
	 * Récupère les identifiants de connexion du site spécifié
	 * @param siteName {String} : le nom du site pour lequel on veut récupérer les identifiants
	 * @return les identifiants du site en cas de succès, NULL sinon
	 * @deprecated
	 */
	public Identifiant fetch(String siteName) {
		Connection c = null;
	    Statement stmt = null;
	    Identifiant identity = null;
	    
	    try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:mdpmngr.db");
	    	c.setAutoCommit(false);
	    	AppUtils.setConsoleMessage("Succès de la connexion à la BDD SQLite.", SQLiteDAO.class, MessageType.INFORMATION, 244, AppParams.DEBUG_MODE);

	    	stmt = c.createStatement();
	    	ResultSet rs = stmt.executeQuery("SELECT site, login, mdp FROM identifiants WHERE site = '"+siteName+"';");
	    	
	    	while ( rs.next() ) {
	    		String site = rs.getString("site");
	    		String login = rs.getString("login");
	    		byte[] mdp = rs.getBytes("mdp");
	    		String mdpDecrypted = this.decrypt(mdp, clef);
	    		identity = new Identifiant(site, login, mdpDecrypted);
	    		
		        String consoleMsg = "SITE = " + site + "\n";
		        consoleMsg += "LOGIN = " + login + "\n";
		        consoleMsg += "MDPENCRYPTED = " + mdp + "\n";
		        consoleMsg += "MDPDECRYPTED = " + mdpDecrypted + "\n";
	    		AppUtils.setConsoleMessage(consoleMsg, SQLiteDAO.class, MessageType.INFORMATION, 260, AppParams.DEBUG_MODE);
	    	}
	    	
	    	rs.close();
	    	stmt.close();
	    	c.close();
	    	
//	    	SKUtils.setConsoleMessage("Succès de la lecture des données.", SqliteDAO.class, MessageType.INFORMATION, 230, SKConfig.DEBUG_MODE);
//	    	SKUtils.setConsoleMessage(identity.toString(), SqliteDAO.class, MessageType.INFORMATION, 231, SKConfig.DEBUG_MODE);
	    	
	    	if (identity == null) {
	    		AppUtils.setConsoleMessage("Aucun site de ce nom trouvé dans la base de données.", SQLiteDAO.class, MessageType.WARNING, 271, AppParams.DEBUG_MODE);
	    	}
	    } catch ( Exception e ) {
	    	AppUtils.setConsoleMessage(e.getClass().getName() + ": " + e.getMessage(), SQLiteDAO.class, MessageType.ERROR, 274, AppParams.DEBUG_MODE);
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() + "[SqliteDAO.java - fetch() - ligne 136]");
	    	System.exit(0);
	    }
	    finally {
	    	try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	    return identity;
	}// END fetch()
	
	/**
	 * 
	 * @param siteName
	 */
	public void delete(String siteName) {
		AppUtils.setConsoleMessage("Nom du site : " + siteName, SQLiteDAO.class, MessageType.INFORMATION, 294, AppParams.DEBUG_MODE);
		dataDeleted = false;
		Connection c = null;
	    PreparedStatement stmt = null;
	    
	    try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:mdpmngr.db");
	    	c.setAutoCommit(false);
	    	
	    	AppUtils.setConsoleMessage("Succès de la connexion à la BDD SQLite.", SQLiteDAO.class, MessageType.INFORMATION, 304, AppParams.DEBUG_MODE);
	    	
	    	if (siteName != null || !siteName.equals("")) {// si l'utilisateur a renseigné un site web, on vérifie que ce site existe dans la BDD
		    	stmt = c.prepareStatement("DELETE FROM identifiants WHERE site = ?;");
			    stmt.setString(1, siteName);
			    
			    if (stmt.executeUpdate() > 0) {// si la suppression a réussi...
			    	dataDeleted = true;
//			    	SKUtils.showUserMessage("Succès de la suppression de la donnée dans la base de données.", JOptionPane.INFORMATION_MESSAGE);
			    	AppUtils.setConsoleMessage("Succès de la suppression des données.", SQLiteDAO.class, MessageType.INFORMATION, 313, AppParams.DEBUG_MODE);
			    }
			    else {// si la suppression a échoué...
//			    	SKUtils.showUserMessage("Ce site web n'existe pas dans la BDD.", JOptionPane.WARNING_MESSAGE);
			    	AppUtils.setConsoleMessage("Ce site web n'existe pas dans la BDD.", SQLiteDAO.class, MessageType.ERROR, 317, AppParams.DEBUG_MODE);
			    }
	    	}
	    	else {// si l'utilisateur n'a rien renseigné, on affiche un message d'erreur
//	    		SKUtils.showUserMessage("Veuillez saisir un site web.", JOptionPane.WARNING_MESSAGE);
	    		AppUtils.setConsoleMessage("Aucun site web n'a été saisi.", SQLiteDAO.class, MessageType.ERROR, 322, AppParams.DEBUG_MODE);
	    	}
	    	
	    	stmt.close();
	    	c.commit();
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	AppUtils.showUserMessage(e.getClass().getName() + ": " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
	    	AppUtils.setConsoleMessage(e.getClass().getName() + ": " + e.getMessage(), SQLiteDAO.class, MessageType.ERROR, 331, AppParams.DEBUG_MODE);
//	    	System.exit(0);
	    	try {
				stmt.close();
		    	c.commit();
		    	c.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    }
	}// END delete()
	
	/**
	 * Met à jour les identifiants d'un site web 
	 * @param login : le login
	 * @param mdpB : le mot de passe
	 * @param site : le site pour lequel on veut mettre à jour les identifiants de connexion
	 * @deprecated
	 */
	public void update(String login, char[] mdpB, String site) {
		String query = null;
		
		Connection c = null;
		PreparedStatement stmt = null;
		String mdp = null;
		byte[] mdpEncrypted = null;
		int queryNb = 0;
		
		if (mdpB != null) {
			mdp = AppUtils.charTabToString(mdpB);
			mdpEncrypted = this.encrypt(mdp, clef);
		}
		
		String consoleMsg = "*** UPDATE du site web : " + site + " ***\n";
		consoleMsg += "login : " + login + "\n";
		consoleMsg += "mdp : " + mdp + "\n";
		AppUtils.setConsoleMessage(consoleMsg, SQLiteDAO.class, MessageType.INFORMATION, 366, AppParams.DEBUG_MODE);
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:mdpmngr.db");
			c.setAutoCommit(false);
			
			AppUtils.setConsoleMessage("Succès de la connexion à la BDD SQLite.", SQLiteDAO.class, MessageType.INFORMATION, 373, AppParams.DEBUG_MODE);
			
			// Si login et mdp sont invalides...
			if ((login.isEmpty() || login.equals("") || login.matches(" +") || login == null || login.equals("NA")) &&
					(mdp.isEmpty() || mdp.equals("") || mdp.matches(" +") || mdp == null || mdp.equals("NA"))) {
				// TODO: afficher une fenêtre informant à l'utilisateur qu'au moins un des 2 champs doit être rempli
			}
			else if (login.isEmpty() || login.equals("") || login.matches(" +") || login == null  || login.equals("NA")) {
				query = "UPDATE identifiants SET mdp = ? WHERE site = ?;";// si login vide, enregistre mot de passe
				queryNb = 1;
			}
			else if (mdp.isEmpty() || mdp.equals("") || mdp.matches(" +") || mdp == null  || mdp.equals("NA")) {
				query = "UPDATE identifiants SET login = ? WHERE site = ?;";// si mdp vide, enregistre login
				queryNb = 2;
			}
			else {
				query = "UPDATE identifiants SET login = ?, mdp = ? WHERE site = ?;";// sinon, enregistre les 2 champs
				queryNb = 3;
			}
			
			stmt = c.prepareStatement(query);
			
			switch (queryNb) {
				case 1:
					stmt.setBytes(1, mdpEncrypted);
					stmt.setString(2, site);
					break;
				case 2:
					stmt.setString(1, login);
					stmt.setString(2, site);
					break;
				case 3:
					stmt.setString(1, login);
					stmt.setBytes(2, mdpEncrypted);
					stmt.setString(3, site);
					break;
				default:
					System.out.println("Impossible d'efectuer la mise à jour.");
			}
			
//	    	stmt.executeUpdate();
//	    	stmt.close();
//	    	c.commit();
//	    	c.close();
	    	
	    	AppUtils.setConsoleMessage("Succès de la mise à jour de la donnée dans la BDD SQLite.", SQLiteDAO.class, MessageType.INFORMATION, 418, AppParams.DEBUG_MODE);
			
		} catch (Exception e) {
			AppUtils.setConsoleMessage(e.getClass().getName() + ": " + e.getMessage(), SQLiteDAO.class, MessageType.INFORMATION, 421, AppParams.DEBUG_MODE);
			System.err.println( e.getClass().getName() + ": " + e.getMessage() + " [SqliteDAO.java - update() - ligne 361]");
		}
		finally {
			try {
				stmt.executeUpdate();
				stmt.close();
		    	c.commit();
		    	c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}// END update(String login, byte[] mdp, String site)
	
	
	
	/* ///////////////////// */
	/* // PRIVATE METHODS // */
	/* ///////////////////// */
	
	/**
	 * Crypte le mot de passe de connexion au site web
	 * @param mdp : le mot de passe de connexion au site web à crypter
	 * @param key : la clef (symétrique) servant à crypter le mot de passe de connexion au site web
	 * @return le mot de passe de connexion crypté sous forme d'un tableau d'octets
	 */
	private byte[] encrypt(String mdp, String key) {
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
	private String decrypt(byte[] mdp, String key) {
		Key secretKey = new SecretKeySpec(key.getBytes(), "Blowfish");
		try {
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(mdp));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			AppUtils.setConsoleMessage("Erreur : La clef de décryptage n'est pas la même que celle qui a servi au cryptage.", SQLiteDAO.class, MessageType.ERROR, 478, AppParams.DEBUG_MODE);
			e.printStackTrace();
			return null;
		}
	}

	public void addObserver(Observer observer) {
		this.observateurs.add(observer);
	}

	public void removeObserver(Observer observer) {
		this.observateurs.remove(observer);
	}

	public void notifyObservers() {
		for (Observer obs: observateurs) {
			obs.update((Observable) this, identifiant);
		}
	}

	public void notifyObservers(Object obj) {
		for (Observer obs: observateurs) {
			obs.update((Observable) this, (Identifiant) obj);
		}
	}

	public void encryptAndSave(String valueToEncrypt, int columnIndex, String siteName) {
		dataSaved = false;
		Connection connexion = null;
		PreparedStatement prepStmt = null;
		String query = null;
		byte[] valueEncrypted = this.encrypt(valueToEncrypt, clef);
		
		switch (columnIndex) {
			case 1:
				query = "UPDATE identifiants SET login = ? WHERE site = ?";
				break;
			case 2:
				query = "UPDATE identifiants SET mdp = ? WHERE site = ?";
				break;
			default:
				System.out.println("Le numéro de colonne dépasse le nombre de colonnes dans la table.");
				break;
		}
		
		try {
			connexion = this.openDB();
			connexion.setAutoCommit(false);
			
			prepStmt = connexion.prepareStatement(query);
			prepStmt.setBytes(1, valueEncrypted);
			prepStmt.setString(2, siteName);
			
			prepStmt.executeUpdate();
			
			prepStmt.close();
			connexion.commit();
			connexion.close();
			
			dataSaved = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveLoginsList(ArrayList<Identifiant> identifiants) {
		dataSaved = false;
		String query = "INSERT INTO identifiants (site, login, mdp) VALUES (?, ?, ?)";
		Connection connexion = null;
		PreparedStatement prepStmt = null;
		
		final int TOTAL_NB_QUERIES = identifiants.size();
		int successedQueries = 0;
		
		try {
			connexion = this.openDB();
			connexion.setAutoCommit(false);
			
			for (int i=0; i<identifiants.size(); i++) {
				byte[] loginEncrypted = this.encrypt(identifiants.get(i).getLogin(), this.clef);
				byte[] mdpEncrypted = this.encrypt(identifiants.get(i).getMdp(), this.clef);
				prepStmt = connexion.prepareStatement(query);
				prepStmt.setString(1, identifiants.get(i).getSite());
				prepStmt.setBytes(2, loginEncrypted);
				prepStmt.setBytes(3, mdpEncrypted);
				prepStmt.executeUpdate();
				
//				if (isDataSaved()) {
					successedQueries++;
					connexion.commit();
//				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (prepStmt != null) prepStmt.close();
				if (connexion != null) connexion.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (successedQueries == TOTAL_NB_QUERIES) {
			dataSaved = true;
		}
		else {
			dataSaved = false;
		}		
	}

	public void update(Identifiant identifiant) {
		Connection c = null;
		PreparedStatement prepStmt = null;
		final String QUERY = "UPDATE identifiants SET login = ?, mdp = ? WHERE site = ?";
		byte[] loginEncrypted = encrypt(identifiant.getLogin(), this.clef);
		byte[] mdpEncrypted = encrypt(identifiant.getMdp(), this.clef);
		
		try {
			c = this.openDB();
			c.setAutoCommit(false);
			prepStmt = c.prepareStatement(QUERY);
			
			prepStmt.setBytes(1, loginEncrypted);
			prepStmt.setBytes(2, mdpEncrypted);
			prepStmt.setString(3, identifiant.getSite());
			
			prepStmt.executeUpdate();
			c.commit();
			AppUtils.setConsoleMessage("Succès de la mise à jour de l'identifiant.", SQLiteDAO.class, MessageType.INFORMATION, 618, AppParams.DEBUG_MODE);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			 try {
				 if (prepStmt != null) { prepStmt.close(); }
				 if (c != null) { c.close(); }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
}// END class SQLiteDAO

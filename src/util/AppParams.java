package util;

public class AppParams {
	// Informations sur l'application
	public static final String APP_NAME = "MdP Manager";
	public static final String VERSION = "1.7";
	public static final String AUTHOR = "Sylvain KEROUANTON";
	public static final String LANGUAGE = "Java";
	public static final String DESCRIPTION = "Logiciel permettant de gérer ses identifiants de connexion aux sites Web";
	public static final String RELEASE_DATE = "08/02/2015";
	public static final boolean DEBUG_MODE = false;
	// Informations sur le système
	public static final String USER_DIR = System.getProperty("user.dir");// répertoire de l'application
	public static final String USER_HOME = System.getProperty("user.home");// répertoire personnel de l'utilisateur de l'ordinateur
	public static final String OS_NAME = System.getProperty("os.name");// nom du système d'exploitation
	public static final String OS_VERSION = System.getProperty("os.version");// version du système d'exploitation
}

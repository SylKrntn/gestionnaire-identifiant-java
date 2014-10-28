package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;


























import javax.swing.filechooser.FileNameExtensionFilter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import util.MessageType;
import util.AppParams;
import util.AppUtils;
import model.Identifiant;
import model.IdentifiantTableModel;
import model.dao.SQLiteDAO;

public class Fenetre extends JFrame {
	private final String TITLE = "MdP Manager";// titre de la fenêtre
	private final int WIDTH = 640;// largeur de la fenêtre
	private final int HEIGHT = 480;// hauteur de la fenêtre
	private JPanel btnPane;// panel contenant les boutons d'ajout et de suppression d'identifiant
	private JTable tableau;// tableau dans lequel seront affichés les identifiants
	private IdentifiantTableModel identifiantTM;// modèle du tableau
	private JMenuBar barreMenu;// barre des menus
	private JMenu fichier;// menu fichier
	private JMenu edition;// menu edition
	private JMenu aide;// menu d'aide
	
	public Fenetre() {
		this.setTitle(TITLE);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initComponents();
		this.setJMenuBar(barreMenu);
		this.getContentPane().add(new JScrollPane(tableau), BorderLayout.CENTER);
		this.getContentPane().add(btnPane, BorderLayout.SOUTH);
		
		this.setVisible(true);
		AppUtils.setConsoleMessage("Fenêtre principale ouverte.", Fenetre.class, MessageType.INFORMATION, 36, AppParams.DEBUG_MODE);
	}

	/**
	 * Initialise les composants de la fenêtre (barre de menu et les différents panels)
	 */
	private void initComponents() {
		initBarreMenu();
		initContentPane();
		initBtnPane();
	}

	/**
	 * Initialise la barre des menus
	 */
	private void initBarreMenu() {
		barreMenu = new JMenuBar();
		
		initFileMenu();
		initEditionMenu();
		initHelpMenu();
		
		barreMenu.add(fichier);
		barreMenu.add(edition);
		barreMenu.add(aide);
	}

	/**
	 * Initialise le menu "Fichier"
	 */
	private void initFileMenu() {
		fichier = new JMenu("Fichier"); 
		fichier.setMnemonic(KeyEvent.VK_F);
		
		// QUITTER
		JMenuItem quit = new JMenuItem("Quitter");
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quit.addActionListener(new QuitAction());
		
		// IMPORTER
		JMenu importFrom = new JMenu("Importer à partir du format...");
		JMenuItem importFromCSV = new JMenuItem("CSV");
		JMenuItem importFromPDF = new JMenuItem("PDF [nom implémenté]");
		JMenuItem importFromTXT = new JMenuItem("TXT");
		
		importFromCSV.addActionListener(new ImportFromCSVOrTXTAction());
//		importFromPDF.addActionListener(new ImportFromPDFAction());
		importFromTXT.addActionListener(new ImportFromCSVOrTXTAction());
		
		importFrom.add(importFromCSV);
		importFrom.add(importFromPDF);
		importFrom.add(importFromTXT);
		
		// EXPORTER
		JMenu exportAs = new JMenu("Exporter au format...");
		JMenuItem exportAsCSV = new JMenuItem("CSV [non implémenté]");
		JMenuItem exportAsPDF = new JMenuItem("PDF");
		JMenuItem exportAsTXT = new JMenuItem("TXT [non implémenté]");
		
//		exportAsCSV.addActionListener(new ExportAsCSVAction());
		exportAsPDF.addActionListener(new ExportAsPDFAction());
//		exportAsTXT.addActionListener(new ExportAsTXTAction());
		
		exportAs.add(exportAsCSV);
		exportAs.add(exportAsPDF);
		exportAs.add(exportAsTXT);
		
		// AJOUT DES SOUS-MENUS AU MENU
		fichier.add(importFrom);
		fichier.add(exportAs);
		fichier.add(new JSeparator());
		fichier.add(quit);
	}
	
	/**
	 * Initialise le menu "Edition"
	 */
	private void initEditionMenu() {
		edition = new JMenu("Edition");
		edition.setMnemonic(KeyEvent.VK_E);
		
		JMenuItem addIdentifiant = new JMenuItem("Ajouter un identifiant");
		addIdentifiant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		addIdentifiant.addActionListener(new AddIdentifiantAction());
		
		JMenuItem delIdentifiant = new JMenuItem("Supprimer un(des) identifiant(s)");
		delIdentifiant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		delIdentifiant.addActionListener(new DelIdentifiantAction());
		
		edition.add(addIdentifiant);
		edition.add(delIdentifiant);
	}

	/**
	 * Initialise le menu "?" (menu d'aide)
	 */
	private void initHelpMenu() {
		aide = new JMenu("?");
		aide.setMnemonic(KeyEvent.VK_H);
		// TODO: "A propos", "notes de version", "manuel utilisation", "options"
		
		JMenuItem aPropos = new JMenuItem("A propos");
		aPropos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		aPropos.addActionListener(new AProposAction());
		
		JMenuItem versions = new JMenuItem("Notes de versions");
		versions.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		versions.addActionListener(new NoteVersionAction());
		
		JMenuItem manuel = new JMenuItem("Manuel utilisateur  [non implémenté]");
		manuel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
//		manuel.addActionListener(new AProposAction());
		
		JMenuItem option = new JMenuItem("Options de configuration  [non implémenté]");
		option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
//		option.addActionListener(new OptionAction());
		
		aide.add(aPropos);
		aide.add(versions);
		aide.add(manuel);
		aide.add(option);
	}

	/**
	 * Initialise le contenu central de la fenêtre (le tableau et son modèle)
	 */
	private void initContentPane() {
		tableau = new JTable();
		identifiantTM = new IdentifiantTableModel();
		SQLiteDAO.getInstance().addObserver(identifiantTM);
		tableau.setModel(identifiantTM);
		tableau.setAutoCreateRowSorter(true);
		tableau.getColumn("SITE").setCellRenderer(new FirstColumnRender());
		AppUtils.setConsoleMessage("contentPane initialisé.", Fenetre.class, MessageType.INFORMATION, 49, AppParams.DEBUG_MODE);
	}

	/**
	 * Initialise le panel inférieur (boutons d'ajout et suppression d'idientifiant)
	 */
	private void initBtnPane() {
		btnPane = new JPanel();
		JButton btnAdd = new JButton("Ajouter un identifiant");
		JButton btnDel = new JButton("Supprimer un(des) identifiant(s)");
		
		btnDel.setToolTipText("Sélectionnez une(des) ligne(s) dans la table puis cliquez sur ce bouton");
		
		btnAdd.addActionListener(new AddIdentifiantAction());
		btnDel.addActionListener(new DelIdentifiantAction());
		
		btnPane.add(btnAdd);
		btnPane.add(btnDel);
		AppUtils.setConsoleMessage("btnPane initialisé.", Fenetre.class, MessageType.INFORMATION, 62, AppParams.DEBUG_MODE);
	}
	
	/* --------------------------------------------------------------------------------------------------------------- */
	
	/* //////////////////////// */
	/* // INNER CLASS ACTION // */
	/* //////////////////////// */
	
	private class ImportFromCSVOrTXTAction extends AbstractAction {
		ArrayList<Identifiant> identifiants = new ArrayList<Identifiant>();
		
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Object[] importDial = ImportationDialog.open(Fenetre.this);
			System.out.println(importDial[0]);// valeur du bouton cliqué {int}
			System.out.println(importDial[1]);// chemin du fichier {String}
			System.out.println(importDial[2]);// séparateur {String}
			System.out.println(importDial[3]);// présence d'une en-tête de colonne {boolean}
			
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader((String) importDial[1]));// ouverture d'un flux de lecture du fichier
				String line = null;
				if ((boolean) importDial[3]) {
					br.readLine();// saute le titre
				}
				while ((line = br.readLine()) != null) {
					String[] splitedIdentifiant = line.split((String) importDial[2]);
					if (splitedIdentifiant.length == 3) {
						identifiants.add(new Identifiant(splitedIdentifiant[0], splitedIdentifiant[1], splitedIdentifiant[2]));
					}
					else {
						Identifiant identifiant = new Identifiant();
						identifiant.setSite(splitedIdentifiant[0]);
						identifiant.setLogin(splitedIdentifiant[1]);
						String mdp = "";
						for (int i=2; i<splitedIdentifiant.length; i++) {
							mdp += splitedIdentifiant[i];
						}
						identifiant.setMdp(mdp);
						identifiants.add(identifiant);
					}					
				}
				identifiantTM.setIdentifiants(identifiants);// enregistrement des données dans la table
			} catch (FileNotFoundException e2) {
				// TODO le fichier n'existe pas
				e2.printStackTrace();
			} catch (IOException e1) {
				// TODO pb lors de lecture de la ligne
				e1.printStackTrace();
			}
			finally {
				if (br != null)
					try {
						br.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		}		
	}// END inner class ImportFromCSVAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class ExportAsCSVAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub			
		}		
	}// END inner class ExportAsCSVAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class ExportAsPDFAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			final String DEFAULT_FILE_PATH = "./identifiants.pdf";
			String filePath = DEFAULT_FILE_PATH;// chemin de sortie du fichier
			String[] headers = identifiantTM.getENTETE();// en-têtes de colonnes
			ArrayList<Identifiant> datas = identifiantTM.getIdentifiants();// les données (identifiants)
			int nbColonnes = headers.length;
			int nbLignes = datas.size();
			
			// TODO: Proposer à l'utilisateur de saisir le répertoire d'enregistrement
			JFileChooser saveChooser = new JFileChooser();
			FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("Extension .pdf", "pdf");
			saveChooser.setFileFilter(pdfFilter);
			int btnClicked = saveChooser.showSaveDialog(Fenetre.this);
			if (btnClicked == JFileChooser.APPROVE_OPTION) {
				try {
					filePath = saveChooser.getSelectedFile().getCanonicalPath();
					if (filePath == null) filePath = DEFAULT_FILE_PATH;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else {// si l'utilisateur a cliqué sur la "croix" ou le bouton "annuler", on sort de l'action en cours.
				return;
			}
			
			// Trie par ordre croissant tous les identifiants en fonction du nom du site web
			Collections.sort(datas, new Comparator<Identifiant>() {
				public int compare(Identifiant a, Identifiant b) {
					return a.getSite().compareTo(b.getSite());
				}
			});
			
			// Prépare le document PDF
			Document document = new Document();
			Paragraph paragraphe = new Paragraph();
			PdfPTable pdfTable = new PdfPTable(nbColonnes);
			
			// Ajoute l'en-tête
			pdfTable.setHeaderRows(1);
			for (int i=0; i<nbColonnes; i++) {
				pdfTable.addCell(headers[i]);
			}
			
			// Ajoute les données
			for (int i=0; i<nbLignes; i++) {
				for (int j=0; j<nbColonnes; j++) {
					switch (j) {
						case 0:
							pdfTable.addCell(datas.get(i).getSite());
							break;
						case 1:
							pdfTable.addCell(datas.get(i).getLogin());
							break;
						case 2:
							pdfTable.addCell(datas.get(i).getMdp());
							break;
						default:
							System.out.println("Le numéro de colonne dépasse le nombre de colonnes dans la table PDF.");
							break;
					}
				}				
			}
			
			// Enregistre le document pdf créé dans le fichier de sortie (PDF)
			try {
				PdfWriter.getInstance(document, new FileOutputStream(filePath));
				document.open();
				document.add(paragraphe);
				document.add(pdfTable);
				document.close();
				AppUtils.showUserMessage("Fichier PDF créé avec succès.", JOptionPane.INFORMATION_MESSAGE);
			} catch (DocumentException | FileNotFoundException e1) {
				AppUtils.showUserMessage("Une erreur est survenue lors de l'exportation. Le fichier n'a pas été créé.", JOptionPane.INFORMATION_MESSAGE);
				e1.printStackTrace();
			}
		}		
	}// END inner class ExportAsPDFAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class ExportAsTXTAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub			
		}		
	}// END inner class ExportAsTXTAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class AddIdentifiantAction extends AbstractAction {
//		private AddIdentifiantDialog addIdentifiantDial;
		
		public void actionPerformed(ActionEvent e) {
			new AddIdentifiantDialog(Fenetre.this);
		}		
	}// END inner class AddIdentifiantAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class DelIdentifiantAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			int[] selection = tableau.getSelectedRows();
			
			for (int i=selection.length -1; i>=0; i--) {
				System.out.println("selectedRow : " + selection[i]);
				String site = (String) identifiantTM.getValueAt(selection[i], 0);
				System.out.println("site : " + site);
				SQLiteDAO.getInstance().delete(site);
				if (SQLiteDAO.getInstance().isDataDeleted()) {
					identifiantTM.removeIdentifiant(selection[i]);
				}
			}
		}		
	}// END inner class DelIdentifiantAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class QuitAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			System.exit(EXIT_ON_CLOSE);
		}		
	}// END inner class QuitAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class AProposAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			new AProposDialog(Fenetre.this);
		}		
	}// END inner class AProposAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class NoteVersionAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			new NoteVersionDialog(Fenetre.this);
		}		
	}// END inner class NoteVersionAction
	
}// END class Fenetre

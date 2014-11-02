package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import util.MessageType;
import util.AppParams;
import util.AppUtils;
import model.Identifiant;
import model.IdentifiantTableModel;
import model.MdpApp;
import model.dao.FileDAO;
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
		JMenuItem importFromXLS = new JMenuItem("XLS");
		
		importFromCSV.addActionListener(new ImportFromCSVOrTXTAction());
		importFromPDF.addActionListener(new ImportFromPDFAction());
		importFromTXT.addActionListener(new ImportFromCSVOrTXTAction());
		importFromXLS.addActionListener(new ImportFromXLSAction());
		
		importFrom.add(importFromCSV);
		importFrom.add(importFromPDF);
		importFrom.add(importFromTXT);
		importFrom.add(importFromXLS);
		
		// EXPORTER
		JMenu exportAs = new JMenu("Exporter au format...");
		JMenuItem exportAsCSV = new JMenuItem("CSV");
		JMenuItem exportAsPDF = new JMenuItem("PDF");
		JMenuItem exportAsTXT = new JMenuItem("TXT");
		JMenuItem exportAsXLS = new JMenuItem("XLS");
		
		exportAsCSV.addActionListener(new ExportAsCSVAction());
		exportAsPDF.addActionListener(new ExportAsPDFAction());
		exportAsTXT.addActionListener(new ExportAsTXTAction());
		exportAsXLS.addActionListener(new ExportAsXLSAction());
		
		exportAs.add(exportAsCSV);
		exportAs.add(exportAsPDF);
		exportAs.add(exportAsTXT);
		exportAs.add(exportAsXLS);
		
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
		
		JMenuItem abortSuppression = new JMenuItem("Annuler la suppression");
		abortSuppression.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		abortSuppression.addActionListener(new AbortSuppressionAction());
		
		JMenuItem addIdentifiant = new JMenuItem("Ajouter un identifiant");
		addIdentifiant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		addIdentifiant.addActionListener(new AddIdentifiantAction());
		
		JMenuItem delIdentifiant = new JMenuItem("Supprimer un(des) identifiant(s)");
		delIdentifiant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		delIdentifiant.addActionListener(new DelIdentifiantAction());
		
		edition.add(abortSuppression);
		edition.addSeparator();
		edition.add(addIdentifiant);
		edition.add(delIdentifiant);
	}

	/**
	 * Initialise le menu "?" (menu d'aide)
	 */
	private void initHelpMenu() {
		aide = new JMenu("?");
		aide.setMnemonic(KeyEvent.VK_H);
		// TODO: "manuel utilisation", "options"
		
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
		
		JMenuItem appPwd = new JMenuItem("Changer le mot de passe applicatif");
		appPwd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		appPwd.addActionListener(new ChangePasswordAction());
		
		aide.add(aPropos);
		aide.add(versions);
		aide.add(manuel);
		aide.add(option);
		aide.addSeparator();
		aide.add(appPwd);
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
			Object[] importDialConfig = ImportationDialog.open(Fenetre.this);
			System.out.println(importDialConfig[0]);// valeur du bouton cliqué {int}
			System.out.println(importDialConfig[1]);// chemin du fichier {String}
			System.out.println(importDialConfig[2]);// séparateur {String}
			System.out.println(importDialConfig[3]);// présence d'une en-tête de colonne {boolean}
			
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader((String) importDialConfig[1]));// ouverture d'un flux de lecture du fichier
				String line = null;
				if ((boolean) importDialConfig[3]) {
					br.readLine();// saute le titre
				}
				while ((line = br.readLine()) != null) {
					String[] splitedIdentifiant = line.split((String) importDialConfig[2]);
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
	private class ImportFromPDFAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			ArrayList<Identifiant> datas = new ArrayList<Identifiant>();
			FileInputStream fis = null;
			
			Object[] pdfConfig = PdfImportationDialog.open(Fenetre.this);
			// pdfConfig[0] {int} = bouton cliqué
			// pdfConfig[1] {String} = chemin du fichier
			// pdfConfig[3] {boolean} = présence d'une en-tête
			
			// si l'utilisateur n'a renseigné aucun fichier, on quitte
			if (pdfConfig[1] == null) {
				return;
			}
			
			// si l'utilisateur a cliqué sur "OK"
			if ((int) pdfConfig[0] == PdfImportationDialog.OK_BTN) {
				try {
					fis = new FileInputStream(new File((String) pdfConfig[1]));
					
					PdfReader reader = new PdfReader(fis);
					int nop = reader.getNumberOfPages();
					
					// TODO: améliorer algorithme (pb avec les espaces + redondance code)
					for (int i=0; i<nop; i++) {
						String page = PdfTextExtractor.getTextFromPage(reader, i+1);
						String[] lines = page.split("\n");
						
						// s'il y a une en-tête de colonne, on passe directement à la ligne suivante
						if ((boolean) pdfConfig[2]) {
							
							// lit les lignes, une par une
							for (int j=1; j<lines.length; j++) {
								String[] prop = lines[j].split(" ");// split la ligne en chaque propriété d'un identifiant de connexion
								Identifiant identifiant = new Identifiant();// initialise un nouvel identifiant
								
								// lit les propriétés de l'identifiant, une par une
								for (int k=0; k<prop.length; k++) {
									
									switch(k) {
										case 0:
											identifiant.setSite(prop[k]);
											break;
										case 1:
											identifiant.setLogin(prop[k]);
											break;
										case 2:
											identifiant.setMdp(prop[k]);
											break;
										default:
											System.out.println("L'indice dépasse l'indice maximal.");
											break;
									}
								}
								datas.add(identifiant);
							}
						}
						// s'il n'y a pas d'en-tête de colonne, on commence directement à la première ligne
						else {
							// lit les lignes, une par une
							for (int j=0; j<lines.length; j++) {
								String[] prop = lines[j].split(" ");// split la ligne en chaque propriété d'un identifiant de connexion
								Identifiant identifiant = new Identifiant();// initialise un nouvel identifiant
								
								// lit les propriétés de l'identifiant, une par une
								for (int k=0; k<prop.length; k++) {
									
									switch(k) {
										case 0:
											identifiant.setSite(prop[k]);
											break;
										case 1:
											identifiant.setLogin(prop[k]);
											break;
										case 2:
											identifiant.setMdp(prop[k]);
											break;
										default:
											System.out.println("L'indice dépasse l'indice maximal.");
											break;
									}
								}
								datas.add(identifiant);
							}
						}
						identifiantTM.setIdentifiants(datas);
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if (fis != null) fis.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			// sinon, l'utilisateur a cliqué sur la "croix" ou "annuler"
			else {
				return;
			}
		}
		
	}// END inner class ImportFromPDFAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class ImportFromXLSAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			ArrayList<Identifiant> datas = new ArrayList<Identifiant>();
			FileInputStream fis = null;
			
			Object[] xlsConfig = XlsImportationDialog.open(Fenetre.this);
			// xlsConfig[0] {int} = bouton cliqué
			// xlsConfig[1] {String} = chemin du fichier
			// xlsConfig[3] {boolean} = présence d'une en-tête
			
			if (xlsConfig[1] == null) {
				return;
			}
			
			// si l'utilisateur a cliqué sur "OK"
			if ((int) xlsConfig[0] == XlsImportationDialog.OK_BTN) {
				try {
					fis = new FileInputStream(new File((String) xlsConfig[1]));
					
					HSSFWorkbook classeur = new HSSFWorkbook(fis);
					HSSFSheet feuille = classeur.getSheet("Identifiants");
					int nbCol = feuille.getRow(0).getLastCellNum();
					int nbLignes = feuille.getLastRowNum();
					
					// s'il y a une en-tête de colonne
					if ((boolean) xlsConfig[2]) {
						for (int i=1; i<=nbLignes; i++) {
							HSSFRow ligne = feuille.getRow(i);
							Identifiant identifiant = new Identifiant();
							
							for (int j=0; j<nbCol; j++) {
								switch(j) {
									case 0:
										identifiant.setSite(ligne.getCell(j).getStringCellValue());
										break;
									case 1:
										identifiant.setLogin(ligne.getCell(j).getStringCellValue());
										break;
									case 2:
										identifiant.setMdp(ligne.getCell(j).getStringCellValue());
										break;
									default:
										System.out.println("L'indice dépasse l'indice maximal de colonnes.");
										break;
								}
							}
							datas.add(identifiant);
						}
						identifiantTM.setIdentifiants(datas);
					}
					// sinon, il n'y a pas d'en-tête
					else {
						for (int i=0; i<=nbLignes; i++) {
							HSSFRow ligne = feuille.getRow(i);
							Identifiant identifiant = new Identifiant();
							
							for (int j=0; j<nbCol; j++) {
								switch(j) {
									case 0:
										identifiant.setSite(ligne.getCell(j).getStringCellValue());
										break;
									case 1:
										identifiant.setLogin(ligne.getCell(j).getStringCellValue());
										break;
									case 2:
										identifiant.setMdp(ligne.getCell(j).getStringCellValue());
										break;
									default:
										System.out.println("L'indice dépasse l'indice maximal de colonnes.");
										break;
								}
							}
							datas.add(identifiant);
						}
						identifiantTM.setIdentifiants(datas);
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if (fis != null) fis.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			// sinon, l'utilisateur a cliqué sur la "croix" ou "annuler"
			else {
				return;
			}
		}
		
	}// END inner class ImportFromXLSAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class ExportAsCSVAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			final String DEFAULT_FILE_PATH = "./identifiants.csv";
			String filePath = DEFAULT_FILE_PATH;// chemin de sortie du fichier
			String separateur = ";";
			String[] headers = null;// en-têtes de colonnes
			ArrayList<Identifiant> datas = identifiantTM.getIdentifiants();// les données (identifiants)
			
			
			Object[] CsvExportationConfig = CsvExportationDialog.open(Fenetre.this);
			System.out.println(CsvExportationConfig[0]);// valeur du bouton cliqué {int}
			System.out.println(CsvExportationConfig[1]);// chemin du fichier {String}
			System.out.println(CsvExportationConfig[2]);// séparateur {String}
			System.out.println(CsvExportationConfig[3]);// les en-têtes de colonne {String}
			
			// si l'utilisateur a cliqué sur OK, on récupère les données
			if ((int) CsvExportationConfig[0] == CsvExportationDialog.OK_BTN) {
				filePath = (String) CsvExportationConfig[1] == null ? DEFAULT_FILE_PATH : (String) CsvExportationConfig[1];
				headers = (String) CsvExportationConfig[3] == null ? null : ((String) CsvExportationConfig[3]).split(";");
				separateur = (String) CsvExportationConfig[2];
			}
			// sinon, il a cliqué sur la "croix" ou "annuler"
			else {
				return;
			}
			
			// Trie par ordre croissant tous les identifiants en fonction du nom du site web
			Collections.sort(datas, new Comparator<Identifiant>() {
				public int compare(Identifiant a, Identifiant b) {
					return a.getSite().compareTo(b.getSite());
				}
			});
			
			// prépare le fichier CSV
			try {
				BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8);
				
				String out = "";
				if ( headers != null && headers.length == 3) {
					bw.write(headers[0] + separateur + headers[1] + separateur + headers[2]);
					bw.newLine();
				}
				
				for (int i=0; i<datas.size(); i++) {
					bw.write(datas.get(i).getSite() + separateur + datas.get(i).getLogin() + separateur + datas.get(i).getMdp());
					if (i < datas.size() -1) {
						bw.newLine();
					}
				}
				bw.close();
				AppUtils.showUserMessage("Succès de l'exportation au format CSV.", JOptionPane.INFORMATION_MESSAGE);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
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
			
			// Propose à l'utilisateur de saisir le répertoire d'enregistrement
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
				AppUtils.showUserMessage("Succès de l'exportation au format PDF.", JOptionPane.INFORMATION_MESSAGE);
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
		
		public void actionPerformed(ActionEvent e) {
			final String DEFAULT_FILE_PATH = "./identifiants.txt";
			String filePath = DEFAULT_FILE_PATH;// chemin de sortie du fichier
			String separateur = ";";
			String[] headers = null;// en-têtes de colonnes
			ArrayList<Identifiant> datas = identifiantTM.getIdentifiants();// les données (identifiants)
			
			
			Object[] txtExportationConfig = TxtExportationDialog.open(Fenetre.this);
			System.out.println(txtExportationConfig[0]);// valeur du bouton cliqué {int}
			System.out.println(txtExportationConfig[1]);// chemin du fichier {String}
			System.out.println(txtExportationConfig[2]);// séparateur {String}
			System.out.println(txtExportationConfig[3]);// les en-têtes de colonne {String}
			
			// si l'utilisateur a cliqué sur OK, on récupère les données
			if ((int) txtExportationConfig[0] == TxtExportationDialog.OK_BTN) {
				filePath = (String) txtExportationConfig[1] == null ? DEFAULT_FILE_PATH : (String) txtExportationConfig[1];
				headers = (String) txtExportationConfig[3] == null ? null : ((String) txtExportationConfig[3]).split(";");
				separateur = (String) txtExportationConfig[2];
			}
			// sinon, il a cliqué sur la "croix" ou "annuler"
			else {
				return;
			}
			
			// Trie par ordre croissant tous les identifiants en fonction du nom du site web
			Collections.sort(datas, new Comparator<Identifiant>() {
				public int compare(Identifiant a, Identifiant b) {
					return a.getSite().compareTo(b.getSite());
				}
			});
			
			// prépare le fichier TXT
			try {
				BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8);
				
				String out = "";
				if ( headers != null && headers.length == 3) {
					bw.write(headers[0] + separateur + headers[1] + separateur + headers[2]);
					bw.newLine();
				}
				
				for (int i=0; i<datas.size(); i++) {
					bw.write(datas.get(i).getSite() + separateur + datas.get(i).getLogin() + separateur + datas.get(i).getMdp());
					if (i < datas.size() -1) {
						bw.newLine();
					}
				}
				bw.close();
				AppUtils.showUserMessage("Succès de l'exportation au format TXT.", JOptionPane.INFORMATION_MESSAGE);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}		
	}// END inner class ExportAsTXTAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class ExportAsXLSAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			final String DEFAULT_FILE_PATH = "./identifiants.xls";
			String filePath = DEFAULT_FILE_PATH;// chemin de sortie du fichier
			FileOutputStream fos = null;
			String separateur = ";";
			String[] headers = null;// en-têtes de colonnes
			ArrayList<Identifiant> datas = identifiantTM.getIdentifiants();// les données (identifiants)
			int nbColonnes = identifiantTM.getColumnCount();
			int nbLignes = identifiantTM.getRowCount();
			
			
			Object[] txtExportationConfig = XlsExportationDialog.open(Fenetre.this);
			System.out.println(txtExportationConfig[0]);// valeur du bouton cliqué {int}
			System.out.println(txtExportationConfig[1]);// chemin du fichier {String}
			System.out.println(txtExportationConfig[2]);// séparateur {String}
			System.out.println(txtExportationConfig[3]);// les en-têtes de colonne {String}
			
			// si l'utilisateur a cliqué sur OK, on récupère les données
			if ((int) txtExportationConfig[0] == XlsExportationDialog.OK_BTN) {
				filePath = (String) txtExportationConfig[1] == null ? DEFAULT_FILE_PATH : (String) txtExportationConfig[1];
				headers = (String) txtExportationConfig[3] == null ? null : ((String) txtExportationConfig[3]).split(";");
				separateur = (String) txtExportationConfig[2];
			}
			// sinon, il a cliqué sur la "croix" ou "annuler"
			else {
				return;
			}
			
			// Trie par ordre croissant tous les identifiants en fonction du nom du site web
			Collections.sort(datas, new Comparator<Identifiant>() {
				public int compare(Identifiant a, Identifiant b) {
					return a.getSite().compareTo(b.getSite());
				}
			});
			
			// prépare le fichier XLS
			try {
				HSSFWorkbook classeur = new HSSFWorkbook();
				HSSFSheet feuille = classeur.createSheet("Identifiants");
				
				// s'il y a une en-tête de colonne
				if (headers != null && headers.length == 3) {
					// on ajoute l'en-tête
					HSSFRow entete = feuille.createRow(0);// première ligne = en-tête
					for (int i=0; i<headers.length; i++) {
						HSSFCell cellule = entete.createCell(i, HSSFCell.CELL_TYPE_STRING);
						cellule.setCellValue(headers[i]);
					}
					
					// on ajoute les identifiants
					for (int i=0; i<datas.size(); i++) {
						HSSFRow ligne = feuille.createRow(i+1);
						
						for (int j=0; j<nbColonnes; j++) {
							HSSFCell cellule = ligne.createCell(j, HSSFCell.CELL_TYPE_STRING);

							switch(j) {
								case 0:
									cellule.setCellValue(datas.get(i).getSite());
									break;
								case 1:
									cellule.setCellValue(datas.get(i).getLogin());
									break;
								case 2:
									cellule.setCellValue(datas.get(i).getMdp());
									break;
								default:
									System.out.println("L'indice dépasse l'indice maximum de colonnes");
									break;
							}
						}
					}
				}
				// sinon, il n'y a pas d'en-tête
				else {
					// on ajoute les identifiants
					for (int i=0; i<datas.size(); i++) {
						HSSFRow ligne = feuille.createRow(i);
						
						for (int j=0; j<nbColonnes; j++) {
							HSSFCell cellule = ligne.createCell(j, HSSFCell.CELL_TYPE_STRING);

							switch(j) {
								case 0:
									cellule.setCellValue(datas.get(i).getSite());
									break;
								case 1:
									cellule.setCellValue(datas.get(i).getLogin());
									break;
								case 2:
									cellule.setCellValue(datas.get(i).getMdp());
									break;
								default:
									System.out.println("L'indice dépasse l'indice maximum de colonnes");
									break;
							}
						}
					}
				}
				fos = new FileOutputStream(new File(filePath));
				classeur.write(fos);
				AppUtils.showUserMessage("Succès de l'exportation au format XLS.", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}		
	}// END inner class ExportAsCSVAction
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class AbortSuppressionAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			Identifiant lastDeletedIdentifiant = identifiantTM.getLastDeletedLogin();
			if (lastDeletedIdentifiant == null) {
				return;
			}
			SQLiteDAO.getInstance().save(lastDeletedIdentifiant.getSite(), lastDeletedIdentifiant.getLogin(), lastDeletedIdentifiant.getMdp());
			if (SQLiteDAO.getInstance().isDataSaved()) {
				identifiantTM.removeLastDeletedLogin();
			}
		}		
	}// END inner class AbortSuppressionAction
	
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
	
	/**
	 * 
	 * @author Sainsain
	 *
	 */
	private class ChangePasswordAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			ArrayList<Identifiant> identifiants = null;
			/** res[0]: bouton cliqué, res[1]: ancien mot de passe, res[2]: nouveau mot de passe */
			Object[] res = {AppPasswordAlterationDialog.DEFAULT_BTN, null, null};
			do {
				res = AppPasswordAlterationDialog.open(Fenetre.this);
			} while((int) res[0] == AppPasswordAlterationDialog.OK_BTN && res[1] == null && res[2] == null);
			
			if ((int) res[0] != AppPasswordAlterationDialog.OK_BTN) { return; }
			
			// Vérifie si l'ancien mot de passe (saisi par l'utilisateur) est différent du mot de passe applicatif
			MdpApp mdpApp = FileDAO.getInstance().getAppPassword(new File("./mdpmngr.cfg"));// récupère le mot de passe applicatif (crypté)
			String oldMdpApp = DigestUtils.sha256Hex(((String) res[1]).getBytes());// chiffre le mot de passe
			if (!oldMdpApp.equals(mdpApp.getMdpSha256())) {
				AppUtils.showUserMessage("Echec : L'ancien mot de passe et le mot de passe applicatif sont différents.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// Sinon, remplace le mot de passe applicatif par le nouveau
			String mdpEncrypted = DigestUtils.sha256Hex(((String) res[2]).getBytes());
			boolean mdpSaved = FileDAO.getInstance().saveAppPassword(new MdpApp(mdpEncrypted), new File("./mdpmngr.cfg"));
			
			// si le nouveau mot de passe n'a pas été enregistré, on annule tout
			if (!mdpSaved) {
				AppUtils.showUserMessage("Echec de l'enregistrement du nouveau mot de passe.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			AppUtils.showUserMessage("Succès de l'enregistrement du nouveau mot de passe.", JOptionPane.INFORMATION_MESSAGE);
						
			// sinon, on crypte les identifiants avec la nouvelle clef (=le nouveau mot de passe)
			SQLiteDAO.getInstance().setKey((String) res[2]);// affecte la nouvelle clef (= le nouveau mot de passe)
			identifiants = identifiantTM.getIdentifiants();
			for (int i=0; i<identifiants.size(); i++) {
				SQLiteDAO.getInstance().update(identifiants.get(i));
			}
		}		
	}// END inner class ChangePasswordAction
	
}// END class Fenetre

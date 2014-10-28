package model;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import util.AppParams;
import util.AppUtils;
import util.MessageType;
import model.dao.SQLiteDAO;

public class IdentifiantTableModel extends AbstractTableModel implements Observer {
	private final String[] ENTETE = {"SITE", "LOGIN", "MOT DE PASSE"};
	private ArrayList<Identifiant> identifiants = null;
	private ArrayList<Identifiant> deletedLogins = new ArrayList<Identifiant>();

	//
	// GETTERS
	//
	public String[] getENTETE() {
		return this.ENTETE;
	}

	public ArrayList<Identifiant> getIdentifiants() {
		return this.identifiants;
	}
	
	public ArrayList<Identifiant> getDeletedLogins() {
		if (this.deletedLogins != null && this.deletedLogins.size() > 0) {
			return this.deletedLogins;
		}
		return null;
	}
	
	public Identifiant getLastDeletedLogin() {
		if (this.deletedLogins != null && this.deletedLogins.size() > 0) {
			int lastLoginIndex = this.deletedLogins.size() - 1;
			return this.deletedLogins.get(lastLoginIndex);
		}
		return null;
	}

	//
	// CONSTRUCTEUR
	//
	public IdentifiantTableModel() {
//		identifiants = new ArrayList<Identifiant>();
		identifiants = SQLiteDAO.getInstance().fetchAll();
	}
	
	public int getRowCount() {
		return this.identifiants.size();
	}
	
	public int getColumnCount() {
		return this.ENTETE.length;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
			case 0:
				return this.identifiants.get(rowIndex).getSite();
			case 1:
				return this.identifiants.get(rowIndex).getLogin();
			case 2:
				return this.identifiants.get(rowIndex).getMdp();
			default:
				return -1;// ne devrait jamais arriver
		}
	}
	
	public String getColumnName(int columnIndex) {
		return this.ENTETE[columnIndex];
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex > 0) {
			return true;
		}
		return false;
	}
	
	public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
		String oldValue = null;
		String siteName = null;
		
		switch(columnIndex) {
			case 0:
				oldValue = this.identifiants.get(rowIndex).getSite();
				this.identifiants.get(rowIndex).setSite((String) newValue);
				break;
			case 1:
				oldValue = this.identifiants.get(rowIndex).getLogin();
				siteName = this.identifiants.get(rowIndex).getSite();
				this.identifiants.get(rowIndex).setLogin((String) newValue);
				break;
			case 2:
				oldValue = this.identifiants.get(rowIndex).getMdp();
				siteName = this.identifiants.get(rowIndex).getSite();
				this.identifiants.get(rowIndex).setMdp((String) newValue);
				break;
			default:
				System.out.println("ERREUR : Le numéro de colonne dépasse le nombre de colonnes du modèle");
				break;
		}
		
		if (oldValue.equals("") || oldValue == null) {
			return;
		}
		if (newValue.equals("") || newValue == null) {
			return;
		}
		if (columnIndex > 0 && columnIndex < this.getColumnCount() && !newValue.equals(oldValue)) {
			SQLiteDAO.getInstance().encryptAndSave((String) newValue, columnIndex, siteName);
			if (SQLiteDAO.getInstance().isDataSaved()) {
				this.fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
	}
	
	/**
	 * Ajoute un identifiant au modèle
	 * @param obj [Identifiant} : l'identifiant à ajouter au modèle
	 */
	public void addIdentifiant(Identifiant obj) {
		if (obj instanceof Identifiant) {
			System.out.println("nb d'identfiants avant ajout : " + this.getRowCount());
			listSites();
			this.identifiants.add(obj);
			System.out.println("nb d'identfiants après ajout : " + this.getRowCount());
			this.fireTableRowsInserted(this.getRowCount() - 1, this.getRowCount() - 1);
		}
	}
	
	/**
	 * Supprime un identifiant du modèle à l'indice passé en paramètre
	 * @param rowIndex {int} : l'indice dans le tableau pour lequel il faut supprimer l'identifiant
	 */
	public void removeIdentifiant(int rowIndex) {
		System.out.println("nb d'identfiants avant suppression : " + this.getRowCount());
		System.out.println(rowIndex);
		listSites();
		this.deletedLogins.add(this.identifiants.get(rowIndex));// mémorise l'identifiant supprimé pour pouvoir annuler sa suppression par la suite, si l'utilisateur le souhaite
		this.identifiants.remove(rowIndex);
		System.out.println("nb d'identfiants  après suppression : " + this.getRowCount());
		this.fireTableRowsDeleted(rowIndex, rowIndex);
	}
	
	/**
	 * Met à jour la liste des identifiants après en avoir été notifié
	 */
	public void update(Observable observable, Object obj) {
		AppUtils.setConsoleMessage(((Identifiant) obj).toString(), IdentifiantTableModel.class, MessageType.INFORMATION, 136, AppParams.DEBUG_MODE);
		this.addIdentifiant((Identifiant) obj);
	}
	
	/**
	 * 
	 */
	public void listSites() {
		for (int i=0; i<identifiants.size(); i++) {
			System.out.println("identifiants[" + i + "] = " +identifiants.get(i).getSite());
		}
	}

	/**
	 * 
	 * @param identifiants
	 */
	public void setIdentifiants(ArrayList<Identifiant> identifiants) {
		ArrayList<Identifiant> oldIdentifiants = getIdentifiants();
		this.identifiants = identifiants;// affecte la liste d'identifiants (importée d'un fichier) à la liste du modèle
		SQLiteDAO.getInstance().saveLoginsList(this.identifiants);// enregistre toutes ces nouvelles données dans la BDD
		if (SQLiteDAO.getInstance().isDataSaved()) {// si toutes les données ont été insérées...
			for (int i=0; i<oldIdentifiants.size(); i++) {
				this.identifiants.add(oldIdentifiants.get(i));// rajoute les anciennes données pour refléter celles de la BDD
			}
			this.fireTableDataChanged();// notifie à la table de se mettre à jour dans l'interface
		}		
	}

	/**
	 * 
	 */
	public void removeLastDeletedLogin() {
		int indice = this.deletedLogins.size() - 1;
		this.deletedLogins.remove(indice);
	}

}// END class IdentifiantTableModel

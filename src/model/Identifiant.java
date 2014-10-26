package model;

import java.util.ArrayList;

public class Identifiant {
	
	/* //////////////// */
	/* // PROPRIETES // */
	/* //////////////// */
	
	private Integer id;
	private String site;
	private String login;
	private String mdp;
	
	
	/* /////////////////// */
	/* // CONSTRUCTEURS // */
	/* /////////////////// */
	
	public Identifiant() { }

	public Identifiant(String site, String login, String mdp) {
		super();
		this.site = site;
		this.login = login;
		this.mdp = mdp;
	}

	public Identifiant(Integer id, String site, String login, String mdp) {
		super();
		this.id = id;
		this.site = site;
		this.login = login;
		this.mdp = mdp;
	}

	
	/* ///////////// */
	/* // GETTERS // */
	/* ///////////// */
	
	public Integer getId() {
		return id;
	}

	public String getSite() {
		return site;
	}

	public String getLogin() {
		return login;
	}

	public String getMdp() {
		return mdp;
	}

	
	/* ///////////// */
	/* // SETTERS // */
	/* ///////////// */
	
	public void setId(Integer id) {
		this.id = id;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	
	
	/* ///////////// */
	/* // METHODS // */
	/* ///////////// */
	
	@Override
	public String toString() {
		String identifiant = "*** Identifiant ***\n";
		identifiant += "ID = " + this.id + "\n";
		identifiant += "SITE = " + this.site + "\n";
		identifiant += "LOGIN = " + this.login + "\n";
		identifiant += "MDP = " + this.mdp + "\n";
		return identifiant;
	}
	
}// END class Identifiant

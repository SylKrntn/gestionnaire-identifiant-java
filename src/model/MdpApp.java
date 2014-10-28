package model;

import java.io.Serializable;

public class MdpApp implements Serializable {

	private String sha256;
	
	public MdpApp() { }
	
	public MdpApp(String mdpEncrypted) {
		this.sha256 = mdpEncrypted;
	}
	
	public String getMdpSha256() {
		return this.sha256;
	}
	
	public void setMdpSha256(String mdpEncrypted) {
		this.sha256 = mdpEncrypted;
	}
}

package org.djv.stockresearcher.model;

public class WebSiteAuth {
	
	String host;
	String userName;
	String passEnc;
	String twoFactorScheme;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassEnc() {
		return passEnc;
	}
	public void setPassEnc(String passEnc) {
		this.passEnc = passEnc;
	}
	public String getTwoFactorScheme() {
		return twoFactorScheme;
	}
	public void setTwoFactorScheme(String twoFactorScheme) {
		this.twoFactorScheme = twoFactorScheme;
	}

}

package edu.kit.scc.webreg.service.twofa.linotp;

public class LinotpGetBackupTanListResult {

	private boolean status;
	private LinotpGetBackupTanListValue value;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public LinotpGetBackupTanListValue getValue() {
		return value;
	}

	public void setValue(LinotpGetBackupTanListValue value) {
		this.value = value;
	}

}

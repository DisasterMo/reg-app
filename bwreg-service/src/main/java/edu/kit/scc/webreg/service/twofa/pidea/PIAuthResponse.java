package edu.kit.scc.webreg.service.twofa.pidea;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PIAuthResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String version;
	
	@JsonProperty("jsonrpc")
	private String jsonRpc;
	
	private PIAuthResult result;
	
	private Integer id;

	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getJsonRpc() {
		return jsonRpc;
	}
	
	public void setJsonRpc(String jsonRpc) {
		this.jsonRpc = jsonRpc;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PIAuthResult getResult() {
		return result;
	}

	public void setResult(PIAuthResult result) {
		this.result = result;
	}
}

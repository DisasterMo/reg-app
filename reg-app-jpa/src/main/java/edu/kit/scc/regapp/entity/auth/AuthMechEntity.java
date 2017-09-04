package edu.kit.scc.regapp.entity.auth;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import edu.kit.scc.regapp.entity.AbstractBaseEntity;

@Entity
@Table(name = "authmech")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AuthMechEntity extends AbstractBaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "name", length = 128)
	private String name;

	@ElementCollection
	private List<String> hostNameList;
	
	public List<String> getHostNameList() {
		return hostNameList;
	}

	public void setHostNameList(List<String> hostNameList) {
		this.hostNameList = hostNameList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

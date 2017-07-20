package edu.kit.scc.webreg.entity.account;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import edu.kit.scc.webreg.entity.AbstractBaseEntity;

@Entity(name = "AccountEntity")
@Table(name = "account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AccountEntity extends AbstractBaseEntity {

	private static final long serialVersionUID = 1L;

	@ElementCollection
	@JoinTable(name = "account_attribute_store")
    @MapKeyColumn(name = "key_data", length = 1024)
    @Column(name = "value_data", length = 2048)
    private Map<String, String> accountStore;

	public Map<String, String> getAccountStore() {
		return accountStore;
	}

	public void setAccountStore(Map<String, String> accountStore) {
		this.accountStore = accountStore;
	} 
}

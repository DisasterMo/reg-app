package edu.kit.scc.webreg.entity.account;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import edu.kit.scc.webreg.entity.AbstractBaseEntity;
import edu.kit.scc.webreg.entity.UserEntity;

@Entity(name = "AccountEntity")
@Table(name = "account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AccountEntity extends AbstractBaseEntity {

	private static final long serialVersionUID = 1L;

    @Id
	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	
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

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	} 
}

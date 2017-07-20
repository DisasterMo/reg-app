package edu.kit.scc.webreg.entity.account;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.kit.scc.webreg.entity.AbstractBaseEntity;
import edu.kit.scc.webreg.entity.UserEntity;

@Entity(name = "AccountEntity")
@Table(name = "account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AccountEntity extends AbstractBaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	
	@ElementCollection
	@JoinTable(name = "account_attribute_store")
    @MapKeyColumn(name = "key_data", length = 1024)
    @Column(name = "value_data", length = 2048)
    private Map<String, String> accountStore;

	@Column(name = "global_id", length = 1024)
	private String globalId;

	@OneToMany(targetEntity = AccountGroupEntity.class, mappedBy="account")
	private Set<AccountGroupEntity> groups;		

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

	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public Set<AccountGroupEntity> getGroups() {
		return groups;
	}

	public void setGroups(Set<AccountGroupEntity> groups) {
		this.groups = groups;
	} 
}

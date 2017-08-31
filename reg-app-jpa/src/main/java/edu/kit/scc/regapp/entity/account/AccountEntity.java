package edu.kit.scc.regapp.entity.account;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.kit.scc.regapp.entity.AbstractBaseEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.UserStatus;

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

	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	
	@Column(name = "last_update")
	private Date lastUpdate;
	
	@Column(name = "last_failed_update")
	private Date lastFailedUpdate;

	@Column(name = "last_status_change")
	private Date lastStatusChange;
		
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

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Date getLastFailedUpdate() {
		return lastFailedUpdate;
	}

	public void setLastFailedUpdate(Date lastFailedUpdate) {
		this.lastFailedUpdate = lastFailedUpdate;
	}

	public Date getLastStatusChange() {
		return lastStatusChange;
	}

	public void setLastStatusChange(Date lastStatusChange) {
		this.lastStatusChange = lastStatusChange;
	} 
}

package edu.kit.scc.webreg.entity.account;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import edu.kit.scc.webreg.entity.AbstractBaseEntity;

@Entity(name = "AccountEntity")
@Table(name = "account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AccountEntity extends AbstractBaseEntity {

	private static final long serialVersionUID = 1L;


}

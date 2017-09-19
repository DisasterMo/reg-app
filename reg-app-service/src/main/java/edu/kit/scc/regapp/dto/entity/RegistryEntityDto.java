package edu.kit.scc.regapp.dto.entity;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import edu.kit.scc.regapp.entity.AgreementTextEntity;
import edu.kit.scc.regapp.entity.RegistryStatus;

public class RegistryEntityDto extends AbstractBaseEntityDto {

	private static final long serialVersionUID = 1L;

	private RegistryStatus registryStatus;
	
	private Set<AgreementTextEntity> agreedTexts;
	
	private Date agreedTime;
	
    private Map<String, String> registryValues; 
	
	private Date lastReconcile;
	
	private Date lastFullReconcile;
	
	private Date lastStatusChange;
	
	private Date lastAccessCheck;

	private Long userId;
	
	private Long serviceId;
	
	private String serviceName;
	
	private String serviceShortDescription;
	
	public RegistryStatus getRegistryStatus() {
		return registryStatus;
	}

	public void setRegistryStatus(RegistryStatus registryStatus) {
		this.registryStatus = registryStatus;
	}

	public Set<AgreementTextEntity> getAgreedTexts() {
		return agreedTexts;
	}

	public void setAgreedTexts(Set<AgreementTextEntity> agreedTexts) {
		this.agreedTexts = agreedTexts;
	}

	public Date getAgreedTime() {
		return agreedTime;
	}

	public void setAgreedTime(Date agreedTime) {
		this.agreedTime = agreedTime;
	}

	public Map<String, String> getRegistryValues() {
		return registryValues;
	}

	public void setRegistryValues(Map<String, String> registryValues) {
		this.registryValues = registryValues;
	}

	public Date getLastReconcile() {
		return lastReconcile;
	}

	public void setLastReconcile(Date lastReconcile) {
		this.lastReconcile = lastReconcile;
	}

	public Date getLastFullReconcile() {
		return lastFullReconcile;
	}

	public void setLastFullReconcile(Date lastFullReconcile) {
		this.lastFullReconcile = lastFullReconcile;
	}

	public Date getLastStatusChange() {
		return lastStatusChange;
	}

	public void setLastStatusChange(Date lastStatusChange) {
		this.lastStatusChange = lastStatusChange;
	}

	public Date getLastAccessCheck() {
		return lastAccessCheck;
	}

	public void setLastAccessCheck(Date lastAccessCheck) {
		this.lastAccessCheck = lastAccessCheck;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceShortDescription() {
		return serviceShortDescription;
	}

	public void setServiceShortDescription(String serviceShortDescription) {
		this.serviceShortDescription = serviceShortDescription;
	}
}

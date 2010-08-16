package org.kuali.rice.kew.ria.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Class which represents bo for mapping between ria name and ria url.
 * 
 * @author mpk35
 *
 */
@Entity
@Table(name="KEW_RIA_DOCTYPE_MAP_T")
public class RIADocTypeMap extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = -9128875892852099416L;
	
	@Id
	@Column(name="ID")
	private Long ID;
	
	@Column(name="RIA_DOC_TYPE_NAME")
	private String riaDocTypeName;
	
	// flag which determines if the document should be editable after submission
	@Column(name="EDITABLE")
	private boolean editable;

	// name of the groups separated by comma 
	// which can access the document. if initGroups == null everybody can access it
	@Column(name="INIT_GROUPS", length=255)
	private String initGroups;
	
	@Column(name="HELP_URL")	
	private String helpUrl;
	
	@Column(name="RIA_URL")	
	private String riaUrl;
	
	@Column(name="UPDATED_AT")	
	private Date updatedAt;
	
	public RIADocTypeMap() {
		super();
		this.setUpdatedAt(new Date(new java.util.Date().getTime()));
	}
	
	@Override
	protected LinkedHashMap<String, Object> toStringMapper() {
		LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
		propMap.put("ID", getID());
		propMap.put("riaDocTypeName", getRiaDocTypeName());
		propMap.put("helpUrl", getHelpUrl());
		propMap.put("updateAt", getUpdatedAt());
		return propMap;
	}
	public Long getID() {
		return ID;
	}
	public void setID(Long ID) {
		this.ID = ID;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getHelpUrl() {
		return helpUrl;
	}

	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getInitGroups() {
		return initGroups;
	}

	public void setInitGroups(String initGroups) {
		this.initGroups = initGroups;
	}

	public String getRiaDocTypeName() {
		return this.riaDocTypeName;
	}

	public void setRiaDocTypeName(String riaDocTypeName) {
		this.riaDocTypeName = riaDocTypeName;
	}

	public String getRiaUrl() {
		return this.riaUrl;
	}

	public void setRiaUrl(String riaUrl) {
		this.riaUrl = riaUrl;
	}

}
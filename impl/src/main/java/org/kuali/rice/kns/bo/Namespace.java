package org.kuali.rice.kns.bo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="KRNS_NMSPC_T")
@AttributeOverrides({@AttributeOverride(name="code", column=@Column(name="NMSPC_CD")), @AttributeOverride(name="name", column=@Column(name="NM"))})
public class Namespace extends KualiCodeBase {
	private String applicationNamespaceCode;
	public String getNamespaceCode() {
		return code;
	}
	
	public void setNamespaceCode(String namespaceCode) {
		code = namespaceCode;
	}
	
	public String getNamespaceName() {
		return name;
	}
	
	public void setNamespaceName(String namespaceName) {
		name = namespaceName;
	}

	/**
	 * @return the applicationNamespaceCode
	 */
	public String getApplicationNamespaceCode() {
		return this.applicationNamespaceCode;
	}

	/**
	 * @param applicationNamespaceCode the applicationNamespaceCode to set
	 */
	public void setApplicationNamespaceCode(String applicationNamespaceCode) {
		this.applicationNamespaceCode = applicationNamespaceCode;
	}
	
	

}


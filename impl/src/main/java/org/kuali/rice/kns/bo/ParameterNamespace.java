package org.kuali.rice.kns.bo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="KRNS_NMSPC_T")
@AttributeOverrides({@AttributeOverride(name="code", column=@Column(name="NMSPC_CD")), @AttributeOverride(name="name", column=@Column(name="NM"))})
public class ParameterNamespace extends KualiCodeBase {
	
	public String getParameterNamespaceCode() {
		return code;
	}
	
	public void setParameterNamespaceCode(String parameterNamespaceCode) {
		code = parameterNamespaceCode;
	}
	
	public String getParameterNamespaceName() {
		return name;
	}
	
	public void setParameterNamespaceName(String parameterNamespaceName) {
		name = parameterNamespaceName;
	}

}


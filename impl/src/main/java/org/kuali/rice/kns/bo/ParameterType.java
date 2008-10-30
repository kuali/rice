package org.kuali.rice.kns.bo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="KRNS_PARM_TYP_T")
@AttributeOverrides({@AttributeOverride(name="code", column=@Column(name="PARM_TYP_CD")), @AttributeOverride(name="name", column=@Column(name="NM"))})
public class ParameterType extends KualiCodeBase implements Inactivateable {

	public String getParameterTypeCode() {
		return code;
	}

	public void setParameterTypeCode(String parameterTypeCode) {
		this.code = parameterTypeCode;
	}

	public String getParameterTypeName() {
		return name;
	}

	public void setParameterTypeName(String parameterTypeName) {
		this.name = parameterTypeName;
	}

}


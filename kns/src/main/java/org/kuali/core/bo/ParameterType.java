package org.kuali.core.bo;


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

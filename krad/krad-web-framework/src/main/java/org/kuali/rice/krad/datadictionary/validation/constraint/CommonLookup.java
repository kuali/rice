package org.kuali.rice.krad.datadictionary.validation.constraint;

import java.io.Serializable;
import java.util.List;


/**
 * This class is a direct copy of one that was in Kuali Student. Look up constraints are currently not implemented. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
public class CommonLookup implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // unique ID of this lookup
	private String name; // name of this search
	private String desc;
	private String searchTypeId;
	private String resultReturnKey;
    private String searchParamIdKey;
	private List<CommonLookupParam> params;

	public String getSearchTypeId() {
		return searchTypeId;
	}

	public void setSearchTypeId(String searchTypeId) {
		this.searchTypeId = searchTypeId;
	}

	public String getResultReturnKey() {
		return resultReturnKey;
	}

	public void setResultReturnKey(String resultReturnKey) {
		this.resultReturnKey = resultReturnKey;
	}

	public List<CommonLookupParam> getParams() {
		return params;
	}

	public void setParams(List<CommonLookupParam> params) {
		this.params = params;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSearchParamIdKey() {
		return searchParamIdKey;
	}

	public void setSearchParamIdKey(String searchParamIdKey) {
		this.searchParamIdKey = searchParamIdKey;
	}

}

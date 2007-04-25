package edu.sampleu.travel.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObjectBase;


/**
 * FiscalOfficer
 */
public class FiscalOfficer extends PersistableBusinessObjectBase {
	
	private String userName;
	private Long id;
	private List<TravelAccount> accounts;

	public void setUserName(String userId) {
		userName = userId;
	}

	public String getUserName() {
		return userName;
	}

	public final boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof FiscalOfficer)) return false;
        FiscalOfficer f = (FiscalOfficer) o;
        return StringUtils.equals(userName, f.getUserName()) &&
               ObjectUtils.equals(id, f.getId());
	}

	/**
	 * Returns the hashcode of the docHeaderId, which is supposed to be the
	 * primary key for the document
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	/*public int hashCode() {
		return (accountNum == null) ? 0 : accountNum.hashCode();
	}*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public List<TravelAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<TravelAccount> accounts) {
        this.accounts = accounts;
    }

    /*
    public String toString() {
        return "(" + userName + "," + accountNum + ")";
    }*/
    public String toString() {
        return "[FiscalOfficer: id=" + id +
                             ", userName=" + userName +
                             "]";
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap propMap = new LinkedHashMap();
        propMap.put("id", getId());
        propMap.put("userName", getUserName());
        return propMap;
    }
    
}
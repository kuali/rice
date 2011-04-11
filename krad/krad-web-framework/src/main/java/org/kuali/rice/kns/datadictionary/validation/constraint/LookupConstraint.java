package org.kuali.rice.kns.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


/**
 * This class is a direct copy of one that was in Kuali Student. Look up constraints are currently not implemented. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LookupConstraint extends CommonLookup implements Constraint {


	private static final long serialVersionUID = 1L;
//	private String searchTypeId; // id of search type defined in search xml
//	private String resultReturnKey; // key of searchResultColumn to map back to
//									// this field
//	protected List<LookupConstraintParamMapping> lookupParams; // maps fields to
//																// search
//																// params?
//
//	public List<LookupConstraintParamMapping> getLookupParams() {
//		return lookupParams;
//	}
//
//	public void setLookupParams(List<LookupConstraintParamMapping> lookupParams) {
//		this.lookupParams = lookupParams;
//	}
}
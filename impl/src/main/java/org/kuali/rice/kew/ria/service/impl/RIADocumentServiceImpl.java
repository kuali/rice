package org.kuali.rice.kew.ria.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.ria.RIAConstants;
import org.kuali.rice.kew.ria.bo.RIADocTypeMap;
import org.kuali.rice.kew.ria.service.RIADocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class RIADocumentServiceImpl implements RIADocumentService {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RIADocumentServiceImpl.class);

	/**
	 * @see org.kuali.rice.kew.ria.service.RIADocumentService#getRIADocTypeMap(java.lang.String)
	 */
	public RIADocTypeMap getRiaDocTypeMap(String riaDocTypeName) throws WorkflowException {
		
		// check if name is provided
	    if (StringUtils.isEmpty(riaDocTypeName)) {
	    	log.error("No name was provided for RIA Document type.");
	        throw new WorkflowException("No name was provided for RIA Document type.");
	    }
	        	
	    Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(RIAConstants.RIA_DOC_TYPE_NAME, riaDocTypeName);
	               
	    List<RIADocTypeMap> riaDocTypeMaps = (List<RIADocTypeMap>)KNSServiceLocator.getBusinessObjectService().findMatchingOrderBy(RIADocTypeMap.class, criteria, RIAConstants.RIA_UPDATED_AT, false);
	        
	    if (riaDocTypeMaps == null || riaDocTypeMaps.isEmpty()) {
	    	log.error("Document Type for name: " + riaDocTypeName + " does not exist.");
	        throw new WorkflowException("Document Type for name: " + riaDocTypeName + " does not exist.");
	    }
	        
	    RIADocTypeMap riaDocTypeMap = riaDocTypeMaps.get(0);
	        
	    // check if document type exists for provided name
	    if (riaDocTypeMap == null || StringUtils.isEmpty(riaDocTypeMap.getRiaDocTypeName())) {
	    	log.error("Document Type for name: " + riaDocTypeName + " does not exist.");
	    	throw new WorkflowException("Document Type for name: " + riaDocTypeName + " does not exist.");
	    }
	        
	    return riaDocTypeMap;
	}

}
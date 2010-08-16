package org.kuali.rice.kew.ria.service;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.ria.bo.RIADocTypeMap;

/**
 * Service used by ria documents.
 * 
 * @author mpk35
 *
 */
public interface RIADocumentService {
	
	/**
	 * Retrieves RIADocTypeMap based on the given riaDocTypeName.
	 * 
	 * @param riaDocTypeName
	 * @return
	 * @throws WorkflowException
	 */
	public RIADocTypeMap getRiaDocTypeMap(String riaDocTypeName) throws WorkflowException;
}
/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sampleu.travel.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;

import edu.sampleu.travel.bo.FiscalOfficer;
import edu.sampleu.travel.dto.FiscalOfficerInfo;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerServiceImpl implements FiscalOfficerService {

    @Override
    public FiscalOfficerInfo createFiscalOfficer(FiscalOfficerInfo fiscalOfficerInfo) {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    @Override
    public List<FiscalOfficerInfo> lookupFiscalOfficer(Map<String, String> criteria) {
        LookupService service = KNSServiceLocator.getLookupService();
        Collection<FiscalOfficer> temp = service.findCollectionBySearch(FiscalOfficer.class, criteria);
        List<FiscalOfficerInfo> results = new ArrayList<FiscalOfficerInfo>();
        if(temp != null) {
            for(FiscalOfficer fiscalOfficer : temp) {
                results.add(toFiscalOfficerInfo(fiscalOfficer));
            }
        }
        
        return results;
    }

    @Override
    public FiscalOfficerInfo retrieveFiscalOfficer(Long id) {
        BusinessObjectService service = KNSServiceLocator.getBusinessObjectService();
        FiscalOfficer temp = service.findBySinglePrimaryKey(FiscalOfficer.class, id);
        FiscalOfficerInfo result = toFiscalOfficerInfo(temp);
        return result;
    }

    @Override
    public FiscalOfficerInfo updateFiscalOfficer(FiscalOfficerInfo fiscalOfficerInfo) {
        // TODO swgibson - THIS METHOD NEEDS JAVADOCS
        return null;
    }
    
    protected FiscalOfficer toFiscalOfficer(FiscalOfficerInfo fiscalOfficerInfo) {
        FiscalOfficer fiscalOfficer = new FiscalOfficer();
        
        fiscalOfficer.setFirstName(fiscalOfficerInfo.getFirstName());
        fiscalOfficer.setId(fiscalOfficerInfo.getId());
        fiscalOfficer.setUserName(fiscalOfficerInfo.getUserName());
        fiscalOfficer.setObjectId(fiscalOfficerInfo.getObjectId());
        fiscalOfficer.setVersionNumber(fiscalOfficerInfo.getVersionNumber());
        
        return fiscalOfficer;
    }
    
    protected FiscalOfficerInfo toFiscalOfficerInfo(FiscalOfficer fiscalOfficer) {
        FiscalOfficerInfo fiscalOfficerInfo = null;
        
        if(fiscalOfficer != null) {
            fiscalOfficerInfo = new FiscalOfficerInfo();
            
            fiscalOfficerInfo.setFirstName(fiscalOfficer.getFirstName());
            fiscalOfficerInfo.setId(fiscalOfficer.getId());
            fiscalOfficerInfo.setUserName(fiscalOfficer.getUserName());
            fiscalOfficerInfo.setObjectId(fiscalOfficer.getObjectId());
            fiscalOfficerInfo.setVersionNumber(fiscalOfficer.getVersionNumber());
        }
        
        return fiscalOfficerInfo;
    }

}

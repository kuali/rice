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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.springframework.beans.BeanUtils;

import edu.sampleu.travel.bo.FiscalOfficer;
import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.dto.FiscalOfficerInfo;
import edu.sampleu.travel.dto.TravelAccountInfo;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerServiceImpl implements FiscalOfficerService {

    protected BusinessObjectService businessObjectService;
    protected LookupService lookupService;
    
    @Override
    public FiscalOfficerInfo createFiscalOfficer(FiscalOfficerInfo fiscalOfficerInfo) {
        getBusinessObjectService().save(toFiscalOfficer(fiscalOfficerInfo));
        
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("id", fiscalOfficerInfo.getId());
        FiscalOfficer fiscalOfficer = getBusinessObjectService().findByPrimaryKey(FiscalOfficer.class, criteria);
        return toFiscalOfficerInfo(fiscalOfficer);
    }

    @Override
    public List<FiscalOfficerInfo> lookupFiscalOfficer(Map<String, String> criteria) {
        @SuppressWarnings("unchecked")
        Collection<FiscalOfficer> temp = getLookupService().findCollectionBySearch(FiscalOfficer.class, criteria);
        
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
        FiscalOfficer temp = getBusinessObjectService().findBySinglePrimaryKey(FiscalOfficer.class, id);
        FiscalOfficerInfo result = toFiscalOfficerInfo(temp);
        return result;
    }

    @Override
    public FiscalOfficerInfo updateFiscalOfficer(FiscalOfficerInfo fiscalOfficerInfo) {
        FiscalOfficer fiscalOfficer = getBusinessObjectService().findBySinglePrimaryKey(FiscalOfficer.class, fiscalOfficerInfo.getId());
        
        if(fiscalOfficer != null) {
            getBusinessObjectService().save(toFiscalOfficerUpdate(fiscalOfficerInfo, fiscalOfficer));
            fiscalOfficer = getBusinessObjectService().findBySinglePrimaryKey(FiscalOfficer.class, fiscalOfficerInfo.getId());
        }
        
        return toFiscalOfficerInfo(fiscalOfficer);
    }
    
    /**
     * This method only copies fields from the dto to the ojb bean that should be copied
     * on an update call.
     * 
     * @param fiscalOfficerInfo dto to convert to ojb bean
     * @param fiscalOfficer target ojb bean to update from info
     * @return
     */
    protected FiscalOfficer toFiscalOfficerUpdate(FiscalOfficerInfo fiscalOfficerInfo,
            FiscalOfficer fiscalOfficer) {
        
        fiscalOfficer.setFirstName(fiscalOfficerInfo.getFirstName());
        fiscalOfficer.setId(fiscalOfficerInfo.getId());
        fiscalOfficer.setUserName(fiscalOfficerInfo.getUserName());
        fiscalOfficer.setObjectId(fiscalOfficerInfo.getObjectId());
        fiscalOfficer.setVersionNumber(fiscalOfficerInfo.getVersionNumber());
        
        return fiscalOfficer;
    }
    
    protected FiscalOfficer toFiscalOfficer(FiscalOfficerInfo fiscalOfficerInfo) {
        FiscalOfficer fiscalOfficer = new FiscalOfficer();
        
        fiscalOfficer.setFirstName(fiscalOfficerInfo.getFirstName());
        fiscalOfficer.setId(fiscalOfficerInfo.getId());
        fiscalOfficer.setUserName(fiscalOfficerInfo.getUserName());
        fiscalOfficer.setObjectId(fiscalOfficerInfo.getObjectId());
        fiscalOfficer.setVersionNumber(fiscalOfficerInfo.getVersionNumber());
        
        if(fiscalOfficerInfo.getAccounts() != null) {
            for(TravelAccountInfo accountInfo : fiscalOfficerInfo.getAccounts()) {
                TravelAccount travelAccount = new TravelAccount();
                BeanUtils.copyProperties(accountInfo, travelAccount);
                fiscalOfficer.getAccounts().add(travelAccount);
            }
        }
        
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
            
            if(fiscalOfficer.getAccounts() != null) {
                List<TravelAccountInfo> accountInfoList = new ArrayList<TravelAccountInfo>();
                
                for(TravelAccount travelAccount : fiscalOfficer.getAccounts()) {
                    accountInfoList.add(toTravelAccountInfo(travelAccount));
                }
                
                fiscalOfficerInfo.setAccounts(accountInfoList);
            }
        }
        
        return fiscalOfficerInfo;
    }
    
    protected TravelAccountInfo toTravelAccountInfo(TravelAccount travelAccount) {
        TravelAccountInfo travelAccountInfo = new TravelAccountInfo();
        
        travelAccountInfo.setCreateDate(travelAccount.getCreateDate());
        travelAccountInfo.setName(travelAccount.getName());
        travelAccountInfo.setNumber(travelAccount.getNumber());
        travelAccountInfo.setObjectId(travelAccount.getObjectId());
        travelAccountInfo.setVersionNumber(travelAccount.getVersionNumber());
        
        return travelAccountInfo;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if(businessObjectService == null) {
            businessObjectService = KNSServiceLocator.getBusinessObjectService();
        }
        return this.businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected LookupService getLookupService() {
        if(lookupService == null) {
            lookupService = KNSServiceLocator.getLookupService();
        }
        return this.lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

}

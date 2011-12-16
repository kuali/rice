/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.springframework.beans.BeanUtils;

import edu.sampleu.travel.bo.FiscalOfficer;
import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.dto.FiscalOfficerInfo;
import edu.sampleu.travel.dto.TravelAccountInfo;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerTravelAccountServiceImpl implements FiscalOfficerService, TravelAccountService {

    protected static String[] FOI_SKIP = { "accounts" };
    
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

    @Override
    public TravelAccountInfo retrieveTravelAccount(String number) {
        TravelAccount temp = getBusinessObjectService().findBySinglePrimaryKey(TravelAccount.class, number);
        TravelAccountInfo result = toTravelAccountInfo(temp);

        return result;
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
        
        BeanUtils.copyProperties(fiscalOfficerInfo, fiscalOfficer, FOI_SKIP);
        
        List<TravelAccountInfo> newAccounts = new ArrayList<TravelAccountInfo>();
        Map<String, TravelAccountInfo> accountInfoMap = new HashMap<String, TravelAccountInfo>();
        
        // create a map of accounts that are being edited and list of accounts to be added
        for(TravelAccountInfo accountInfo : fiscalOfficerInfo.getAccounts()) {
            if(accountInfo.getVersionNumber() == null) {
                newAccounts.add(accountInfo);
            }
            else {
                accountInfoMap.put(accountInfo.getNumber(), accountInfo);
            }
        }
        
        // iterator the accounts from the db, updating or removing if necessary
        Iterator<TravelAccount> iterator = fiscalOfficer.getAccounts().iterator();
        while(iterator.hasNext()) {
            TravelAccount account = iterator.next();
            TravelAccountInfo accountInfo = accountInfoMap.get(account.getNumber());
            
            if(accountInfo == null) {
                // this really isnt the best example data set, so just orphan
                // instead of actually deleting
                //iterator.remove();
                
                account.setFoId(null);
                account.setFiscalOfficer(null);
            }
            else {
                BeanUtils.copyProperties(accountInfo, account);
            }
        }
        
        // add the new accounts to the list
        for(TravelAccountInfo accountInfo : newAccounts) {
            fiscalOfficer.getAccounts().add(toTravelAccount(accountInfo));
        }
        
        return fiscalOfficer;
    }
    
    protected FiscalOfficer toFiscalOfficer(FiscalOfficerInfo fiscalOfficerInfo) {
        FiscalOfficer fiscalOfficer = new FiscalOfficer();
        
        BeanUtils.copyProperties(fiscalOfficerInfo, fiscalOfficer, FOI_SKIP);
        
        if(fiscalOfficerInfo.getAccounts() != null) {
            for(TravelAccountInfo accountInfo : fiscalOfficerInfo.getAccounts()) {
                fiscalOfficer.getAccounts().add(toTravelAccount(accountInfo));
            }
        }
       
        return fiscalOfficer;
    }
    
    protected FiscalOfficerInfo toFiscalOfficerInfo(FiscalOfficer fiscalOfficer) {
        FiscalOfficerInfo fiscalOfficerInfo = null;
        
        if(fiscalOfficer != null) {
            fiscalOfficerInfo = new FiscalOfficerInfo();
            
            BeanUtils.copyProperties(fiscalOfficer, fiscalOfficerInfo, FOI_SKIP);
            
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
        
        BeanUtils.copyProperties(travelAccount, travelAccountInfo);
        
        return travelAccountInfo;
    }
    
    protected TravelAccount toTravelAccount(TravelAccountInfo travelAccountInfo) {
        TravelAccount travelAccount = new TravelAccount();
        
        BeanUtils.copyProperties(travelAccountInfo, travelAccount);
        
        return travelAccount;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if(businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return this.businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected LookupService getLookupService() {
        if(lookupService == null) {
            lookupService = KRADServiceLocatorWeb.getLookupService();
        }
        return this.lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

}

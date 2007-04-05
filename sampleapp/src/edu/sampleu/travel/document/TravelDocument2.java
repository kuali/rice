/*
 * Copyright 2007 The Kuali Foundation.
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
package edu.sampleu.travel.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.document.TransactionalDocumentBase;

import edu.sampleu.travel.bo.TravelAccount;



public class TravelDocument2 extends TransactionalDocumentBase {
    
    private String traveler;
    private String origin;
    private String destination;
    private String requestType;
       
    private List<TravelAccount> travelAccounts;
    
    public TravelDocument2() {
        travelAccounts = new ArrayList<TravelAccount>();
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> meMap = new LinkedHashMap<String, String>();
        meMap.put("traveler", getTraveler());
        meMap.put("origin", getOrigin());
        meMap.put("destination", getDestination());
        return meMap;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTraveler() {
        return traveler;
    }

    public void setTraveler(String traveler) {
        this.traveler = traveler;
    }

    public List<TravelAccount> getTravelAccounts() {
        return travelAccounts;
    }

    public void setTravelAccounts(List<TravelAccount> travelAccounts) {
        this.travelAccounts = travelAccounts;
    }       

    public TravelAccount getTravelAccount(int index) {
        while(travelAccounts.size() - 1 < index) {
            travelAccounts.add(new TravelAccount());
        }
        return travelAccounts.get(index);
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
}

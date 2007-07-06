// Created on Oct 18, 2006

package edu.sampleu.travel.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;



public class TravelAccount extends PersistableBusinessObjectBase {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7739303391609093875L;
	private String number;
    private String name;
    private Long foId;
    private FiscalOfficer fiscalOfficer;    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public FiscalOfficer getFiscalOfficer() {
        return fiscalOfficer;
    }

    public void setFiscalOfficer(FiscalOfficer fiscalOfficer) {
        this.fiscalOfficer = fiscalOfficer;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap propMap = new LinkedHashMap();
        propMap.put("number", getNumber());
        propMap.put("name", getName());
        return propMap;
    }

    public Long getFoId() {
        return foId;
    }

    public void setFoId(Long foId) {
        this.foId = foId;
    }
 
}
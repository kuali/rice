package org.kuali.rice.kns.datadictionary.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ValidatorFactory provides a mechanism to 
 *  
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */

public class ValidatorFactory {

	private Map<String,Validator> customValidators = null; 
	private DefaultValidatorImpl defaultValidator;
	
	private List<Validator> validatorList = new ArrayList<Validator>();
	
	public ValidatorFactory(){
	}
	
	public synchronized void initializeMap(){
	    
	    if(null == customValidators) {
	        customValidators = new HashMap<String, Validator>();
	        for(Validator validator: validatorList){
	            String validatorName = validator.getClass().getName();
	            customValidators.put(validatorName, validator);
	        }
	        
	    }
	}
	
	
	public Validator getValidator(String customValidator) {
	
	    System.out.println("Retrieving validatior:" + customValidator);
	    if(null == customValidators) {
	        initializeMap();
	    }
	    
	    Validator v = customValidators.get(customValidator); 
	    
	    if(v != null && v instanceof BaseAbstractValidator) {
	        BaseAbstractValidator bv = (BaseAbstractValidator)v;
	        bv.setValidatorFactory(this);
	        return bv;
	    } else {
	       return v;
	    }
	}
	
	public Validator getValidator(){
		if(defaultValidator==null){
		    defaultValidator = new DefaultValidatorImpl();
		}
		
//		defaultValidator.setValidatorFactory(this);
		return defaultValidator;
	}
	
	public DefaultValidatorImpl getDefaultValidator() {
		return defaultValidator;
	}

	public void setDefaultValidator(DefaultValidatorImpl defaultValidator) {
		this.defaultValidator = defaultValidator;
	}

    public List<Validator> getValidatorList() {
        return validatorList;
    }

    public void setValidatorList(List<Validator> validatorList) {
        this.validatorList = validatorList;
    }
}

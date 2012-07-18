/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation.constraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


/**
 * A class that implements the required accessor for label keys. This provides a convenient base class
 * from which other constraints can be derived.
 *
 * Only BaseConstraints can have state validation.
 * 
 * This class is a direct copy of one that was in Kuali Student. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseConstraint implements Constraint {
    @XmlElement
    protected String labelKey; 
    @XmlElement
    protected Boolean applyClientSide;
    
    protected List<String> validationMessageParams;
    protected List<String> states;
    protected List<? extends BaseConstraint> constraintStateOverrides;
    
    public BaseConstraint(){
    	applyClientSide = Boolean.valueOf(true);
    }
    
	/**
	 * LabelKey should be a single word key.  This key is used to find a message to use for this
	 * constraint from available messages.  The key is also used for defining/retrieving validation method
	 * names when applicable for ValidCharactersContraints.
	 * 
	 * If a comma separated list of keys is used, a message will be generated that is a comma separated list of
	 * the messages retrieved for each key.
	 * 
	 * @see ValidCharactersConstraint
	 * 
	 * @return
	 */
	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	/**
	 * If this is true, the constraint should be applied on the client side when the user interacts with
	 * a field - if this constraint can be interpreted for client side use. Default is true.
	 * @return the applyClientSide
	 */
	public Boolean getApplyClientSide() {
		return this.applyClientSide;
	}

	/**
	 * @param applyClientSide the applyClientSide to set
	 */
	public void setApplyClientSide(Boolean applyClientSide) {
		this.applyClientSide = applyClientSide;
	}
	

    /**
     * Parameters to be used in the string retrieved by this constraint's labelKey, ordered by number of
     * the param
     * @return the validationMessageParams
     */
    public List<String> getValidationMessageParams() {
        return this.validationMessageParams;
    }
    
    /**
     * Parameters to be used in the string retrieved by this constraint's labelKey, ordered by number of
     * the param
     * @return the validationMessageParams
     */
    public String[] getValidationMessageParamsArray() {
        if(this.getValidationMessageParams() != null){
            return this.getValidationMessageParams().toArray(new String[this.getValidationMessageParams().size()]);
        }
        else{
            return null;
        }

    }

    /**
     * @param validationMessageParams the validationMessageParams to set
     */
    public void setValidationMessageParams(List<String> validationMessageParams) {
        this.validationMessageParams = validationMessageParams;
    }

    /**
     * A list of states to apply this constraint for, this will effect when a constraint
     * is applied.
     *
     * <p>Each state this constraint is applied for needs to be declared with few additional options:
     * <ul>
     *     <li>if NO states are defined for this constraint, this constraint is applied for ALL states</li>
     *     <li>if a state is defined with a + symbol, example "state+", then this constraint will be applied for that state
     *     and ALL following states</li>
     *     <li>if a state is defined as a range with ">", example "state1>state6", then this constraint will be applied for all
     *     states from state1 to state6 </li>
     * </ul>
     * These can be mixed and matched, as appropriate, though states using a + symbol should always be the last
     * item of a list (as they imply this state and everything else after).</p>
     *
     * <p>Example state list may be: ["state1", "state3>state5", "state6+"].  In this example, note that this constraint is
     * never applied to "state2" (assuming these example states represent a state order by number)</p>
     *
     * @return the states to apply the constraint on, an empty list if the constraint is applied for all states
     */
    public List<String> getStates() {
        if(states == null){
            states = new ArrayList<String>();
        }
        return states;
    }

    /**
     * Set the states for this contraint to be applied on
     * @param states
     */
    public void setStates(List<String> states) {
        this.states = states;
    }

    /**
     * Get the list of constraintStateOverrides which represent constraints that will replace THIS constraint
     * when their state is matched during validation.
     * Because of this, constraints added to this list MUST have their states defined.
     *
     * <p>ConstraintStateOverrides always take precedence over this
     * constraint if they apply to the state being evaluated during validation.  These settings have no effect if
     * there is no stateMapping represented on the entry/view being evaluated.
     * </p>
     * @return List of constraint overrides for this constraint
     */
    public List<? extends BaseConstraint> getConstraintStateOverrides() {
        return constraintStateOverrides;
    }

    /**
     * Set the constraintStateOverrides to be used when a state is matched during validation
     *
     * @param constraintStateOverrides
     */
    public void setConstraintStateOverrides(List<? extends BaseConstraint> constraintStateOverrides) {
        for(BaseConstraint bc: constraintStateOverrides){
            if(!bc.getClass().equals(this.getClass())){
                List<Class<?>> superClasses = new ArrayList<Class<?>>();
                Class<?> o = bc.getClass();
                while (o != null && !o.equals(BaseConstraint.class)) {
                  superClasses.add(o);
                  o = o.getSuperclass();
                }
                
                List<Class<?>> thisSuperClasses = new ArrayList<Class<?>>();
                o = this.getClass();
                while (o != null && !o.equals(BaseConstraint.class)) {
                  thisSuperClasses.add(o);
                  o = o.getSuperclass();
                }
                superClasses.retainAll(thisSuperClasses);

                if(superClasses.isEmpty()){
                    throw new RuntimeException("Constraint State Override is not a correct type, type should be " +
                            this.getClass().toString() + " (or child/parent of that constraint type)");
                }
            }
            if(bc.getStates().isEmpty()){
                throw new RuntimeException("Constraint State Overrides MUST declare the states they apply to.  No states"
                        + "were declared.");
            }
        }
        this.constraintStateOverrides = constraintStateOverrides;
    }
}

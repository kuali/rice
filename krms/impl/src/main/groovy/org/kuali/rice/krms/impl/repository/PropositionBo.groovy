package org.kuali.rice.krms.impl.repository


import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinitionContract;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator
import org.kuali.rice.krms.api.repository.proposition.PropositionType
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;


public class PropositionBo extends PersistableBusinessObjectBase implements PropositionDefinitionContract {

	def String id
	def String description
    def String ruleId
	def String typeId
	def String propositionTypeCode
	
	def List<PropositionParameterBo> parameters
	
	// Compound parameter related properties
	def String compoundOpCode
	def List<PropositionBo> compoundComponents

    // parameter display string (for tree display)
    def String parameterDisplayString;

    private void setupParameterDisplayString(){
        if (PropositionType.SIMPLE.getCode().equalsIgnoreCase(getPropositionTypeCode())){
            // Simple Propositions should have 3 parameters ordered in reverse polish notation.
            // TODO: enhance to get term names for term type parameters.
            List<PropositionParameterBo> parameters = getParameters();
            if (parameters != null && parameters.size() == 3){
                setParameterDisplayString(getParamValue(parameters.get(0))
                        + " " + getParamValue(parameters.get(2))
                        + " " + getParamValue(parameters.get(1)));
            } else {
                // should not happen
            }
        }
    }

    private String getParamValue(PropositionParameterBo prop){
        if (PropositionParameterType.TERM.getCode().equalsIgnoreCase(prop.getParameterType())){
            String termName = "";
            String termId = prop.getValue();
            if (termId != null && termId.length()>0){
                //TODO: use termBoService
                TermBo term = getBoService().findBySinglePrimaryKey(TermBo.class,termId);
                termName = term.getSpecification().getName();
            }
            return termName;
        } else {
            return prop.getValue();
        }
    }
    /**
     * @return the parameterDisplayString
     */
    public String getParameterDisplayString() {
        if (parameterDisplayString == null){
            setupParameterDisplayString()
        }
        return this.parameterDisplayString;
    }

    /**
     * @param parameterDisplayString the parameterDisplayString to set
     */
    public void setParameterDisplayString(String parameterDisplayString) {
        this.parameterDisplayString = parameterDisplayString;
    }

    public BusinessObjectService getBoService() {
        return KRADServiceLocator.getBusinessObjectService();
    }


	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static PropositionDefinition to(PropositionBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.proposition.PropositionDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static PropositionBo from(PropositionDefinition im) {
	   if (im == null) { return null }

	   PropositionBo bo = new PropositionBo()
	   bo.id = im.id
	   bo.description = im.description

       //bo.ruleId = im.ruleId
       setRuleIdRecursive(im.ruleId, bo)
       
	   bo.typeId = im.typeId
	   bo.propositionTypeCode = im.propositionTypeCode
	   bo.parameters = new ArrayList<PropositionParameterBo>()
	   for ( parm in im.parameters){
		   bo.parameters.add (PropositionParameterBo.from(parm))
	   }
	   bo.compoundOpCode = im.compoundOpCode
	   bo.compoundComponents = new ArrayList<PropositionBo>()
	   for (prop in im.compoundComponents){
		   bo.compoundComponents.add (PropositionBo.from(prop))
	   }
	   bo.versionNumber = im.versionNumber
	   return bo
   }
   
   private static void setRuleIdRecursive(String ruleId, PropositionBo prop) {
       prop.ruleId = ruleId;
       if (prop.compoundComponents != null) for (PropositionBo child : prop.compoundComponents) if (child != null) {
           setRuleIdRecursive(ruleId, child);
       }
   }
 
} 
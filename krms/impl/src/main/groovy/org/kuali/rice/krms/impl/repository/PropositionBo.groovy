package org.kuali.rice.krms.impl.repository


import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinitionContract;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator
import org.kuali.rice.krms.api.repository.proposition.PropositionType
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType
import org.kuali.rice.krad.service.SequenceAccessorService
import org.kuali.rice.krms.api.repository.LogicalOperator;


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
    def boolean editMode = false;
    private SequenceAccessorService sequenceAccessorService;  //todo move to wrapper object

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

    public boolean getEditMode(){
        return this.editMode;
    }

    public void setEditMode(boolean editMode){
        this.editMode = editMode;
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
 
    /**
       * This method creates a partially populated Simple PropositionBo with
       * three parameters:  a term type paramter (value not assigned)
       *                    a operation parameter
       *                    a constant parameter (value set to empty string)
       * The returned PropositionBo has an generatedId. The type code, ruleId and TypeId properties are assigned the
       * same value as the sibling param passed in.
       * Each PropositionParameter has the id generated, and type, sequenceNumber,
       * propId default values set. The value is set to "".
       * @param sibling -
       * @param pType
       * @return  a PropositionBo partially populated.
       */
  public static PropositionBo createSimplePropositionBoStub(PropositionBo sibling, String pType){
      // create a simple proposition Bo
      PropositionBo prop = null;
      if (PropositionType.SIMPLE.getCode().equalsIgnoreCase(pType)){
          prop = new PropositionBo();
          prop.setId(getNewPropId());
          prop.setPropositionTypeCode(pType);
          prop.setRuleId(sibling.getRuleId());
          prop.setTypeId(sibling.getTypeId());
          prop.setEditMode(true);

          // create blank proposition parameters
          PropositionParameterBo pTerm = new PropositionParameterBo();
          pTerm.setId(getNewPropParameterId());
          pTerm.setParameterType("T");
          pTerm.setPropId(prop.getId());
          pTerm.setSequenceNumber(new Integer("0"));
          pTerm.setVersionNumber(new Long(1));
          pTerm.setValue("");

          // create blank proposition parameters
          PropositionParameterBo pOp = new PropositionParameterBo();
          pOp.setId(getNewPropParameterId());
          pOp.setParameterType("F");
          pOp.setPropId(prop.getId());
          pOp.setSequenceNumber(new Integer("2"));
          pOp.setVersionNumber(new Long(1));

          // create blank proposition parameters
          PropositionParameterBo pConst = new PropositionParameterBo();
          pConst.setId(getNewPropParameterId());
          pConst.setParameterType("C");
          pConst.setPropId(prop.getId());
          pConst.setSequenceNumber(new Integer("1"));
          pConst.setVersionNumber(new Long(1));
          pConst.setValue("");

          List<PropositionParameterBo> paramList = Arrays.asList(pTerm, pConst, pOp);
          prop.setParameters(paramList);
      }
      return prop;
  }

    public static PropositionBo createCompoundPropositionBoStub(PropositionBo simple){
        // create a simple proposition Bo
        PropositionBo prop = new PropositionBo();
        prop.setId(getNewPropId());
        prop.setPropositionTypeCode(PropositionType.COMPOUND.code);
        prop.setRuleId(simple.getRuleId());
        prop.setTypeId(simple.getTypeId());
        prop.setCompoundOpCode(LogicalOperator.AND.code);  // default to and
        prop.setDescription("");
        prop.setEditMode(true);

        PropositionBo newProp = createSimplePropositionBoStub(simple, PropositionType.SIMPLE.code)
        List <PropositionBo> components = Arrays.asList(simple, newProp);
        prop.setCompoundComponents(components);
        return prop;
    }

    private static String getNewPropId(){
        SequenceAccessorService sas = KRADServiceLocator.getSequenceAccessorService();
        Long id = sas.getNextAvailableSequenceNumber("KRMS_PROP_S",
                PropositionBo.class);
        return id.toString();
    }
    private static String getNewPropParameterId(){
        SequenceAccessorService sas = KRADServiceLocator.getSequenceAccessorService();
        Long id = sas.getNextAvailableSequenceNumber("KRMS_PROP_PARM_S",
                PropositionParameterBo.class);
        return id.toString();
    }
}
package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinitionContract
import java.util.Map.Entry
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;


public class TermBo extends PersistableBusinessObjectBase implements TermDefinitionContract {

	def String id
	def String specificationId
    def String description

    // new-ing up an empty one allows the TermBo lookup to work on fields in the term specification:
	def TermSpecificationBo specification = new TermSpecificationBo()

	def List<TermParameterBo> parameters

	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static TermDefinition to(TermBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.term.TermDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static TermBo from(TermDefinition im) {
	   if (im == null) { return null }

	   TermBo bo = new TermBo()
	   bo.id = im.id
	   bo.specificationId = im.specification.id
       bo.description = im.description
	   bo.specification = TermSpecificationBo.from(im.specification)
	   bo.parameters = new ArrayList<TermParameterBo>()
	   for (parm in im.parameters){
		   bo.parameters.add ( TermParameterBo.from(parm) )
	   }
	   bo.versionNumber = im.versionNumber
	   return bo
   }

   public TermSpecificationBo getSpecification(){
	   return specification
   }

   public List<TermParameterBo> getParameters(){
	   return parameters
   }

   public Map<String, String> getParametersMap() {
       HashMap<String, String> results = new HashMap<String, String>()
       if (parameters != null) for (TermParameterBo param : parameters) {
           results.put(param.name, param.value);
       }
       return results;
   }

   public void setParametersMap(HashMap<String, String> paramsMap) {
       parameters.clear();
       if (paramsMap != null) {
           for (Entry<String, String> paramEntry : paramsMap.entrySet()) {
               TermParameterDefinition termDef = TermParameterDefinition.Builder.create(null, id, paramEntry.key, paramEntry.value).build();
               parameters.add(TermParameterBo.from(termDef));
           }
       }
   }

} 
package org.kuali.rice.krms.impl.repository

import java.util.Map.Entry

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinitionContract;


public class ContextBo extends PersistableBusinessObjectBase implements ContextDefinitionContract {

	String id
	String name
	String namespace
	String typeId
    String description

	List<AgendaBo> agendas = new ArrayList<AgendaBo>()

	List<ContextAttributeBo> attributeBos = new ArrayList<ContextAttributeBo>()
	List<ContextValidEventBo> validEvents = new ArrayList<ContextValidEventBo>()
	List<ContextValidActionBo> validActions = new ArrayList<ContextValidActionBo>()

	Long versionNumber


	@Override
	public List<AgendaBo> getAgendas(){
		return agendas
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attributes = new HashMap<String, String>();
		for (attr in attributeBos) {
			attributes.put( attr.attributeDefinition.name, attr.value )
		}
		return attributes;
	}

	/**
	 * Converts a mutable bo to it's immutable counterpart
	 * @param bo the mutable business object
	 * @return the immutable object
	 */
	static ContextDefinition to(ContextBo bo) {
		if (bo == null) { return null }
		return org.kuali.rice.krms.api.repository.context.ContextDefinition.Builder.create(bo).build()
	}

	/**
	 * Converts a immutable object to it's mutable bo counterpart
	 * @param im immutable object
	 * @return the mutable bo
	 */
	static ContextBo from(ContextDefinition im) {
		if (im == null) { return null }

		ContextBo bo = new ContextBo()
		bo.id = im.id
		bo.namespace = im.namespace
		bo.name = im.name
		bo.typeId = im.typeId
        bo.description = im.description
		bo.agendas = new ArrayList<AgendaBo>()
		for (agenda in im.agendas){
			bo.agendas.add( AgendaBo.from(agenda) )
		}
		// build the list of agenda attribute BOs
		List<ContextAttributeBo> attrs = new ArrayList<ContextAttributeBo>();

		// for each converted pair, build an AgendaAttributeBo and add it to the list
		ContextAttributeBo attributeBo;
		for (Entry<String,String> entry  : im.getAttributes().entrySet()){
			KrmsAttributeDefinitionBo attrDefBo = KrmsRepositoryServiceLocator
			.getKrmsAttributeDefinitionService()
			.getKrmsAttributeBo(entry.getKey(), im.getNamespace());
			attributeBo = new ContextAttributeBo();
			attributeBo.setContextId( im.getId() );
			attributeBo.setAttributeDefinitionId( attrDefBo.getId() );
			attributeBo.setValue( entry.getValue() );
			attributeBo.setAttributeDefinition( attrDefBo );
			attrs.add( attributeBo );
		}
		bo.setAttributeBos(attrs);

		bo.versionNumber = im.versionNumber
		return bo
	}
} 
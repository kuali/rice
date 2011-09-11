package org.kuali.rice.kew.api.repository.type;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

import java.util.List;

public interface KewTypeDefinitionContract extends Identifiable, Inactivatable,
		Versioned {

	/**
	 * This is the name for the KEWType
	 * 
	 * <p>
	 * It is a name of a KEW type.
	 * </p>
	 * 
	 * @return name for KEW type.
	 */
	public String getName();

	/**
	 * This is the namespace code.
	 * 
	 * <p>
	 * It provides scope of the KEW type.
	 * </p>
	 * 
	 * @return the namespace code of the KEW type.
	 */
	public String getNamespace();

	/**
	 * This is the name of the KEW KewType service
	 * 
	 * @return the service name of the KEW type
	 */
	public String getServiceName();

	/**
	 * This method returns a list of attributes associated with the KewType
	 * 
	 * @return a list of KewTypeAttribute objects.
	 */
	public List<? extends KewTypeAttributeContract> getAttributes();
}

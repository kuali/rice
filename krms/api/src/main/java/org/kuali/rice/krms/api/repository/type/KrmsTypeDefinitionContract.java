package org.kuali.rice.krms.api.repository.type;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

import java.util.List;

public interface KrmsTypeDefinitionContract extends Identifiable, Inactivatable, Versioned {

	/**
	 * This is the name for the KRMSType 
	 *
	 * <p>
	 * It is a name of a KRMS type.
	 * </p>
	 * @return name for KRMS type.
	 */
	public String getName();

	/**
	 * This is the namespace code. 
	 *
	 * <p>
	 * It provides scope of the KRMS type.
	 * </p>
	 * @return the namespace code of the KRMS type.
	 */
	public String getNamespace();

	/**
	 * This is the name of the KRMS KrmsType service
	 * 
	 * @return the service name of the KRMS type
	 */
	public String getServiceName();

	/**
	 * This method returns a list of attributes associated with the 
	 * KrmsType
	 * 
	 * @return a list of KrmsTypeAttribute objects.
	 */
	public List<? extends KrmsTypeAttributeContract> getAttributes();
}

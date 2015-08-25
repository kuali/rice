/**
 * Copyright 2005-2015 The Kuali Foundation
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
package edu.arizona.kim.ldap;

import org.kuali.rice.kim.util.Constants;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import edu.arizona.kim.eds.UaEdsConstants;

/**
 * Base abstract class for mapping abstract data transfer objects from Kuali
 * Rice into Spring LDAP.
 * 
 */
public abstract class UaBaseMapper<T> extends AbstractContextMapper {

	private Constants constants;
	private UaEdsConstants edsConstants;

	@Override
	public Object doMapFromContext(DirContextOperations context) {
		return mapDtoFromContext(context);
	}

	abstract T mapDtoFromContext(DirContextOperations context);

	public final Constants getConstants() {
		return this.constants;
	}

	public final void setConstants(final Constants argConstants) {
		this.constants = argConstants;
	}

	public UaEdsConstants getEdsConstants() {
		return edsConstants;
	}

	public void setEdsConstants(UaEdsConstants edsConstants) {
		this.edsConstants = edsConstants;
	}

}

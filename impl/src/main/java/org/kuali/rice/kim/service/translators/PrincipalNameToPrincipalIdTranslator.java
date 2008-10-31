/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service.translators;

import java.util.ArrayList;

import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimAttributesTranslatorBase;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * This is a description of what this class does - kellerj don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class PrincipalNameToPrincipalIdTranslator extends KimAttributesTranslatorBase {

	private static final String PRINCIPAL_NAME = "principalName";
	private static final String PRINCIPAL_ID = "principalId";
	protected IdentityManagementService identityManagementService;
	
	/**
	 * 
	 */
	public PrincipalNameToPrincipalIdTranslator() {
		ArrayList<String> attribs = new ArrayList<String>( 1 );
		attribs.add( PRINCIPAL_NAME );
		setSupportedAttributeNames( attribs );
		attribs = new ArrayList<String>( 1 );
		attribs.add( PRINCIPAL_ID );
		setResultAttributeNames( attribs );
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.types.KimAttributesTranslator#translateAttributes(org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public AttributeSet translateAttributes(AttributeSet attributes) {
		AttributeSet results = new AttributeSet( attributes );
		if ( !results.containsKey( PRINCIPAL_ID ) ) { 
			KimPrincipal p = getIdentityManagementService().getPrincipalByPrincipalName( attributes.get( PRINCIPAL_NAME ) );
			if ( p != null ) {
				results.put( PRINCIPAL_ID, p.getPrincipalId() );
			}
		}
		return results;
	}

	public IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}
}

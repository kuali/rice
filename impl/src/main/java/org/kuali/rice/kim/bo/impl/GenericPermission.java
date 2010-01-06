/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_PERM_T")
public class GenericPermission extends PersistableBusinessObjectBase {
//	private static final Logger LOG = Logger.getLogger(GenericPermission.class);	
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="PERM_ID")
	protected String permissionId;
	protected String namespaceCode;
	protected String name;
	protected String description;
	protected boolean active;
	protected String templateId;
	protected String detailValues;
	protected AttributeSet details;
	
	/**
	 * This constructs a ...
	 * 
	 */
	public GenericPermission() {
	}
	
	public GenericPermission( KimPermission perm ) {
		loadFromKimPermission( perm );
	}
	public void loadFromKimPermission( KimPermission perm ) {
		setPermissionId( perm.getPermissionId() );
		setNamespaceCode( perm.getNamespaceCode() );
		setName( perm.getName() );
		setTemplateId( perm.getTemplateId() );
		setDescription( perm.getDescription() );
		setActive( perm.isActive() );
		setDetails( perm.getDetails() );
	}
	
	public String getDetailValues() {
		/*StringBuffer sb = new StringBuffer();
		if ( details != null ) {
			Iterator<String> keyIter = details.keySet().iterator();
			while ( keyIter.hasNext() ) {
				String key = keyIter.next();
				sb.append( key ).append( '=' ).append( details.get( key ) );
				if ( keyIter.hasNext() ) {
					sb.append( '\n' );
				}
			}
		}
		return sb.toString();*/
		return detailValues;
	}
	
	public void setDetailValues( String detailValues ) {
		this.detailValues = detailValues;
		String detailValuesTemp = detailValues;
		AttributeSet details = new AttributeSet();
		if ( detailValuesTemp != null ) {
			// ensure that all line delimiters are single linefeeds
			detailValuesTemp = detailValuesTemp.replace( "\r\n", "\n" );
			detailValuesTemp = detailValuesTemp.replace( '\r', '\n' );
			if ( StringUtils.isNotBlank( detailValuesTemp ) ) {
				String[] values = detailValuesTemp.split( "\n" );
				for ( String attrib : values ) {
					if ( attrib.indexOf( '=' ) != -1 ) {
						String[] keyValueArray = attrib.split( "=", 2 );
						details.put( keyValueArray[0].trim(), keyValueArray[1].trim() );
					}
				}
			}
		}
		this.details = details;
	}
	
	public void setDetailValues( AttributeSet detailsAttribs ) {
		StringBuffer sb = new StringBuffer();
		if ( detailsAttribs != null ) {
			Iterator<String> keyIter = detailsAttribs.keySet().iterator();
			while ( keyIter.hasNext() ) {
				String key = keyIter.next();
				sb.append( key ).append( '=' ).append( detailsAttribs.get( key ) );
				if ( keyIter.hasNext() ) {
					sb.append( '\n' );
				}
			}
		}
		detailValues = sb.toString();
	}
	
	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getPermissionId()
	 */
	public String getPermissionId() {
		return permissionId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getName()
	 */
	public String getName() {
		return name;
	}

	public void setDescription(String permissionDescription) {
		this.description = permissionDescription;
	}

	public void setName(String permissionName) {
		this.name = permissionName;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "permissionId", permissionId );
		m.put( "name", name );
		m.put( "details", getDetails() );
		return m;
	}

	public void setDetails( AttributeSet details ) {
		this.details = details;
		setDetailValues(details);
	}
	
	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public AttributeSet getDetails() {
		return details;
	}
	
	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}


	public void refresh() {
		// do nothing - not a persistable object
	}
	
	@Override
	public void refreshNonUpdateableReferences() {
		// do nothing - not a persistable object
	}
	@Override
	public void refreshReferenceObject(String referenceObjectName) {
		// do nothing - not a persistable object
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#beforeInsert()
	 */
	@Override
	@PrePersist
	public void beforeInsert() {
		throw new UnsupportedOperationException( "This object should never be persisted.");
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#beforeUpdate()
	 */
	@Override
	@PreUpdate
	public void beforeUpdate() {
		throw new UnsupportedOperationException( "This object should never be persisted.");
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
	 */
	@Override
	public void beforeInsert(PersistenceBroker persistenceBroker)
			throws PersistenceBrokerException {
		throw new UnsupportedOperationException( "This object should never be persisted.");
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
	 */
	@Override
	public void beforeUpdate(PersistenceBroker persistenceBroker) {
		throw new UnsupportedOperationException( "This object should never be persisted.");
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
	 */
	@Override
	public void beforeDelete(PersistenceBroker persistenceBroker)
			throws PersistenceBrokerException {
		throw new UnsupportedOperationException( "This object should never be persisted.");
	}

}

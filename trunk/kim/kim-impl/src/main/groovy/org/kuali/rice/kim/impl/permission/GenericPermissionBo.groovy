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
package org.kuali.rice.kim.impl.permission

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.apache.commons.lang.StringUtils
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

@Entity
@Table(name = "KRIM_PERM_T")
public class GenericPermissionBo extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="PERM_ID")
    protected String id;
    protected String namespaceCode;
    protected String name;
    protected String description;
    protected boolean active;
    protected String templateId;
    protected String detailValues;
    protected Map<String,String> details;
    PermissionTemplateBo template = new PermissionTemplateBo();
    List<PermissionAttributeBo> attributeDetails
    
    /**
     * This constructs a ...
     * 
     */
    public GenericPermissionBo() {
    }
    
    public GenericPermissionBo( PermissionBo perm ) {
        loadFromPermission( perm );

    }

    public void loadFromPermission( PermissionBo perm ) {
        setId( perm.getId() );
        setNamespaceCode( perm.getNamespaceCode() );
        setTemplate(perm.getTemplate());
        setAttributeDetails(perm.attributeDetails);
        setDetailValues(perm.detailObjectsValues);
        setName( perm.getName() );
        setTemplateId( perm.getTemplateId() );
        setDescription( perm.getDescription() );
        setActive( perm.isActive() );
        setDetails( perm.getAttributes() );
        setVersionNumber(perm.getVersionNumber());
        setObjectId(perm.getObjectId());
        setExtension(perm.getExtension());

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
        Map<String,String> details = new HashMap<String,String>();
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
    
    public void setDetailValues( Map<String, String> detailsAttribs ) {
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

   public boolean isActive() {
       return active;
   }

   public void setActive(boolean active) {
       this.active = active;
   }

   public String getDescription() {
       return description;
   }

   public String getId() {
       return id;
   }

   public String getName() {
       return name;
   }

   PermissionTemplateBo getTemplate() {
       return template;
   }

   public void setDescription(String permissionDescription) {
       this.description = permissionDescription;
   }

   public void setName(String permissionName) {
       this.name = permissionName;
   }

    public void setDetails( Map<String,String> details ) {
        this.details = details;
        setDetailValues(details);
    }
    
    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<String,String> getDetails() {
        return details;
    }
    
    public String getNamespaceCode() {
        return this.namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public void refreshNonUpdateableReferences() {
        // do nothing - not a persistable object
    }
    @Override
    public void refreshReferenceObject(String referenceObjectName) {
        // do nothing - not a persistable object
    }

    @Override
    protected void prePersist() {
        throw new UnsupportedOperationException( "This object should never be persisted.");
    }
    
    @Override
    protected void preUpdate() {
        throw new UnsupportedOperationException( "This object should never be persisted.");
    }

    @Override
    protected void preRemove() {
        throw new UnsupportedOperationException( "This object should never be persisted.");
    }

    public static PermissionBo toPermissionBo(GenericPermissionBo bo) {
        PermissionBo permission = new PermissionBo()
        permission.setTemplateId(bo.templateId)
        permission.setId(bo.id)
        permission.setTemplate(bo.getTemplate())
        permission.setActive(bo.active)
        permission.setDescription(bo.description)
        permission.setName(bo.name)
        permission.setNamespaceCode(bo.namespaceCode)
        permission.setAttributeDetails(bo.getAttributeDetails())
        permission.setAttributes(bo.getDetails())
        permission.setVersionNumber(bo.versionNumber)
        permission.setObjectId(bo.objectId)
        permission.setExtension(bo.getExtension());
        return permission
    }
}

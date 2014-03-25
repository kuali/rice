/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.data.metadata.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.MetadataChild;

/**
 * {@inheritDoc}
 */
public abstract class MetadataChildBase extends MetadataCommonBase implements MetadataChild {
	private static final long serialVersionUID = 2244738385101424133L;

	protected MetadataChild embeddedMetadataChild;

	protected Class<?> relatedType;
	protected List<DataObjectAttributeRelationship> attributeRelationships;
    protected MetadataChild inverseRelationship;

	protected Boolean savedWithParent;
	protected Boolean deletedWithParent;
	protected Boolean loadedAtParentLoadTime;
	protected Boolean loadedDynamicallyUponUse;

    /**
    * {@inheritDoc}
    */
	@Override
	public boolean isSavedWithParent() {
		if (savedWithParent != null) {
			return savedWithParent;
		}
		if (embeddedMetadataChild != null) {
			return embeddedMetadataChild.isSavedWithParent();
		}
		return false;
	}

	public void setSavedWithParent(boolean savedWithParent) {
		this.savedWithParent = savedWithParent;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public boolean isDeletedWithParent() {
		if (deletedWithParent != null) {
			return deletedWithParent;
		}
		if (embeddedMetadataChild != null) {
			return embeddedMetadataChild.isDeletedWithParent();
		}
		return false;
	}

	public void setDeletedWithParent(boolean deletedWithParent) {
		this.deletedWithParent = deletedWithParent;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public boolean isLoadedAtParentLoadTime() {
		if (loadedAtParentLoadTime != null) {
			return loadedAtParentLoadTime;
		}
		if (embeddedMetadataChild != null) {
			return embeddedMetadataChild.isLoadedAtParentLoadTime();
		}
		return false;
	}

	public void setLoadedAtParentLoadTime(boolean loadedAtParentLoadTime) {
		this.loadedAtParentLoadTime = loadedAtParentLoadTime;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public boolean isLoadedDynamicallyUponUse() {
		if (loadedDynamicallyUponUse != null) {
			return loadedDynamicallyUponUse;
		}
		if (embeddedMetadataChild != null) {
			return embeddedMetadataChild.isLoadedDynamicallyUponUse();
		}
		return false;
	}

	public void setLoadedDynamicallyUponUse(boolean loadedDynamicallyUponUse) {
		this.loadedDynamicallyUponUse = loadedDynamicallyUponUse;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public List<DataObjectAttributeRelationship> getAttributeRelationships() {
		if (attributeRelationships != null) {
			return Collections.unmodifiableList(attributeRelationships);
		}
		if (embeddedMetadataChild != null) {
			return embeddedMetadataChild.getAttributeRelationships();
		}
		return Collections.emptyList();
	}

	public void setAttributeRelationships(List<DataObjectAttributeRelationship> attributeRelationships) {
		this.attributeRelationships = attributeRelationships;
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public MetadataChild getInverseRelationship() {
        if (inverseRelationship != null) {
            return inverseRelationship;
        }
        if (embeddedMetadataChild != null) {
            return embeddedMetadataChild.getInverseRelationship();
        }
        return null;
    }

    public void setInverseRelationship(MetadataChild inverseRelationship) {
        this.inverseRelationship = inverseRelationship;
    }

    /**
    * {@inheritDoc}
    */
    @Override
	public String getParentAttributeNameRelatedToChildAttributeName(String childAttribute) {
        for(DataObjectAttributeRelationship dataObjectAttributeRelationship : attributeRelationships){
            if(StringUtils.equals(dataObjectAttributeRelationship.getChildAttributeName(),childAttribute)){
                return dataObjectAttributeRelationship.getParentAttributeName();
            }
        }
        return null;
    }

    /**
    * {@inheritDoc}
    */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName()).append(" [");
		builder.append("name=").append(getName()).append(", ");
		builder.append("relatedType=").append(getRelatedType()).append(", ");
		builder.append("attributes=").append(getAttributeRelationships()).append(", ");
		builder.append("backingObjectName=").append(getBackingObjectName()).append(", ");
		builder.append("readOnly=").append(isReadOnly());
		builder.append(", savedWithParent=");
		builder.append(savedWithParent);
		builder.append(", deletedWithParent=");
		builder.append(deletedWithParent);
		builder.append(", loadedAtParentLoadTime=");
		builder.append(loadedAtParentLoadTime);
		builder.append(", loadedDynamicallyUponUse=");
		builder.append(loadedDynamicallyUponUse);
		if (embeddedMetadataChild != null) {
			builder.append(", embedded=").append(embeddedMetadataChild);
		}
		builder.append("]");
		return builder.toString();
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public Class<?> getRelatedType() {
		return relatedType;
	}

	public void setRelatedType(Class<?> relatedType) {
		this.relatedType = relatedType;
	}

    /**
    * Gets the metadata from the child
    *
    * @return embedded metadata
    */
	public MetadataChild getEmbeddedMetadataChild() {
		return embeddedMetadataChild;
	}

	public void setEmbeddedMetadataChild(MetadataChild embeddedMetadataChild) {
		this.embeddedMetadataChild = embeddedMetadataChild;
		setEmbeddedCommonMetadata(embeddedMetadataChild);
	}

}

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

import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectCollectionSortAttribute;

/**
* Collection meta data.
*
* <p>
* Implementation that represents the meta data for a collection in a data object.
* </p>
*
* @author Kuali Rice Team (rice.collab@kuali.org)
*/
public class DataObjectCollectionImpl extends MetadataChildBase implements DataObjectCollection {
	private static final long serialVersionUID = -785119931770775640L;

	protected DataObjectCollection embeddedCollection;

    protected String elementLabel;
	protected Long minItems;
	protected Long maxItems;
	protected List<DataObjectCollectionSortAttribute> defaultOrdering;
	protected Boolean indirectCollection;

    /**
    * The elementLabel defines the name to be used for a single object within the collection.
    *
    * <p>
    * For example: "Address" may be the name of one object within the "Addresses" collection.
    * </p>
    */
    @Override
	public String getElementLabel() {
		if (elementLabel == null) {
			elementLabel = getLabelFromPropertyName(relatedType.getSimpleName());
		}
        return elementLabel;
    }

    /**
    * Sets name used for single object within collection.
    *
    * @param elementLabel single object name
    */
    public void setElementLabel(String elementLabel) {
        this.elementLabel = elementLabel;
    }

	@Override
	public Long getMinItems() {
		if (minItems != null) {
			return minItems;
		}
		if (embeddedCollection != null) {
			return embeddedCollection.getMinItems();
		}
		return null;
    }

    /**
    * Sets minimum items in collection.
    *
    * @param minOccurs minimum items in collection.
    */
	public void setMinItemsInCollection(Long minOccurs) {
		this.minItems = minOccurs;
    }

    /**
    * {@inheritDoc}
    */
	@Override
	public Long getMaxItems() {
		if (maxItems != null) {
			return maxItems;
		}
		if (embeddedCollection != null) {
			return embeddedCollection.getMaxItems();
		}
		return null;
    }

    /**
    * Sets maximum items in collection.
    *
    * @param maxOccurs maximum items in collection.
    */
	public void setMaxItemsInCollection(Long maxOccurs) {
		this.maxItems = maxOccurs;
    }

    /**
    * {@inheritDoc}
    */
	@Override
	public List<DataObjectCollectionSortAttribute> getDefaultOrdering() {
		if (defaultOrdering != null) {
			return defaultOrdering;
		}
		if (embeddedCollection != null) {
			return embeddedCollection.getDefaultOrdering();
		}
		return Collections.emptyList();
	}

    /**
    * Sets attribute that the default order of the collection.
    *
    * @param defaultCollectionOrdering attribute name
    */
	public void setDefaultCollectionOrderingAttributeNames(
			List<DataObjectCollectionSortAttribute> defaultCollectionOrdering) {
		this.defaultOrdering = defaultCollectionOrdering;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public boolean isIndirectCollection() {
		if (indirectCollection != null) {
			return indirectCollection;
		}
		if (embeddedCollection != null) {
			return embeddedCollection.isIndirectCollection();
		}
		return false;
	}

    /**
    * Sets whether linked item is used.
    *
    * @param indirectCollection whether link item used.
    */
	public void setIndirectCollection(boolean indirectCollection) {
		this.indirectCollection = indirectCollection;
	}

    /**
    * Gets the embedded collection.
    *
    * @return the embedded collection, if it exists.
    */
	public DataObjectCollection getEmbeddedCollection() {
		return embeddedCollection;
	}

	public void setEmbeddedCollection(DataObjectCollection embeddedCollection) {
		this.embeddedCollection = embeddedCollection;
		setEmbeddedMetadataChild(embeddedCollection);
	}

}

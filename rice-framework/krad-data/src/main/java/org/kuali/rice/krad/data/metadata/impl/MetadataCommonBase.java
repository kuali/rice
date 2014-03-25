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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.MetadataCommon;
import org.kuali.rice.krad.data.metadata.MetadataMergeAction;

/**
 * Class defining common attributes on many different components of the metadata (data objects, attributes, etc...)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class MetadataCommonBase implements MetadataCommonInternal {
	private static final long serialVersionUID = 2610090812919046672L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MetadataCommonBase.class);

	protected MetadataCommon embeddedCommonMetadata;
	protected MetadataMergeAction mergeAction = MetadataMergeAction.MERGE;

	protected String backingObjectName;
	protected String name;
	protected String label;
	protected String shortLabel;
	protected String description;
	protected Boolean readOnly = false;

	/**
	 * Returns the object's name without relying on embedded metadata. To override, this name must be set.
	 */
	@Override
	public Object getUniqueKeyForMerging() {
		return name;
	}

	@Override
	public String getBackingObjectName() {
		if (backingObjectName != null) {
			return backingObjectName;
		}
		if (embeddedCommonMetadata != null) {
			return embeddedCommonMetadata.getBackingObjectName();
		}
		return getName();
	}

	public void setBackingObjectName(String backingObjectName) {
		this.backingObjectName = backingObjectName;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		// locally set
		if (label != null) {
			return label;
		}
		// we have an embedded, check it's label
		if (embeddedCommonMetadata != null) {
			return embeddedCommonMetadata.getLabel();
		}
		return getLabelFromPropertyName(name);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getShortLabel() {
		// locally set
		if (StringUtils.isNotBlank(shortLabel)) {
			return shortLabel;
		}
		// we have an embedded, check it's short label
		if (embeddedCommonMetadata != null) {
			return embeddedCommonMetadata.getShortLabel();
		}
		// default to the label (local or embedded)
		return getLabel();
	}

	public void setShortLabel(String shortLabel) {
		this.shortLabel = shortLabel;
	}

	@Override
	public String getDescription() {
		if (description != null) {
			return description;
		}
		if (embeddedCommonMetadata != null) {
			return embeddedCommonMetadata.getDescription();
		}
		return "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean isReadOnly() {
		if (readOnly != null) {
			return readOnly;
		}
		if (embeddedCommonMetadata != null) {
			return embeddedCommonMetadata.isReadOnly();
		}
		return false;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName()).append(" [");
		builder.append("name=").append(getName()).append(", ");
		builder.append("label=").append(getLabel()).append(", ");
		builder.append("backingObjectName=").append(getBackingObjectName()).append(", ");
		builder.append("readOnly=").append(isReadOnly());
		builder.append(", ").append("mergeAction=").append(mergeAction);
		builder.append("]");
		return builder.toString();
	}

    /**
    * Parses the label from the property name.
    *
    * @param propertyName the full property name including separators
    */
	protected String getLabelFromPropertyName(String propertyName) {
		// We only want to include the component after the last property separator
		if (propertyName.contains(".")) {
			propertyName = StringUtils.substringAfterLast(propertyName, ".");
		}
		StringBuilder label = new StringBuilder(propertyName);
		// upper case the 1st letter
		label.replace(0, 1, label.substring(0, 1).toUpperCase());
		// loop through, inserting spaces when cap
		for (int i = 0; i < label.length(); i++) {
			if (Character.isUpperCase(label.charAt(i)) || Character.isDigit(label.charAt(i))) {
				label.insert(i, ' ');
				i++;
			}
		}

		return label.toString().trim();
	}

	@Override
	public MetadataCommon getEmbeddedCommonMetadata() {
		return embeddedCommonMetadata;
	}

	@Override
	public void setEmbeddedCommonMetadata(MetadataCommon embeddedCommonMetadata) {
		this.embeddedCommonMetadata = embeddedCommonMetadata;
	}

	@Override
	public MetadataMergeAction getMergeAction() {
		return mergeAction;
	}

	public void setMergeAction(MetadataMergeAction mergeAction) {
		this.mergeAction = mergeAction;
	}

    /**
    * Merges multiple lists into one.
    *
    * <p>
    *     Merges embedded and locallists.
    * </p>
    *
    * @param embeddedList the embedded list.
    * @param localList the local list.
    */
	protected <T extends MetadataCommon> List<T> mergeLists(List<T> embeddedList, List<T> localList) {
		if (localList == null) {
			return new ArrayList<T>(embeddedList);
		}
		List<T> mergedList = new ArrayList<T>(embeddedList.size() + localList.size());
		// Go through the local list (which can override the embedded list and add to a map by name)
		Map<Object, T> localObjectMap = new HashMap<Object, T>(localList.size());
		for (T item : localList) {
			if (item instanceof MetadataCommonInternal) {
				localObjectMap.put(((MetadataCommonInternal) item).getUniqueKeyForMerging(), item);
			} else {
				localObjectMap.put(item.getName(), item);
			}
		}
		// Go through Master (to be embedded) list - add to merged list
		for (T item : embeddedList) {
			Object mergeKey = item.getName();
			if (item instanceof MetadataCommonInternal) {
				mergeKey = ((MetadataCommonInternal) item).getUniqueKeyForMerging();
			}
			// check for key match in local list
			T localItem = localObjectMap.get(mergeKey);
			// if no match, add to list
			if (localItem == null) {
				mergedList.add(item);
			} else {
				if (localItem.getMergeAction() == MetadataMergeAction.MERGE) {
					// add the master item as embedded in the local item
					if (localItem instanceof MetadataCommonInternal) {
						((MetadataCommonInternal) localItem).setEmbeddedCommonMetadata(item);
						if (localItem instanceof DataObjectAttributeInternal && item instanceof DataObjectAttribute) {
							((DataObjectAttributeInternal) localItem).setEmbeddedAttribute((DataObjectAttribute) item);
						}
					} else {
						LOG.warn("List item implementation class ("
								+ localItem.getClass().getName()
								+ ") does not implement the MetadataCommonInternal interface.  It can not merge in previously extracted metadata.");
					}
					// add the local item to the list
					mergedList.add(localItem);
				} else if (localItem.getMergeAction() == MetadataMergeAction.REPLACE) {
					// use the local metadata and do not embed
					mergedList.add(localItem);
				} else if (localItem.getMergeAction() == MetadataMergeAction.REMOVE) {
					// Do nothing - just don't add to the list
				} else if (localItem.getMergeAction() == MetadataMergeAction.NO_OVERRIDE) {
					// Ignore the overriding item and add the original
					mergedList.add(item);
				} else {
					LOG.warn("Unsupported MetadataMergeAction: " + localItem.getMergeAction() + " on " + localItem);
				}
				// remove the item from the map since it's been merged
				localObjectMap.remove(mergeKey);
			}
		}
		// now, the map only has the remaining items - add them to the end of the list
		mergedList.addAll(localObjectMap.values());

		return mergedList;
	}
}
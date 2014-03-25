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
package org.kuali.rice.krad.data.provider.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.MetadataMergeAction;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeInternal;
import org.kuali.rice.krad.data.metadata.impl.DataObjectMetadataInternal;
import org.kuali.rice.krad.data.provider.CompositeMetadataProvider;
import org.kuali.rice.krad.data.provider.MetadataProvider;

/**
 * This "provider" aggregates the other metadata providers given in its spring configuration.
 *
 * <p>
 * The providers are processed in order, each one having the option to overlay information provided by earlier providers
 * in the chain. The nature of the merge/overlay depends on the value of the mergeAction property on the returned
 * object.
 * </p>
 * 
 * @see MetadataMergeAction
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CompositeMetadataProviderImpl extends MetadataProviderBase implements CompositeMetadataProvider {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(CompositeMetadataProviderImpl.class);

	protected List<MetadataProvider> providers;

    /**
     * {@inheritDoc}
     */
	@Override
	protected synchronized void initializeMetadata(Collection<Class<?>> types) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Initializing Metadata from sources: " + providers);
		}
		masterMetadataMap.clear();
		if (!providers.isEmpty()) {
			// the first one is the master list - later providers will be merged in by embedding the earlier processed
			// items
			// into the later ones.
			// This allows the later providers to be "less complete", only setting the values they want/need to
			// override.
			for (MetadataProvider provider : providers) {
				if (LOG.isInfoEnabled()) {
					LOG.info(" *** Processing MetadataProvider: " + provider);
				}
				// Obtain the data from the next provider
				// If the provider requires us to provide it the types discovered so far, then pull that
				// from the keys of the map
				Map<Class<?>, DataObjectMetadata> metadata = null;
				if (provider.requiresListOfExistingTypes()) {
					metadata = provider.provideMetadataForTypes(masterMetadataMap.keySet());
				} else {
					metadata = provider.provideMetadata();
				}
				// for these, we need to, if the objects are already in the master map, not replace it, but wrap the
				// existing object in the map with the one from the next provider
				for (Class<?> dataObjectType : metadata.keySet()) {
					DataObjectMetadata existingMetadata = masterMetadataMap.get(dataObjectType);
					DataObjectMetadata newMetadata = metadata.get(dataObjectType);
					mergeMetadataForType(newMetadata, existingMetadata);
				}
			}
			// Now that all the data has been merged, go through the data object attributes to look for inherited
			// properties
			// and merge those attributes appropriately
			// This can not be done as part of the "normal" merge process as the needed attributes may not exist
			// until all the providers have been processed.
			mergeInheritedAttributes();
		}
	}

    /**
     * Merges attributes from the current map with those that are inherited.
     */
	protected void mergeInheritedAttributes() {
		for (DataObjectMetadata metadata : masterMetadataMap.values()) {
			for (DataObjectAttribute attr : metadata.getAttributes()) {
				if (attr.isInherited()) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Processing inherited attribute on " + metadata.getType() + "." + attr.getName()
								+ " : " + attr.getInheritedFromType() + " / "
								+ attr.getInheritedFromParentAttributeName() + "."
								+ attr.getInheritedFromAttributeName());
					}
					// now that we know there is a cross-data object inheritance, we pull the attribute with the
					// inheritance definition (Which will never have an embedded attribute at this point, since
					// they are created without one by the Annotation provider.)
					DataObjectAttribute originalDataObjectAttribute = attr.getOriginalDataObjectAttribute();
					if (originalDataObjectAttribute == null) {
						// sanity check - just in case someone misconfigured this (via spring provider)
						LOG.error("originalDataObjectAttribute was null for " + attr);
						continue;
					}
					// we need something which allows embedding
					if (!(originalDataObjectAttribute instanceof DataObjectAttributeInternal)) {
						LOG.warn("The originalDataObjectAttribute does not implement the DataObjectAttributeInternal interface, we have no access to the embeddedAttribute property: "
								+ originalDataObjectAttribute);
					}
					Class<?> inheritedFromType = originalDataObjectAttribute.getInheritedFromType();
					String inheritedFromAttributeName = originalDataObjectAttribute.getInheritedFromAttributeName();
					if (inheritedFromType == null || StringUtils.isBlank(inheritedFromAttributeName)) {
						// another sanity check
						LOG.error("inheritedFromType/inheritedFromAttributeName not completely populated for "
								+ originalDataObjectAttribute);
						continue;
					}
					// Now, attempt to find the data object type in the map
					DataObjectMetadata inheritedMetadata = masterMetadataMap.get(inheritedFromType);
					if (inheritedMetadata == null) {
						// again - it may not exist (since linked by class name) - so handle that gracefully with a
						// warning
						LOG.warn("The metadata object for the inheritance does not exist, skipping: "
								+ inheritedFromType);
						continue;
					}
					DataObjectAttribute inheritedAttribute = inheritedMetadata.getAttribute(inheritedFromAttributeName);
					if (inheritedAttribute == null) {
						// Really - we should have blown up before this, since the linker would have failed
						LOG.warn("The attribute on the metadata object for the inheritance does not exist, skipping: "
								+ inheritedFromType + "." + inheritedFromAttributeName);
						continue;
					}
					// Finally - we have the data we need - MERGE IT!
					((DataObjectAttributeInternal) originalDataObjectAttribute)
							.setEmbeddedAttribute(inheritedAttribute);
				}
			}
		}
	}

    /**
     * Merges the metadata of two specific types.
     *
     * @param newMetadata the metadata to merge in.
     * @param existingMetadata the existing metadata to merge into.
     */
	protected void mergeMetadataForType(DataObjectMetadata newMetadata, DataObjectMetadata existingMetadata) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Type: " + newMetadata.getType() + " : " + newMetadata);
		}
		// get the object from the existing map
		// if not there, just set the new value and continue
		if (existingMetadata == null) {
			if (newMetadata.getMergeAction() != MetadataMergeAction.REMOVE) {
				LOG.debug("New Type - Adding metadata to masterMetadataMap");
				masterMetadataMap.put(newMetadata.getType(), newMetadata);
			} else {
				// If the merge action states to remove, then it's (likely) an incomplete definition and we
				// don't want to add it
				// This would happen if the definition was removed from an earlier metadata provider but the
				// override to
				// remove it is still present.
				LOG.warn("Attempt to REMOVE a DataObjectMetadata which did not exist: " + newMetadata.getType());
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Type already exists.  Merging previously retrieved metadata using action "
						+ newMetadata.getMergeAction() + " : " + newMetadata.getType());
			}
			if (newMetadata.getMergeAction() == MetadataMergeAction.MERGE) {
				// if there, replace with the object returned from the new map and set the prior one as
				// embedded
				// the embedding logic is only on this internal interface
				if (newMetadata instanceof DataObjectMetadataInternal
						&& existingMetadata instanceof DataObjectMetadataInternal) {
					((DataObjectMetadataInternal) newMetadata)
							.setEmbedded((DataObjectMetadataInternal) existingMetadata);
					masterMetadataMap.put(newMetadata.getType(), newMetadata);
				} else {
					LOG.warn("New or existing Metadata object does not implement the DataObjectMetadataInternal interface, unable to embed the previously retrieved metadata.  REPLACING the entry in the masterMetadataMap ("
							+ existingMetadata + ") with the new version: " + newMetadata);
					masterMetadataMap.put(newMetadata.getType(), newMetadata);
				}
			} else if (newMetadata.getMergeAction() == MetadataMergeAction.REPLACE) {
				// use the local metadata and do not embed
				masterMetadataMap.put(newMetadata.getType(), newMetadata);
			} else if (newMetadata.getMergeAction() == MetadataMergeAction.REMOVE) {
				masterMetadataMap.remove(newMetadata.getType());
			} else if (newMetadata.getMergeAction() == MetadataMergeAction.NO_OVERRIDE) {
				// Do nothing, leave the original in the map

			} else {
				LOG.warn("Unsupported MetadataMergeAction: " + newMetadata.getMergeAction() + " on " + newMetadata);
			}
		}

	}

    /**
     * {@inheritDoc}
     */
	@Override
	public List<MetadataProvider> getProviders() {
		return providers;
	}

    /**
     * Setter for the providers.
     *
     * @param providers the providers to set.
     */
	public void setProviders(List<MetadataProvider> providers) {
		this.providers = providers;
	}

}

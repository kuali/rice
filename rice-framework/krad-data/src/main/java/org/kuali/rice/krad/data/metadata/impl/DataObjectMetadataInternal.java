package org.kuali.rice.krad.data.metadata.impl;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;


/**
 * Additional methods which are needed to support the linking between embedded metadata objects but that we don't want
 * to expose on our public interfaces.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface DataObjectMetadataInternal extends DataObjectMetadata {
	/**
	 * The embedded DataObjectMetadata which will be used to fill in values not specified by this DataObjectMetadata.
	 * 
	 * @return the embedded metadata, or no if no metadata is embedded
	 */
	DataObjectMetadataInternal getEmbedded();

	void setEmbedded(DataObjectMetadataInternal embedded);
}

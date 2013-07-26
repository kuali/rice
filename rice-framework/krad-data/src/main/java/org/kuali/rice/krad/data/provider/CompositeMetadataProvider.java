package org.kuali.rice.krad.data.provider;

import java.util.List;

/**
 * This metadata provider forms the main provider for the krad-data module. It's responsible for merging the provided
 * list of metadata providers.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)

 * 
 */
public interface CompositeMetadataProvider extends MetadataProvider {

	/**
	 * The list of providers this provider will use. They must be in the necessary order for processing. Later providers
	 * in the list
	 * 
	 * @return a list of all metadata providers contained within this composite provider
	 */
	List<MetadataProvider> getProviders();

}
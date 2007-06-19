package edu.iu.uis.eden.messaging.remotedservices;


/**
 * Defines an interface with methods that help us test distributed caching.
 *
 * @author Eric Westfall
 */
public interface DistributedCachingTestHelperService {

	public boolean isDocumentTypeCachedByName(String documentTypeName);
	public boolean isDocumentTypeCachedById(Long documentTypeId);
	
}

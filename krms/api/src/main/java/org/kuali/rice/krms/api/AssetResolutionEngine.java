package org.kuali.rice.krms.api;

public interface AssetResolutionEngine {

	/**
	 * 
	 * @param asset
	 * @return
	 * @throws AssetResolutionException
	 */
	<T> T resolveAsset(Asset asset) throws AssetResolutionException;
	
	/**
	 * 
	 * @param asset
	 * @param value
	 */
	void addAssetValue(Asset asset, Object value);
	
	/**
	 * 
	 * @param assetResolver
	 */
	void addAssetResolver(AssetResolver<?> assetResolver);
	
}

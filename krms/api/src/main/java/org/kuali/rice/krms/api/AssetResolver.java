package org.kuali.rice.krms.api;

import java.util.Map;
import java.util.Set;

public interface AssetResolver <T> {
	
	/**
	 * @return the set of assets that this resolver requires to resolve its output, or an empty set if it has no prereqs;
	 */
	Set<Asset> getPrerequisites();
	
	/**
	 * @return the asset that the implementor resolves
	 */
	Asset getOutput();
	
	/**
	 * @return an integer representing the cost of resolving the asset. 1 is cheap, Integer.MAX_INT is expensive.
	 */
	int getCost();
	
	/**
	 * @resolvedPrereqs the resolved prereqs
	 * @return the resolved asset
	 * @throws AssetResolutionException if something bad happens during the asset resolution process
	 */
	T resolve(Map<Asset, Object> resolvedPrereqs) throws AssetResolutionException;
}

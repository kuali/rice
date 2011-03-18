package org.kuali.rice.krms.api;

import java.util.List;

public interface Context {

	void execute(ExecutionEnvironment environment);
	
	List<AssetResolver<?>> getAssetResolvers();
	
	boolean appliesTo(ExecutionEnvironment environment);
	
}

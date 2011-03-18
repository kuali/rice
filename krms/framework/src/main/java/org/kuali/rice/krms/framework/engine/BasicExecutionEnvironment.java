package org.kuali.rice.krms.framework.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.AssetResolutionEngine;
import org.kuali.rice.krms.api.AssetResolutionException;
import org.kuali.rice.krms.api.AssetResolver;
import org.kuali.rice.krms.api.EngineResults;
import org.kuali.rice.krms.api.ExecutionEnvironment;
import org.kuali.rice.krms.api.SelectionCriteria;

public final class BasicExecutionEnvironment implements ExecutionEnvironment {

	private final SelectionCriteria selectionCriteria;
	private final Map<Asset, Object> facts;
	private final Map<String, String> executionOptions;
	private final EngineResults engineResults;
	private final AssetResolutionEngine assetResolutionService;
	
	public BasicExecutionEnvironment(SelectionCriteria selectionCriteria, Map<Asset, Object> facts, Map<String, String> executionOptions) {
		if (selectionCriteria == null) {
			throw new IllegalArgumentException("Selection criteria must not be null.");
		}
		if (facts == null) {
			throw new IllegalArgumentException("Facts must not be null.");
		}
		this.selectionCriteria = selectionCriteria;
		this.facts = new HashMap<Asset, Object>(facts.size());
		this.facts.putAll(facts);
		this.executionOptions = new HashMap<String, String>(executionOptions.size());
		this.executionOptions.putAll(executionOptions);
		this.engineResults = new EngineResultsImpl();
		// TODO: inject this (will have to make it non-final)
		this.assetResolutionService = new AssetResolutionEngineImpl();
	}
	
	@Override
	public SelectionCriteria getSelectionCriteria() {
		return this.selectionCriteria;
	}
	
	@Override
	public Map<Asset, Object> getFacts() {
		return Collections.unmodifiableMap(facts);
	}
	
	@Override
	public void addAssetResolver(AssetResolver<?> assetResolver) {
		assetResolutionService.addAssetResolver(assetResolver);
	}
	
	@Override
	public <T> T resolveTerm(Asset asset) throws AssetResolutionException {
		T value;
		
		// This looks funny, but works around a javac bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
		// Specifically, using <T> below works around it.
		value = assetResolutionService.<T>resolveAsset(asset);
		
		publishFact(asset, value);
		return value;
	}

	@Override
	public boolean publishFact(Asset factName, Object factValue) {
		if (facts.containsKey(factName) && ObjectUtils.equals(facts.get(factName), factValue)) {
			return false;
		}
		facts.put(factName, factValue);
		assetResolutionService.addAssetValue(factName, factValue);
		return true;
	}

	@Override
	public Map<String, String> getExecutionOptions() {
		return executionOptions;
	}
	
	@Override
	public EngineResults getEngineResults() {
		return engineResults;
	}

}

/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.framework;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.AssetResolver;

/**
 * Cheesy {@link AssetResolver} implementation for testing purposes.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AssetResolverMock<T> implements AssetResolver<T> {
	
	private T result;
	private Asset outputAsset;
	
	public AssetResolverMock(Asset outputAsset, T result) {
		this.outputAsset = outputAsset;
		this.result = result;
	}
	
	@Override
	public int getCost() { return 1; }
	
	@Override
	public Asset getOutput() { return outputAsset; }
	
	@Override
	public Set<Asset> getPrerequisites() { return Collections.emptySet(); }
	
	@Override
	public T resolve(Map<Asset, Object> resolvedPrereqs) {
		return result;
	}
};
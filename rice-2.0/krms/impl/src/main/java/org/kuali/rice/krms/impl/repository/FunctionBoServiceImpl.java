/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.function.FunctionDefinition;
import org.kuali.rice.krms.api.repository.function.FunctionRepositoryService;

/**
 * Default implementation of the {@link FunctionService}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class FunctionBoServiceImpl implements FunctionRepositoryService, FunctionBoService {
		
    private BusinessObjectService businessObjectService;
    
	@Override
	public FunctionDefinition getFunction(String functionId) {
		return getFunctionById(functionId);
	}
	
	@Override
	public List<FunctionDefinition> getFunctions(List<String> functionIds) {
		
		List<FunctionDefinition> functionDefinitions = new ArrayList<FunctionDefinition>();
		for (String functionId : functionIds){
			functionDefinitions.add( getFunctionById(functionId) );
		}
        return Collections.unmodifiableList(functionDefinitions);
	}
	
	/**
	 * This method will create a {@link FunctionDefintion} as described
	 * by the function passed in.
	 * 
	 * @see org.kuali.rice.krms.impl.repository.FunctionBoService#createFunction(org.kuali.rice.krms.api.repository.function.FunctionDefinition)
	 */
	@Override
	public FunctionDefinition createFunction(FunctionDefinition function) {
		if (function == null){
	        throw new IllegalArgumentException("function is null");
		}
		
		final String nameKey = function.getName();
		final String namespaceKey = function.getNamespace();
		final FunctionDefinition existing = getFunctionByNameAndNamespace(nameKey, namespaceKey);
		if (existing != null && existing.getName().equals(nameKey) && existing.getNamespace().equals(namespaceKey)){
            throw new IllegalStateException("the function to create already exists: " + function);			
		}
		
		FunctionBo functionBo = FunctionBo.from(function);
		businessObjectService.save(functionBo);
		return FunctionBo.to(functionBo);
	}

	/**
	 * This overridden method updates an existing Function in the repository
	 * 
	 * @see org.kuali.rice.krms.impl.repository.FunctionBoService#updateFunction(org.kuali.rice.krms.api.repository.function.FunctionDefinition)
	 */
	@Override
	public void updateFunction(FunctionDefinition function) {
		if (function == null){
			throw new IllegalArgumentException("function is null");
		}
		
		final String functionIdKey = function.getId();
		final FunctionDefinition existing = getFunctionById(functionIdKey);
		if (existing == null) {
			throw new IllegalStateException("the function does not exist: " + function);
		}
		final FunctionDefinition toUpdate;
		if (!existing.getId().equals(function.getId())){
			final FunctionDefinition.Builder builder = FunctionDefinition.Builder.create(function);
			builder.setId(existing.getId());
			toUpdate = builder.build();
		} else {
			toUpdate = function;
		}

		businessObjectService.save(FunctionBo.from(toUpdate));
	}
	
    
	/**
	 * This overridden method retrieves a function by the given function id.
	 * 
	 * @see org.kuali.rice.krms.impl.repository.FunctionBoService#getFunctionById(java.lang.String)
	 */
	@Override
	public FunctionDefinition getFunctionById(String functionId) {
		if (StringUtils.isBlank(functionId)){
            throw new IllegalArgumentException("functionId is null or blank");			
		}
		FunctionBo functionBo = businessObjectService.findBySinglePrimaryKey(FunctionBo.class, functionId);
		return FunctionBo.to(functionBo);
	}
	
	/**
	 * 
	 * This overridden method retrieves a function by the given name and namespace.
	 * 
	 * @see org.kuali.rice.krms.impl.repository.FunctionBoService#getFunctionByNameAndNamespace(java.lang.String, java.lang.String)
	 */
	public FunctionDefinition getFunctionByNameAndNamespace( String name, String namespace ){
		if (StringUtils.isBlank(name)){
			throw new IllegalArgumentException("name is null or blank");
		}
		if (StringUtils.isBlank(namespace)){
			throw new IllegalArgumentException("namespace is null or blank");
		}
				
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);
		FunctionBo functionBo = businessObjectService.findByPrimaryKey(FunctionBo.class, Collections.unmodifiableMap(map));
		return FunctionBo.to(functionBo);
	}
	
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

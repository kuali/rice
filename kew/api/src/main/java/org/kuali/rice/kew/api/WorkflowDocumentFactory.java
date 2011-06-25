/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kew.api.action.InvalidActionTakenException;
import org.kuali.rice.kew.api.doctype.IllegalDocumentTypeException;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentUpdate;

/**
 * TODO ..
 *
 */
public final class WorkflowDocumentFactory {

	private static final String CREATE_METHOD_NAME = "createDocument";
	private static final String LOAD_METHOD_NAME = "loadDocument"; 
	
    /**
     * TODO 
     * 
     * @param principalId TODO
     * @param documentTypeName TODO
     * 
     * @return TODO
     * 
     * @throws IllegalArgumentException if principalId is null or blank
     * @throws IllegalArgumentException if documentTypeName is null or blank
     * @throws IllegalDocumentTypeException if the document type does not allow for creation of a document,
     * this can occur when the given document type is used only as a parent and has no route path configured
     * @throws InvalidActionTakenException if the caller is not allowed to execute this action
     */
    public static WorkflowDocument createDocument(String principalId, String documentTypeName) {
    	return createDocument(principalId, documentTypeName, null, null);
    }
    
    /**
     * TODO
     * 
     * @param principalId TODO
     * @param documentTypeName TODO
     * @param title TODO
     * 
     * @return TODO
     * 
     * @throws IllegalArgumentException if principalId is null or blank
     * @throws IllegalArgumentException if documentTypeName is null or blank
     * @throws DocumentTypeNotFoundException if documentTypeName does not represent a valid document type
     */
    public static WorkflowDocument createDocument(String principalId, String documentTypeName, String title) {
    	DocumentUpdate.Builder builder = DocumentUpdate.Builder.create();
    	builder.setTitle(title);
    	return createDocument(principalId, documentTypeName, builder.build(), null);
    }
    
    /**
     * TODO
     * 
     * @param principalId TODO
     * @param documentTypeName TODO
     * @param documentUpdate TODO
     * @param documentContentUpdate TODO
     * 
     * @return TODO
     * 
     * @throws IllegalArgumentException if principalId is null or blank
     * @throws IllegalArgumentException if documentTypeName is null or blank
     * @throws DocumentTypeNotFoundException if documentTypeName does not represent a valid document type
     */
	public static WorkflowDocument createDocument(String principalId, String documentTypeName, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate) {
		if (StringUtils.isBlank(principalId)) {
			throw new IllegalArgumentException("principalId was null or blank");
		}
		if (StringUtils.isBlank(documentTypeName)) {
			throw new IllegalArgumentException("documentTypeName was null or blank");
		}
		Object provider = loadProvider();
		Method createMethod = null;
		try {
			createMethod = provider.getClass().getMethod(CREATE_METHOD_NAME, String.class, String.class, DocumentUpdate.class, DocumentContentUpdate.class);
		} catch (NoSuchMethodException e) {
			throw new ConfigurationException("Failed to locate valid createDocument method signature on provider class: " + provider.getClass().getName(), e);
		} catch (SecurityException e) {
			throw new ConfigurationException("Encountered security issue when attempting to access createDocument method on provider class: " + provider.getClass().getName(), e);
		}
		
		Object workflowDocument = null;
		
		try {
			workflowDocument = createMethod.invoke(provider, principalId, documentTypeName, documentUpdate, documentContentUpdate);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException("Failed to invoke " + CREATE_METHOD_NAME, e);
		} catch (InvocationTargetException e) {
			throw new ConfigurationException("Failed to invoke " + CREATE_METHOD_NAME, e);
		}

		if (!(workflowDocument instanceof WorkflowDocument)) {
			throw new ConfigurationException("Created document is not a proper instance of " + WorkflowDocument.class + ", was instead " + workflowDocument.getClass());
		}
		return (WorkflowDocument)workflowDocument;
	}
	
	public static WorkflowDocument loadDocument(String principalId, String documentId) {
		if (StringUtils.isBlank(principalId)) {
			throw new IllegalArgumentException("principalId was null or blank");
		}
		if (StringUtils.isBlank(documentId)) {
			throw new IllegalArgumentException("documentId was null or blank");
		}
		
		Object provider = loadProvider();
		Method loadMethod = null;
		try {
			loadMethod = provider.getClass().getMethod(LOAD_METHOD_NAME, String.class, String.class);
		} catch (NoSuchMethodException e) {
			throw new ConfigurationException("Failed to locate valid createDocument method signature on provider class: " + provider.getClass().getName(), e);
		} catch (SecurityException e) {
			throw new ConfigurationException("Encountered security issue when attempting to access createDocument method on provider class: " + provider.getClass().getName(), e);
		}
		
		Object workflowDocument = null;
		
		try {
			workflowDocument = loadMethod.invoke(provider, principalId, documentId);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException("Failed to invoke " + LOAD_METHOD_NAME, e);
		} catch (InvocationTargetException e) {
			throw new ConfigurationException("Failed to invoke " + LOAD_METHOD_NAME, e);
		}

		if (!(workflowDocument instanceof WorkflowDocument)) {
			throw new ConfigurationException("Loaded document is not a proper instance of " + WorkflowDocument.class + ", was instead " + workflowDocument.getClass());
		}
		return (WorkflowDocument)workflowDocument;
	}
	
	private static Object loadProvider() {
		String providerClassName = null;
		String resource = null;
		try {
            resource = new StringBuilder().append("META-INF/services/").append(WorkflowDocument.class.getName()).toString();
            final InputStream resourceStream = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(resource.toString());
            if (resourceStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream, "UTF-8"));
                providerClassName = reader.readLine().trim();
                reader.close();
                Class<?> providerClass = Class.forName(providerClassName);
                return newInstance(providerClass);
            } else {
                throw new ConfigurationException("Failed to locate a services definition file at " + resource);
            }
        } catch (IOException e) {
            throw new ConfigurationException("Failure processing services definition file at " + resource, e);
        } catch (ClassNotFoundException e) {
        	throw new ConfigurationException("Failed to load provider class: " + providerClassName, e);
        }
	}
	
	private static Object newInstance(Class<?> providerClass) {
		try {
			return providerClass.newInstance();
		} catch (InstantiationException e) {
			throw new ConfigurationException("Failed to instantiate provider class: " + providerClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException("Failed to instantiate provider class: " + providerClass.getName(), e);
		}
	}
}

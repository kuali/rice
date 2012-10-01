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
package org.kuali.rice.kew.impl.document;

import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterKey;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for dealing with application document status
 */
public class ApplicationDocumentStatusUtils {

    // Format for a parameter value containing application document status category data
    // "Even=Two,Twenty Two,Two Hundred;Odd=One,Twenty One, Two Hundred One"

    // pattern for matching each category definition, with capture groups for the category name and the value
    private static final Pattern categoriesPattern = Pattern.compile("([^;=]+)=([^;]+)");

    // pattern for matching the individual status names within a category.  Intended to operate on the
    // second capture group from the categoriesPattern
    private static final Pattern categoryElementsPattern = Pattern.compile("[^,]+");

    /**
     * The parameter name used to search for the configuration of application document status categories
     */
    public static final String CATEGORIES_PARAMETER_NAME = "APPLICATION_DOCUMENT_STATUS_CATEGORIES";

    /**
     * the namespace used to search for the document type component
     */
    public static final String CATEGORIES_COMPONENT_NAMESPACE = "KR-WKFLW";

    /**
     * @see {@link #getApplicationDocumentStatusCategories(org.kuali.rice.kew.doctype.bo.DocumentType)}
     */
    public static LinkedHashMap<String, List<String>> getApplicationDocumentStatusCategories(String documentTypeName) {
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        return getApplicationDocumentStatusCategories(documentType);
    }

    /**
     * <p>Returns the categories defined in parameter data for the given DocumentType.  The returned ordered map's keys
     * are the category names, and the values are Lists of the status values within the category.  Ordering is
     * maintained so that the form field can reflect the configuration in the parameter. </p>
     *
     * <p>The parameter holding the configuration for these categories will have an application ID of KUALI, a namespace
     * of KR-WKFLW, and a component of the document type name.  The parameter name will be
     * APPLICATION_DOCUMENT_STATUS_CATEGORIES and the value will be of the form
     * "Incomplete=Awaiting ABC Approval,Awaiting XYZ Approval;Complete=Cancelled,Disapproved,Closed;".</p>
     *
     * <p>Note that the hierarchy for the given document type will be walked until such a parameter is
     * found, or the ancestry is exhausted.</p>
     *
     * @param documentType the document type for which to retrieve the defined application document status categories
     * @return the application document status categories, or an empty map if there are none defined for the given
     * document type
     */
    public static LinkedHashMap<String, List<String>> getApplicationDocumentStatusCategories(DocumentType documentType) {
        LinkedHashMap<String, List<String>> results = new LinkedHashMap<String, List<String>>();

        if (documentType != null) {
            // check the hierarchy
            Parameter appDocStatusCategoriesParameter = null;
            DocumentType docTypeAncestor = documentType;
            while (docTypeAncestor != null) {
                ParameterKey parameterKey =
                        ParameterKey.create(docTypeAncestor.getApplicationId(), CATEGORIES_COMPONENT_NAMESPACE, documentType.getName(),
                                CATEGORIES_PARAMETER_NAME);

                appDocStatusCategoriesParameter = CoreServiceApiServiceLocator.getParameterRepositoryService().getParameter(parameterKey);

                // save a potentially un-needed fetch of the parent doc type fetch
                if (appDocStatusCategoriesParameter != null) break;

                // walk up the hierarchy
                docTypeAncestor = docTypeAncestor.getParentDocType();
            }

            if (appDocStatusCategoriesParameter != null) {
                // parse groupings and create headings
                String categoriesString = appDocStatusCategoriesParameter.getValue();

                Matcher categoriesMatcher = categoriesPattern.matcher(categoriesString);
                while (categoriesMatcher.find()) {
                    String groupName = categoriesMatcher.group(1).trim();
                    String groupBody = categoriesMatcher.group(2);

                    List<String> categoryElements = new ArrayList<String>();

                    Matcher categoryElementsMatcher = categoryElementsPattern.matcher(groupBody);

                    while (categoryElementsMatcher.find()) {
                        String groupElement = categoryElementsMatcher.group().trim();
                        categoryElements.add(groupElement);
                    }

                    results.put(groupName, categoryElements);
                }

            }
        }
        return results;
    }

}

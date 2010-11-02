/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.core.xml;

import java.util.List;

/**
 * Represents a Map between a Schema URI and a set of ChainedXMLFilter
 * implementations required to migrate a Document from the Previous version.
 */
public class XMLInputFilterDefinition {
    private String schemaURI;
    private List<XMLInputFilterEntry> entries;

    /**
     * Returns the Schema URI associated with this Entry
     *
     * @return The Schema URI
     */
    public String getSchemaURI() {
        return schemaURI;
    }

    /**
     * Set the Schema URI associated with this Entry
     *
     * @param schemaUri The Schema URI
     */
    public void setSchemaURI(String schemaURI) {
        this.schemaURI = schemaURI;
    }

    /**
     * Returns the XMLInputFieldEntry list for XMLInputFilterDefinition
     *
     * @return the XMLInputFieldEntry list
     */
    public List<XMLInputFilterEntry> getEntries() {
        return entries;
    }

    /**
     * Sets the XMLInputFieldEntry list for XMLInputFilterDefinition
     *
     * @param entries the XMLInputFieldEntry list
     */    
    public void setEntries(List<XMLInputFilterEntry> entries) {
        this.entries = entries;
    }
}

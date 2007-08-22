/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.batch;

import org.jdom.Namespace;

public class XmlConstants {

    // Namespaces and Schemas
    
    public static final Namespace WORKFLOW_NS = Namespace.getNamespace("", "ns:workflow");
    public static final Namespace SCHEMA_NS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    public static final String WORKFLOW_SCHEMA_LOCATION = "ns:workflow resource:WorkflowData";
    
    // XML Elements
    
    public static final String DATA_ELEMENT = "data";
    
    // XML Attributes
    
    public static final String SCHEMA_LOCATION_ATTR = "schemaLocation";

}

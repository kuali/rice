/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.bo;

/**
 * Interface for items that are often used in sets where one should be used if no explicit one is requested. 
 * 
 * Created for use by the KIM entity attributes.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface Defaultable {

	boolean isDefaultValue();
	void setDefaultValue( boolean dflt );
	
}

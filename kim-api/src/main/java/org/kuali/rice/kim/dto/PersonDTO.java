/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.dto;

import java.util.HashMap;

/**
 * This is a Data Transfer Object (DTO) that is used by the service layer.
 * 
 * This class is a helper object for those systems that only recognize the Person 
 * concept and not the more general Entity concept.  
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PersonDTO extends AbstractEntityBaseDTO {
    private HashMap<String,PersonAttributeDTO> personAttributesDtos;
    
    /**
     * @return personAttributesDtos - the key to the HashMap is the name of the attribute, the value 
     *                            is the actual DTO object 
     */
    public HashMap<String, PersonAttributeDTO> getPersonAttributesDtos() {
        return this.personAttributesDtos;
    }

    /**
     * @param personAttributesDtos - the key to the HashMap is the name of the attribute, the value 
     *                           is the actual DTO object
     */
    public void setPersonAttributesDtos(HashMap<String, PersonAttributeDTO> personAttributes) {
        this.personAttributesDtos = personAttributes;
    }
}
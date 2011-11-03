/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kim.framework.identity.citizenship

import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.krad.bo.ExternalizableBusinessObject
import org.kuali.rice.kim.api.identity.CodedAttribute

class EntityCitizenshipStatusEbo implements CodedAttributeContract, ExternalizableBusinessObject {
    String code;
    String name;
    boolean active;
    String sortCode;
    Long versionNumber;
	String objectId;


    /**
   * Converts a mutable EntityCitizenshipStatusEbo to an immutable EntityCitizenshipStatus representation.
   * @param bo
   * @return an immutable EntityCitizenshipStatus
   */
  static CodedAttribute to(EntityCitizenshipStatusEbo bo) {
    if (bo == null) { return null }
    return CodedAttribute.Builder.create(bo).build()
  }

  /**
   * Creates a EntityCitizenshipStatusEbo business object from an immutable representation of a EntityCitizenshipStatus.
   * @param an immutable EntityCitizenshipStatus
   * @return a EntityCitizenshipStatusEbo
   */
  static EntityCitizenshipStatusEbo from(CodedAttribute immutable) {
    if (immutable == null) {return null}

    EntityCitizenshipStatusEbo bo = new EntityCitizenshipStatusEbo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }

    void refresh() { }
}

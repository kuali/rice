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
package org.kuali.rice.kim.framework.identity.name

import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.krad.bo.ExternalizableBusinessObject
import org.kuali.rice.kim.api.identity.CodedAttribute


class EntityNameTypeEbo implements CodedAttributeContract, ExternalizableBusinessObject {
    String code
    String name
    boolean active
    String sortCode
    Long versionNumber
	String objectId


    /**
   * Converts a mutable AddressTypeBo to an immutable AddressType representation.
   * @param bo
   * @return an immutable AddressType
   */
  static CodedAttribute to(EntityNameTypeEbo bo) {
    if (bo == null) { return null }
    return CodedAttribute.Builder.create(bo).build()
  }

  /**
   * Creates a AddressType business object from an immutable representation of a AddressType.
   * @param an immutable AddressType
   * @return a AddressTypeBo
   */
  static EntityNameTypeEbo from(CodedAttribute immutable) {
    if (immutable == null) {return null}

    EntityNameTypeEbo bo = new EntityNameTypeEbo()
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

/*
 * Copyright 2006-2011 The Kuali Foundation
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







package org.kuali.rice.shareddata.framework.country

import org.kuali.rice.krad.bo.ExternalizableBusinessObject
import org.kuali.rice.krad.bo.Inactivatable
import org.kuali.rice.shareddata.api.country.Country
import org.kuali.rice.shareddata.api.country.CountryContract

//@ToString
class CountryEbo implements Inactivatable, CountryContract, ExternalizableBusinessObject {
  def String code;
  def String alternateCode;
  def String name;
  def boolean restricted;
  def boolean active;
  def Long versionNumber

  /**
   * Converts a mutable CountryEbo to an immutable Country representation.
   * @param bo
   * @return an immutable Country
   */
  static Country to(CountryEbo bo) {
    if (bo == null) { return null }
    return Country.Builder.create(bo).build()
  }

  /**
   * Creates a CountryEbo business object from an immutable representation of a Country.
   * @param an immutable Country
   * @return a CountryEbo
   */
  static CountryEbo from(Country immutable) {
    if (immutable == null) {return null}

    CountryEbo bo = new CountryEbo()
    bo.code = immutable.code
    bo.alternateCode = immutable.alternateCode
    bo.name = immutable.name
    bo.restricted = immutable.restricted
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber

    return bo;
  }

  void refresh() { }
}

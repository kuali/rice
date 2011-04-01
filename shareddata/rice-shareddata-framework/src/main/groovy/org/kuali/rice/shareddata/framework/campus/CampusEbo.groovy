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



package org.kuali.rice.shareddata.framework.campus

import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.shareddata.api.campus.CampusContract

public class CampusEbo implements Inactivateable, CampusContract, ExternalizableBusinessObject {
	private static final long serialVersionUID = 787567094298971223L;

	def String code;
	def String name;
	def String shortName;
	def String campusTypeCode;
	def boolean active;
	def CampusTypeEbo campusType;
    def Long versionNumber;
	def String objectId;
	
	@Override
	CampusTypeEbo getCampusType() {
		return campusType
	}
	
	/**
	* Converts a mutable bo to its immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static org.kuali.rice.shareddata.api.campus.Campus to(CampusEbo bo) {
	   if (bo == null) {
		   return null
	   }

	   return org.kuali.rice.shareddata.api.campus.Campus.Builder.create(bo).build();
   }

   /**
	* Converts a immutable object to its mutable counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static CampusEbo from(org.kuali.rice.shareddata.api.campus.Campus im) {
	   if (im == null) {
		   return null
	   }

	   CampusEbo bo = new CampusEbo()
	   bo.code = im.code
	   bo.name = im.name
	   bo.shortName = im.shortName
	   bo.active = im.active
	   bo.campusTypeCode = im.campusType.code
 
	   bo.campusType = CampusTypeEbo.from(im.campusType)
       bo.versionNumber = im.versionNumber
	   bo.objectId = im.objectId;

	   return bo
   }
   
   void refresh() { }

}
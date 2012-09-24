--
-- Copyright 2005-2012 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

--
--     KULRICE-7799	 Create a separate permission for accessing the new super user tab
--

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES((SELECT (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and regexp_like(KIM_TYP_ATTR_ID, '^[1-9][0-9]{0,3}$')),
           sys_guid(), 1, 'Document Type, Routing Node and Action Event', 'documentTypeAndNodeAndActionEventService', 'Y', 'KR-SYS')
/

INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y',
  (SELECT KIM_TYP_ID FROM KRIM_TYP_T where NM = 'Document Type, Routing Node and Action Event' and SRVC_NM = 'documentTypeAndNodeAndActionEventService'), 'Administer Routing for Document', 'KR-NS', sys_guid(),
  (SELECT (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and regexp_like(perm_tmpl_id, '^[1-9][0-9]{0,3}$')), 1)
/
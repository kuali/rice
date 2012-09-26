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
--    KULRICE-8300 & KULRICE-7799
--

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES((SELECT (max(to_number(KIM_TYP_ID)) + 1) from KRIM_TYP_T where KIM_TYP_ID is not NULL and regexp_like(KIM_TYP_ID, '^[1-9][0-9]{0,3}$')),
           sys_guid(), 1, 'Document Type, Route Node, and Route Status', 'documentTypeAndNodeAndRouteStatusPermissionTypeService', 'Y', 'KR-SYS')
/

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES((SELECT (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and regexp_like(KIM_TYP_ATTR_ID, '^[1-9][0-9]{0,3}$')),
            sys_guid(), 1, 'a',
            (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and NMSPC_CD = 'KR-SYS'),
            (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NM = 'documentTypeName' and NMSPC_CD = 'KR-WKFLW'), 'Y')
/

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES((SELECT (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and regexp_like(KIM_TYP_ATTR_ID, '^[1-9][0-9]{0,3}$')),
            sys_guid(), 1, 'b',
            (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and NMSPC_CD = 'KR-SYS'),
            (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NM = 'routeNodeName' and NMSPC_CD = 'KR-WKFLW'), 'Y')
/

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES((SELECT (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and regexp_like(KIM_TYP_ATTR_ID, '^[1-9][0-9]{0,3}$')),
            sys_guid(), 1, 'c',
            (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and NMSPC_CD = 'KR-SYS'),
            (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NM = 'routeStatusCode' and NMSPC_CD = 'KR-WKFLW'), 'Y')
/

INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y',
  (SELECT KIM_TYP_ID FROM KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and SRVC_NM = 'documentTypeAndNodeAndRouteStatusPermissionTypeService'),
  'Super User Approve Single Action Request', 'KR-WKFLW', sys_guid(),
  (SELECT (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and regexp_like(perm_tmpl_id, '^[1-9][0-9]{0,3}$')), 1)
/

INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y',
  (SELECT KIM_TYP_ID FROM KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and SRVC_NM = 'documentTypeAndNodeAndRouteStatusPermissionTypeService'),
  'Super User Approve Document', 'KR-WKFLW', sys_guid(),
  (SELECT (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and regexp_like(perm_tmpl_id, '^[1-9][0-9]{0,3}$')), 1)
/

INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y',
  (SELECT KIM_TYP_ID FROM KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and SRVC_NM = 'documentTypeAndNodeAndRouteStatusPermissionTypeService'),
  'Super User Disapprove Document', 'KR-WKFLW', sys_guid(),
  (SELECT (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and regexp_like(perm_tmpl_id, '^[1-9][0-9]{0,3}$')), 1)
/
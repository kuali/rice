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

INSERT INTO KRCR_NMSPC_T VALUES ('KR-KRAD', sys_guid(), 1, 'Kuali Rapid Application Development', 'Y', 'RICE');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ('47', sys_guid(), 1, 'viewId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ('48', sys_guid(), 1, 'actionEvent', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ('49', sys_guid(), 1, 'collectionPropertyName', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ('50', sys_guid(), 1, 'fieldId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ('51', sys_guid(), 1, 'groupId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ('52', sys_guid(), 1, 'widgetId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ('53', sys_guid(), 1, 'actionId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_TYP_T VALUES ('69', sys_guid(), 1, 'View', 'viewPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ('70', sys_guid(), 1, 'View Edit Mode', 'viewEditModePermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ('71', sys_guid(), 1, 'View Field', 'viewFieldPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ('72', sys_guid(), 1, 'View Group', 'viewGroupPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ('73', sys_guid(), 1, 'View Widget', 'viewWidgetPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ('74', sys_guid(), 1, 'View Action', 'viewActionPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ('75', sys_guid(), 1, 'View Line Field', 'viewLineFieldPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ('76', sys_guid(), 1, 'View Line Action', 'viewLineActionPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('113', sys_guid(), 1, 'a', '69', '47', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('114', sys_guid(), 1, 'a', '70', '47', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('115', sys_guid(), 1, 'b', '70', '10', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('116', sys_guid(), 1, 'a', '71', '47', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('117', sys_guid(), 1, 'b', '71', '50', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('118', sys_guid(), 1, 'c', '71', '6', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('119', sys_guid(), 1, 'a', '72', '47', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('120', sys_guid(), 1, 'b', '72', '51', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('121', sys_guid(), 1, 'c', '72', '49', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('122', sys_guid(), 1, 'a', '73', '47', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('123', sys_guid(), 1, 'b', '73', '52', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('124', sys_guid(), 1, 'a', '74', '47', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('125', sys_guid(), 1, 'b', '74', '53', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('126', sys_guid(), 1, 'c', '74', '48', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('127', sys_guid(), 1, 'a', '75', '47', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('128', sys_guid(), 1, 'b', '75', '51', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('129', sys_guid(), 1, 'c', '75', '49', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('130', sys_guid(), 1, 'd', '75', '50', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('131', sys_guid(), 1, 'e', '75', '6', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('132', sys_guid(), 1, 'a', '76', '47', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('133', sys_guid(), 1, 'b', '76', '51', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('134', sys_guid(), 1, 'c', '76', '49', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('135', sys_guid(), 1, 'd', '76', '53', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ('136', sys_guid(), 1, 'e', '76', '48', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10003', sys_guid(), 1, 'KR-KRAD', 'Open View', null, '69', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10004', sys_guid(), 1, 'KR-KRAD', 'Edit View', null, '69', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10005', sys_guid(), 1, 'KR-KRAD', 'Use View', null, '70', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10006', sys_guid(), 1, 'KR-KRAD', 'View Field', null, '71', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10007', sys_guid(), 1, 'KR-KRAD', 'Edit Field', null, '71', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10008', sys_guid(), 1, 'KR-KRAD', 'View Group', null, '72', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10009', sys_guid(), 1, 'KR-KRAD', 'Edit Group', null, '72', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10010', sys_guid(), 1, 'KR-KRAD', 'View Widget', null, '73', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10011', sys_guid(), 1, 'KR-KRAD', 'Edit Widget', null, '73', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10012', sys_guid(), 1, 'KR-KRAD', 'Perform Action', null, '74', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10013', sys_guid(), 1, 'KR-KRAD', 'View Line', null, '72', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10014', sys_guid(), 1, 'KR-KRAD', 'Edit Line', null, '72', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10015', sys_guid(), 1, 'KR-KRAD', 'View Line Field', null, '75', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10016', sys_guid(), 1, 'KR-KRAD', 'Edit Line Field', null, '75', 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ('10017', sys_guid(), 1, 'KR-KRAD', 'Perform Line Action', null, '76', 'Y');


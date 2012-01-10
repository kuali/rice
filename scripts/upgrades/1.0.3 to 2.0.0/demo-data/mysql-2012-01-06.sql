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
-- Adds some permissions and a role for testing KIM authorization in the sample app
--

INSERT INTO KRIM_ROLE_T VALUES ('10003', uuid(), 1, 'Sample App Admin', 'KR-SAP', 'Test role for the sample app', '1', 'Y', now());

INSERT INTO KRIM_ROLE_MBR_T VALUES ('10003', 1, uuid(), '10003', 'dev1', 'P', null, null, now());

INSERT INTO KRIM_ROLE_T VALUES ('10004', uuid(), 1, 'Sample App Users', 'KR-SAP', 'Test role for the sample app', '1', 'Y', now());

INSERT INTO KRIM_ROLE_MBR_T VALUES ('10004', 1, uuid(), '10004', 'admin', 'P', null, null, now());

INSERT INTO KRIM_ROLE_MBR_T VALUES ('10005', 1, uuid(), '10004', 'dev1', 'P', null, null, now());

INSERT INTO KRIM_PERM_T VALUES ('10003', uuid(), 1, '10008', 'KR-SAP', 'View Kitchen Sink Group', 'Allows users to view the group in kitchen sink page 9.', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('882', uuid(), 1, '10003', '72', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('883', uuid(), 1, '10003', '72', '51', 'UifCompView-SecureGroupView');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1003', uuid(), 1, '10003', '10003', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10004', uuid(), 1, '10008', 'KR-SAP', 'View Kitchen Sink Page', 'Allows users to view page 9 in the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('884', uuid(), 1, '10004', '72', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('885', uuid(), 1, '10004', '72', '51', 'UifCompView-Page9');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1004', uuid(), 1, '10004', '10004', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10005', uuid(), 1, '10009', 'KR-SAP', 'Edit Kitchen Sink Group', 'Allows users to edit the group in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('886', uuid(), 1, '10005', '72', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('887', uuid(), 1, '10005', '72', '51', 'UifCompView-SecureGroupEdit');


INSERT INTO KRIM_ROLE_PERM_T VALUES ('1005', uuid(), 1, '10003', '10005', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10006', uuid(), 1, '10006', 'KR-SAP', 'View Kitchen Sink Field', 'Allows users to view the field in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('888', uuid(), 1, '10006', '71', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('889', uuid(), 1, '10006', '71', '6', 'field6');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1006', uuid(), 1, '10003', '10006', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10007', uuid(), 1, '10007', 'KR-SAP', 'Edit Kitchen Sink Field', 'Allows users to edit the field in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('890', uuid(), 1, '10007', '71', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('891', uuid(), 1, '10007', '71', '6', 'field7');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1007', uuid(), 1, '10003', '10007', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10008', uuid(), 1, '10006', 'KR-SAP', 'View Kitchen Sink Field Group', 'Allows users to view the field group in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('892', uuid(), 1, '10008', '71', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('893', uuid(), 1, '10008', '71', '50', 'UifCompView-SecureFieldGroup1');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1008', uuid(), 1, '10003', '10008', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10009', uuid(), 1, '10007', 'KR-SAP', 'Edit Kitchen Sink Field Group', 'Allows users to edit the field group in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('894', uuid(), 1, '10009', '71', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('895', uuid(), 1, '10009', '71', '50', 'UifCompView-SecureFieldGroup2');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1009', uuid(), 1, '10003', '10009', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10010', uuid(), 1, '10012', 'KR-SAP', 'Perform Kitchen Sink Action', 'Allows users to perform the save action in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('896', uuid(), 1, '10010', '74', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('897', uuid(), 1, '10010', '74', '48', 'save');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1010', uuid(), 1, '10003', '10010', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10011', uuid(), 1, '10010', 'KR-SAP', 'View Kitchen Sink Widget', 'Allows users to view the quickfinder widget in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('898', uuid(), 1, '10011', '73', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('899', uuid(), 1, '10011', '73', '52', 'UifCompView-SecureWidget');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1011', uuid(), 1, '10003', '10011', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10012', uuid(), 1, '10013', 'KR-SAP', 'View Kitchen Sink Line', 'Allows users to view the collection line in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('900', uuid(), 1, '10012', '72', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('901', uuid(), 1, '10012', '72', '49', 'list1');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1012', uuid(), 1, '10003', '10012', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10013', uuid(), 1, '10014', 'KR-SAP', 'Edit Kitchen Sink Line', 'Allows users to edit the collection line in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('902', uuid(), 1, '10013', '72', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('903', uuid(), 1, '10013', '72', '49', 'list2');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1013', uuid(), 1, '10003', '10013', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10014', uuid(), 1, '10015', 'KR-SAP', 'View Kitchen Sink Line Field', 'Allows users to view the collection line field in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('904', uuid(), 1, '10014', '75', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('905', uuid(), 1, '10014', '75', '49', 'list3');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('906', uuid(), 1, '10014', '75', '6', 'field2');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1014', uuid(), 1, '10003', '10014', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10015', uuid(), 1, '10016', 'KR-SAP', 'Edit Kitchen Sink Line Field', 'Allows users to edit the collection line field in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('907', uuid(), 1, '10015', '75', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('908', uuid(), 1, '10015', '75', '49', 'list3');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('909', uuid(), 1, '10015', '75', '6', 'field3');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1015', uuid(), 1, '10003', '10015', 'Y');

INSERT INTO KRIM_PERM_T VALUES ('10016', uuid(), 1, '10017', 'KR-SAP', 'Perform Kitchen Sink Line Action', 'Allows users to perform the delete line action in page 9 of the kitchen sink', 'Y');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('910', uuid(), 1, '10016', '76', '47', 'UifCompView*');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('911', uuid(), 1, '10016', '76', '49', 'list4');

INSERT INTO KRIM_PERM_ATTR_DATA_T VALUES ('912', uuid(), 1, '10016', '76', '48', 'delete');

INSERT INTO KRIM_ROLE_PERM_T VALUES ('1016', uuid(), 1, '10003', '10016', 'Y');


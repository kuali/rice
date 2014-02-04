--
-- Copyright 2005-2014 The Kuali Foundation
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

-- KULRICE-11268 Travel Demo App maintenance document security not defined.
--

INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Allows a user to receive ad hoc requests for Travel Documents.','Ad Hoc Review Travel Document','KR-SAP',sys_guid(),'KRSAP10017','9',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Allows users to open Travel Documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node).','Administer Routing Travel Document','KR-SAP',sys_guid(),'KRSAP10018','3',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Allows access to the Blanket Approval button on Travel Documents.','Blanket Approve Travel Document','KR-SAP',sys_guid(),'KRSAP10019','4',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Authorizes the initiation of Travel Documents.','Initiate Travel Document','KR-SAP',sys_guid(),'KRSAP10020','10',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Authorizes users to copy Travel Documents.','Copy Travel Document','KR-SAP',sys_guid(),'KRSAP10021','2',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Allows users to access Kuali Travel inquiries.','Inquire Into Travel Records','KR-SAP',sys_guid(),'KRSAP10022','24',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Allow users to access Kuali Travel lookups.','Look Up Travel Records','KR-SAP',sys_guid(),'KRSAP10023','23',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Authorizes users to open Travel Documents.','Open Travel Document','KR-SAP',sys_guid(),'KRSAP10024','40',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Allows users to access all Travel screens.','Use all Travel Screen','KR-SAP',sys_guid(),'KRSAP10025','29',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Authorization to delete notes and attachments on Travel Documents .','Delete Note / Attachment Kuali Document','KR-SAP',sys_guid(),'KRSAP10027','47',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Users who can save Travel documents','Save Travel Document','KR-SAP',sys_guid(),'KRSAP10028','15',1)
/
INSERT INTO KRIM_PERM_T (ACTV_IND,NM,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','Recall Document','KR-SAP',sys_guid(),'KRSAP10029','68',1)
/

INSERT INTO KRIM_TYP_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,SRVC_NM,VER_NBR)
  VALUES ('Y','KRSAP3','Document Type (Permission)','KR-SAP',sys_guid(),'documentTypePermissionTypeService',1)
/
INSERT INTO KRIM_TYP_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,SRVC_NM,VER_NBR)
  VALUES ('Y','KRSAP5','Ad Hoc Review','KR-SAP',sys_guid(),'adhocReviewPermissionTypeService',1)
/
INSERT INTO KRIM_TYP_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,SRVC_NM,VER_NBR)
  VALUES ('Y','KRSAP8','Document Type & Routing Node or State','KR-SAP',sys_guid(),'documentTypeAndNodeOrStatePermissionTypeService',1)
/ 
INSERT INTO KRIM_TYP_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,SRVC_NM,VER_NBR)
  VALUES ('Y','KRSAP10','Namespace or Component','KR-SAP',sys_guid(),'namespaceOrComponentPermissionTypeService',1)
/
INSERT INTO KRIM_TYP_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,SRVC_NM,VER_NBR)
  VALUES ('Y','KRSAP12','Namespace or Action','KR-SAP',sys_guid(),'namespaceOrActionPermissionTypeService',1)
/
INSERT INTO KRIM_TYP_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,SRVC_NM,VER_NBR)
  VALUES ('Y','KRSAP59','Document Type & Relationship to Note Author','KR-SAP',sys_guid(),'documentTypeAndRelationshipToNoteAuthorPermissionTypeService',1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP913','TravelDocument','13', 'KRSAP5',sys_guid(),'KRSAP10017',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP914','TravelDocument','13','KRSAP3',sys_guid(),'KRSAP10018',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP915','TravelDocument','13','KRSAP3',sys_guid(),'KRSAP10019',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP916','TravelDocument','13','KRSAP3',sys_guid(),'KRSAP10020',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP917','TravelDocument','13','KRSAP3',sys_guid(),'KRSAP10021',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP918','TravelDocument','13','KRSAP3',sys_guid(),'KRSAP10024',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP920','TravelDocument','13', 'KRSAP59',sys_guid(),'KRSAP10027',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP919','TravelDocument','13', 'KRSAP8',sys_guid(),'KRSAP10028',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP921','TravelDocument','13', 'KRSAP8',sys_guid(),'KRSAP10029',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP922','KR*','4', 'KRSAP10',sys_guid(),'KRSAP10022',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP923','KR*','4', 'KRSAP10',sys_guid(),'KRSAP10023',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP924','KR*','4', 'KRSAP12',sys_guid(),'KRSAP10025',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,PERM_ID,VER_NBR)
  VALUES ('KRSAP925','false','8', 'KRSAP59',sys_guid(),'KRSAP10027',1)
/


INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10017','1','KRSAP10017',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10018','63','KRSAP10018',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10019','63','KRSAP10019',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10020','1','KRSAP10020',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10021','1','KRSAP10021',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10022','1','KRSAP10022',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10023','1','KRSAP10023',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10024','1','KRSAP10024',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10028','60','KRSAP10028',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10021','95','KRSAP10030',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10027','63','KRSAP10027',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10028','90','KRSAP10031',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10022','1','KRSAP10032',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10023','1','KRSAP10033',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10025','1','KRSAP10025',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y',sys_guid(),'KRSAP10029','60','KRSAP10029',1)
/
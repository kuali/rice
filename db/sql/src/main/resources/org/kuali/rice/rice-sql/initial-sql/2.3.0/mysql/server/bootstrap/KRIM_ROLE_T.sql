--
-- Copyright 2005-2017 The Kuali Foundation
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

INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from the users in the Principal table. This role gives users high-level permissions to interact with RICE documents and to login to KUALI.','2',STR_TO_DATE( '20081104143710', '%Y%m%d%H%i%s' ),'KUALI','5ADF18B6D4847954E0404F8189D85002','1','User',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with that have received an action request for a given document.','42',STR_TO_DATE( '20081114141017', '%Y%m%d%H%i%s' ),'KR-WKFLW','5BABFACC4F62A8EEE0404F8189D8770F','59','Approve Request Recipient',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from the initiator listed within the route log of a given document.','43',STR_TO_DATE( '20081114141017', '%Y%m%d%H%i%s' ),'KR-WKFLW','5BABFACC4F63A8EEE0404F8189D8770F','60','Initiator',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from the initiator and action request recipients listed within the route log of a given document.','43',STR_TO_DATE( '20081114141017', '%Y%m%d%H%i%s' ),'KR-WKFLW','5BABFACC4F64A8EEE0404F8189D8770F','61','Initiator or Reviewer',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role can take superuser actions and blanket approve RICE documents as well as being able to modify and assign permissions, responsibilities and roles belonging to the KR namespaces.','1',STR_TO_DATE( '20081108115522', '%Y%m%d%H%i%s' ),'KR-SYS','5B31640F0105ADF1E0404F8189D84647','63','Technical Administrator',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with the Edit Document permission for a given document type.,','45',STR_TO_DATE( '20081114141017', '%Y%m%d%H%i%s' ),'KR-NS','5BABFACC4F61A8EEE0404F8189D8770F','66','Document Editor',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from the user who took the Complete action on a given document.','43',STR_TO_DATE( '20081114141017', '%Y%m%d%H%i%s' ),'KR-WKFLW','5BABFACC4F65A8EEE0404F8189D8770F','67','Router',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with the Open Document permission for a given document type.,','60',STR_TO_DATE( '20090113192616', '%Y%m%d%H%i%s' ),'KR-NS','606763510FBF96D3E0404F8189D857A2','83','Document Opener',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with an acknowledge action request in the route log of a given document.','42',STR_TO_DATE( '20090121130202', '%Y%m%d%H%i%s' ),'KR-WKFLW','6102F3FA08CE45CFE0404F8189D8317E','88','Acknowledge Request Recipient',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with an FYI action request in the route log of a given document.','42',STR_TO_DATE( '20090121130203', '%Y%m%d%H%i%s' ),'KR-WKFLW','6102F3FA08CF45CFE0404F8189D8317E','89','FYI Request Recipient',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role represents the KR System User, that is the user ID RICE uses when it takes programmed actions.','1',STR_TO_DATE( '20090821035258', '%Y%m%d%H%i%s' ),'KR-SYS','61815E6C62D0B647E0404F8189D873B3','90','System User',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with the Initiate Document permission for a given document type.','66',STR_TO_DATE( '20090821035258', '%Y%m%d%H%i%s' ),'KR-SYS','67F145466E8B9160E0404F8189D86771','95','Document Initiator',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with an Approval action request (that was not generated via the ad-hoc recipients tab) in the route log of a given document.','42',STR_TO_DATE( '20090821035258', '%Y%m%d%H%i%s' ),'KR-WKFLW','67F145466EB09160E0404F8189D86771','97','Non-Ad Hoc Approve Request Recipient',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role maintains KRMS agendas and rules.','1',STR_TO_DATE( '20120131160404', '%Y%m%d%H%i%s' ),'KR-RULE','B7DBFABEFD2D8CBFE0402E0AA9D757C9','98','Kuali Rules Management System Administrator',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with the Route Document permission for a given document type.','76',STR_TO_DATE( '20120412212324', '%Y%m%d%H%i%s' ),'KR-WKFLW','BD82F716EFEFF706E0402E0AA9D70A27','99','Document Router',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role is used for no login guest users.','1',STR_TO_DATE( '20121130172357', '%Y%m%d%H%i%s' ),'KUALI','CFBAA6F5F0D25D07E0406E0AC31D229B','KR1000','GuestRole',1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','This role derives its members from users with an complete action request in the route log of a given document.','42','KR-WKFLW','D0C2892F47ED9142E0406E0AC31D23D6','KR1001','Complete Request Recipient',1)
/

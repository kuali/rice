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

TRUNCATE TABLE KREW_ACTN_ITM_T
/
INSERT INTO KREW_ACTN_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,ROLE_NM,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10222','2366',STR_TO_DATE( '20081222132636', '%Y%m%d%H%i%s' ),'http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2695','Travel Doc 2 - dfads','Travel Request','TravelRequest','director','director director','K','2028',1)
/
INSERT INTO KREW_ACTN_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10224','2367',STR_TO_DATE( '20090317101441', '%Y%m%d%H%i%s' ),'http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2701','Travel Request - test','Travel Request','TravelRequest','user4','A','2022',1)
/
INSERT INTO KREW_ACTN_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DLGN_PRNCPL_ID,DLGN_TYP,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10225','2368',STR_TO_DATE( '20090317101441', '%Y%m%d%H%i%s' ),'user4','S','http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2701','Travel Request - test','Travel Request','TravelRequest','user2','A','2061',1)
/

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

TRUNCATE TABLE KREW_OUT_BOX_ITM_T DROP STORAGE
/
INSERT INTO KREW_OUT_BOX_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10040','2360',TO_DATE( '20081222132425', 'YYYYMMDDHH24MISS' ),'http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2695','Travel Doc 2 - dfads','Travel Request','TravelRequest','user4','A','2022',1)
/
INSERT INTO KREW_OUT_BOX_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,ROLE_NM,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10041','2362',TO_DATE( '20081222132605', 'YYYYMMDDHH24MISS' ),'http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2695','Travel Doc 2 - dfads','Travel Request','TravelRequest','employee','employee employee','A','2024',1)
/
INSERT INTO KREW_OUT_BOX_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,ROLE_NM,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10042','2364',TO_DATE( '20081222132636', 'YYYYMMDDHH24MISS' ),'http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2695','Travel Doc 2 - dfads','Travel Request','TravelRequest','supervisor','supervisr supervisr','A','2026',1)
/

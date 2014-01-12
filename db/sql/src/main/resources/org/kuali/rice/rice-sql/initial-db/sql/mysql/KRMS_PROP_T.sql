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

TRUNCATE TABLE KRMS_PROP_T
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is campus bloomington','S','T1000','T1000',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is campus bloomington','S','T1001','T1001',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is campus bloomington','S','T1002','T1002',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is campus bloomington','S','T1003','T1003',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is campus bloomington','S','T1004','T1004',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is campus bloomington','S','T1005','T1005',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is campus bloomington','S','T1006','T1006',1)
/
INSERT INTO KRMS_PROP_T (CMPND_OP_CD,DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('&','a compound prop','C','T1007','T1007',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('a simple child to a compound prop','S','T1008','T1007',1)
/
INSERT INTO KRMS_PROP_T (CMPND_SEQ_NO,DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES (2,'2nd simple child to a compound prop ','S','T1009','T1007',1)
/
INSERT INTO KRMS_PROP_T (CMPND_SEQ_NO,DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES (3,'3nd simple child to a compound prop ','S','T1010','T1007',1)
/
INSERT INTO KRMS_PROP_T (CMPND_OP_CD,DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('&','is purchase special','C','T1011','T1008',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is purchase order value large','S','T1012','T1008',1)
/
INSERT INTO KRMS_PROP_T (CMPND_OP_CD,DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('|','is purchased item controlled','C','T1013','T1008',1)
/
INSERT INTO KRMS_PROP_T (CMPND_OP_CD,DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('&','is it for a special event','C','T1014','T1008',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is item purchased animal','S','T1015','T1008',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('is purchased item radioactive','S','T1016','T1008',1)
/
INSERT INTO KRMS_PROP_T (CMPND_SEQ_NO,DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES (3,'is it medicinal','S','T1017','T1008',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('charged to Kuali','S','T1018','T1008',1)
/
INSERT INTO KRMS_PROP_T (DESC_TXT,DSCRM_TYP_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Party at Travis House','S','T1019','T1008',1)
/

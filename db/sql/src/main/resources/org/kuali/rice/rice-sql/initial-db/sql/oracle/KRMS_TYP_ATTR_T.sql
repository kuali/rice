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

TRUNCATE TABLE KRMS_TYP_ATTR_T DROP STORAGE
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','1000',1,'1000','1000',0)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','1000',1,'1001','1001',0)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','1001',1,'1002','1002',1)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','1004',2,'1005','1003',1)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','1005',3,'1006','1003',1)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','1006',3,'1007','1000',1)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','1006',3,'1008','1001',1)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','T1000',1,'T1000','T1003',1)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','T1002',2,'T1001','T1005',0)
/
INSERT INTO KRMS_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','T1003',1,'T1002','T1005',0)
/

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

TRUNCATE TABLE KREW_ATTR_DEFN_T
/
INSERT INTO KREW_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,CMPNT_NM,LBL,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','1','edu.sampleu.travel.bo.TravelAccount','Travel Number','number','KR-SAP',1)
/
INSERT INTO KREW_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,CMPNT_NM,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','2','edu.sampleu.travel.bo.FiscalOfficer','id','KR-SAP',1)
/

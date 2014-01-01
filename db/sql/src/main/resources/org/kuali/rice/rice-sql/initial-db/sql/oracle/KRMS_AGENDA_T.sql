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

TRUNCATE TABLE KRMS_AGENDA_T DROP STORAGE
/
INSERT INTO KRMS_AGENDA_T (ACTV,AGENDA_ID,CNTXT_ID,INIT_AGENDA_ITM_ID,NM,VER_NBR)
  VALUES ('Y','T1000','CONTEXT1','T1000','My Fabulous Agenda',1)
/
INSERT INTO KRMS_AGENDA_T (ACTV,AGENDA_ID,CNTXT_ID,INIT_AGENDA_ITM_ID,NM,TYP_ID,VER_NBR)
  VALUES ('Y','T1001','CONTEXT1','T1007','SimpleAgendaCompoundProp','T1004',1)
/
INSERT INTO KRMS_AGENDA_T (ACTV,AGENDA_ID,CNTXT_ID,INIT_AGENDA_ITM_ID,NM,TYP_ID,VER_NBR)
  VALUES ('Y','T1002','CONTEXT1','T1008','One Big Rule','T1004',1)
/

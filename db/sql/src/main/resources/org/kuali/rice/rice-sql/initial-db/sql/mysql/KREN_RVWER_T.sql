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

TRUNCATE TABLE KREN_RVWER_T
/
INSERT INTO KREN_RVWER_T (CHNL_ID,PRNCPL_ID,RVWER_ID,TYP,VER_NBR)
  VALUES (1,'RiceTeam',1,'GROUP',1)
/
INSERT INTO KREN_RVWER_T (CHNL_ID,PRNCPL_ID,RVWER_ID,TYP,VER_NBR)
  VALUES (5,'testuser3',2,'USER',1)
/
INSERT INTO KREN_RVWER_T (CHNL_ID,PRNCPL_ID,RVWER_ID,TYP,VER_NBR)
  VALUES (5,'TestGroup1',3,'GROUP',1)
/

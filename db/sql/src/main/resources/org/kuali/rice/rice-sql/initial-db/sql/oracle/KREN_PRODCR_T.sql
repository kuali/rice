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

TRUNCATE TABLE KREN_PRODCR_T DROP STORAGE
/
INSERT INTO KREN_PRODCR_T (CNTCT_INFO,DESC_TXT,NM,PRODCR_ID,VER_NBR)
  VALUES ('kuali-ken-testing@cornell.edu','This producer represents messages sent from the general message sending forms.','Notification System',1,1)
/
INSERT INTO KREN_PRODCR_T (CNTCT_INFO,DESC_TXT,NM,PRODCR_ID,VER_NBR)
  VALUES ('kuali-ken-testing@cornell.edu','This producer represents messages sent from the University Library system.','University Library System',2,1)
/
INSERT INTO KREN_PRODCR_T (CNTCT_INFO,DESC_TXT,NM,PRODCR_ID,VER_NBR)
  VALUES ('kuali-ken-testing@cornell.edu','This producer represents messages sent from the University Events system.','University Events Office',3,1)
/

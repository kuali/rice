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

TRUNCATE TABLE KREN_CHNL_T
/
INSERT INTO KREN_CHNL_T (CHNL_ID,DESC_TXT,NM,SUBSCRB_IND,VER_NBR)
  VALUES (1,'This channel is used for sending out information about the Kuali Rice effort.','Kuali Rice Channel','Y',1)
/
INSERT INTO KREN_CHNL_T (CHNL_ID,DESC_TXT,NM,SUBSCRB_IND,VER_NBR)
  VALUES (2,'This channel is used for sending out information about Library Events.','Library Events Channel','Y',1)
/
INSERT INTO KREN_CHNL_T (CHNL_ID,DESC_TXT,NM,SUBSCRB_IND,VER_NBR)
  VALUES (3,'This channel is used for sending out information about your overdue books.','Overdue Library Books','N',1)
/
INSERT INTO KREN_CHNL_T (CHNL_ID,DESC_TXT,NM,SUBSCRB_IND,VER_NBR)
  VALUES (4,'This channel broadcasts any concerts coming to campus.','Concerts Coming to Campus','Y',1)
/
INSERT INTO KREN_CHNL_T (CHNL_ID,DESC_TXT,NM,SUBSCRB_IND,VER_NBR)
  VALUES (5,'This channel broadcasts general announcements for the university.','University Alerts','N',1)
/
INSERT INTO KREN_CHNL_T (CHNL_ID,DESC_TXT,NM,SUBSCRB_IND,VER_NBR)
  VALUES (500,'Builtin channel for KEW','KEW','N',1)
/

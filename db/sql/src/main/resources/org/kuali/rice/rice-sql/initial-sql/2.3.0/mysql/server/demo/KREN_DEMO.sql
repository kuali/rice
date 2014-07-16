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

# -----------------------------------------------------------------------
# KREN_CHNL_T
# -----------------------------------------------------------------------
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

# -----------------------------------------------------------------------
# KREN_PRODCR_T
# -----------------------------------------------------------------------
INSERT INTO KREN_PRODCR_T (CNTCT_INFO,DESC_TXT,NM,PRODCR_ID,VER_NBR)
  VALUES ('kuali-ken-testing@cornell.edu','This producer represents messages sent from the general message sending forms.','Notification System',1,1)
/
INSERT INTO KREN_PRODCR_T (CNTCT_INFO,DESC_TXT,NM,PRODCR_ID,VER_NBR)
  VALUES ('kuali-ken-testing@cornell.edu','This producer represents messages sent from the University Library system.','University Library System',2,1)
/
INSERT INTO KREN_PRODCR_T (CNTCT_INFO,DESC_TXT,NM,PRODCR_ID,VER_NBR)
  VALUES ('kuali-ken-testing@cornell.edu','This producer represents messages sent from the University Events system.','University Events Office',3,1)
/

# -----------------------------------------------------------------------
# KREN_CHNL_PRODCR_T
# -----------------------------------------------------------------------
INSERT INTO KREN_CHNL_PRODCR_T (CHNL_ID,PRODCR_ID)
  VALUES (1,1)
/
INSERT INTO KREN_CHNL_PRODCR_T (CHNL_ID,PRODCR_ID)
  VALUES (2,1)
/
INSERT INTO KREN_CHNL_PRODCR_T (CHNL_ID,PRODCR_ID)
  VALUES (2,2)
/
INSERT INTO KREN_CHNL_PRODCR_T (CHNL_ID,PRODCR_ID)
  VALUES (3,1)
/
INSERT INTO KREN_CHNL_PRODCR_T (CHNL_ID,PRODCR_ID)
  VALUES (3,2)
/
INSERT INTO KREN_CHNL_PRODCR_T (CHNL_ID,PRODCR_ID)
  VALUES (4,1)
/
INSERT INTO KREN_CHNL_PRODCR_T (CHNL_ID,PRODCR_ID)
  VALUES (4,3)
/
INSERT INTO KREN_CHNL_PRODCR_T (CHNL_ID,PRODCR_ID)
  VALUES (5,1)
/

# -----------------------------------------------------------------------
# KREN_CHNL_SUBSCRP_T
# -----------------------------------------------------------------------
INSERT INTO KREN_CHNL_SUBSCRP_T (CHNL_ID,CHNL_SUBSCRP_ID,PRNCPL_ID)
  VALUES (1,1,'testuser4')
/
INSERT INTO KREN_CHNL_SUBSCRP_T (CHNL_ID,CHNL_SUBSCRP_ID,PRNCPL_ID)
  VALUES (1,1000,'admin')
/

# -----------------------------------------------------------------------
# KREN_RECIP_DELIV_T
# -----------------------------------------------------------------------
INSERT INTO KREN_RECIP_DELIV_T (CHNL,NM,RECIP_DELIV_ID,RECIP_ID,VER_NBR)
  VALUES ('KEW','mock',1,'testuser6',0)
/
INSERT INTO KREN_RECIP_DELIV_T (CHNL,NM,RECIP_DELIV_ID,RECIP_ID,VER_NBR)
  VALUES ('KEW','mock',2,'testuser1',0)
/
INSERT INTO KREN_RECIP_DELIV_T (CHNL,NM,RECIP_DELIV_ID,RECIP_ID,VER_NBR)
  VALUES ('KEW','mock',3,'testuser2',0)
/
INSERT INTO KREN_RECIP_DELIV_T (CHNL,NM,RECIP_DELIV_ID,RECIP_ID,VER_NBR)
  VALUES ('KEW','mock',4,'quickstart',0)
/
INSERT INTO KREN_RECIP_DELIV_T (CHNL,NM,RECIP_DELIV_ID,RECIP_ID,VER_NBR)
  VALUES ('KEW','mock',5,'testuser5',0)
/
INSERT INTO KREN_RECIP_DELIV_T (CHNL,NM,RECIP_DELIV_ID,RECIP_ID,VER_NBR)
  VALUES ('KEW','mock',6,'testuser4',0)
/

# -----------------------------------------------------------------------
# KREN_RECIP_LIST_T
# -----------------------------------------------------------------------
INSERT INTO KREN_RECIP_LIST_T (CHNL_ID,RECIP_ID,RECIP_LIST_ID,RECIP_TYP_CD)
  VALUES (4,'testuser1',1,'USER')
/
INSERT INTO KREN_RECIP_LIST_T (CHNL_ID,RECIP_ID,RECIP_LIST_ID,RECIP_TYP_CD)
  VALUES (4,'testuser3',2,'USER')
/

# -----------------------------------------------------------------------
# KREN_RVWER_T
# -----------------------------------------------------------------------
INSERT INTO KREN_RVWER_T (CHNL_ID,PRNCPL_ID,RVWER_ID,TYP,VER_NBR)
  VALUES (1,'RiceTeam',1,'GROUP',1)
/
INSERT INTO KREN_RVWER_T (CHNL_ID,PRNCPL_ID,RVWER_ID,TYP,VER_NBR)
  VALUES (5,'testuser3',2,'USER',1)
/
INSERT INTO KREN_RVWER_T (CHNL_ID,PRNCPL_ID,RVWER_ID,TYP,VER_NBR)
  VALUES (5,'TestGroup1',3,'GROUP',1)
/

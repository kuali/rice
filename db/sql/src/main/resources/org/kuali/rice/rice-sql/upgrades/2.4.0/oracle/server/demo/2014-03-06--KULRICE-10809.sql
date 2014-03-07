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

DROP TABLE TRV_MULTI_ATT_SAMPLE
/

CREATE TABLE TRV_ATT_GRP_SAMPLE  (
	ATT_GRP_NUM      	VARCHAR2(10),
	OBJ_ID        	VARCHAR2(36) NOT NULL,
	VER_NBR       	NUMBER(8,0) DEFAULT 1 NOT NULL,
	ATT_GRP_NAME     	VARCHAR2(40),
  PRIMARY KEY ( ATT_GRP_NUM )
)
/

DROP TABLE TRV_ATT_SAMPLE
/

CREATE TABLE TRV_ATT_SAMPLE
(
      ATTACHMENT_ID VARCHAR2(30),
      ATT_GRP_NUM VARCHAR2(30),
      DESCRIPTION VARCHAR2(4000),
      ATTACHMENT_FILENAME VARCHAR2(300),
      ATTACHMENT_FILE_CONTENT_TYPE VARCHAR2(255),
      ATTACHMENT_FILE BLOB,
      OBJ_ID VARCHAR2(36) NOT NULL,
      VER_NBR NUMBER(8) default 0 NOT NULL,
      PRIMARY KEY (ATTACHMENT_ID,ATT_GRP_NUM)
)
/

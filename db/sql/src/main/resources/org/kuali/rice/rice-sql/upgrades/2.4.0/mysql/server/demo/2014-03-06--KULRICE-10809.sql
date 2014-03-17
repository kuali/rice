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

CREATE TABLE TRV_ATT_GRP_SAMPLE (
  ATT_GRP_NUM varchar(10) NOT NULL DEFAULT '',
  OBJ_ID varchar(36) NOT NULL,
  VER_NBR decimal(8,0) NOT NULL DEFAULT 1,
  ATT_GRP_NAME varchar(40) DEFAULT NULL,
  PRIMARY KEY (ATT_GRP_NUM)
)
/

DROP TABLE TRV_ATT_SAMPLE
/

CREATE TABLE TRV_ATT_SAMPLE
(
      ATTACHMENT_ID VARCHAR(30),
      ATT_GRP_NUM VARCHAR(30),
      DESCRIPTION VARCHAR(100),
      ATTACHMENT_FILENAME VARCHAR(300),
      ATTACHMENT_FILE_CONTENT_TYPE VARCHAR(255),
      ATTACHMENT_FILE LONGBLOB,
      OBJ_ID VARCHAR(36) NOT NULL,
      VER_NBR DECIMAL(8,0) default 0 NOT NULL,
      PRIMARY KEY (ATTACHMENT_ID,ATT_GRP_NUM)
)
/






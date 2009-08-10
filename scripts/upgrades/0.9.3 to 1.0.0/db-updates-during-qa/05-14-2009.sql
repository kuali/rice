-- 
-- Copyright 2009 The Kuali Foundation
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
TRUNCATE TABLE KRNS_SESN_DOC_T
/

DROP TABLE KRNS_SESN_DOC_T
/

CREATE TABLE KRNS_SESN_DOC_T
(SESN_DOC_ID                   VARCHAR2(40) NOT NULL,
DOC_HDR_ID                     VARCHAR2(14) NOT NULL,
PRNCPL_ID                             VARCHAR2(40) NOT NULL,
IP_ADDR                                 VARCHAR2(60) NOT NULL,
SERIALZD_DOC_FRM               BLOB,
LAST_UPDT_DT                   DATE,
CONTENT_ENCRYPTED_IND          CHAR(1) DEFAULT 'N')
/

CREATE INDEX KRNS_SESN_DOC_TI1 ON KRNS_SESN_DOC_T
(
LAST_UPDT_DT                    ASC
)
/

ALTER TABLE KRNS_SESN_DOC_T
ADD CONSTRAINT KRNS_SESN_DOC_TP1
PRIMARY KEY (SESN_DOC_ID, DOC_HDR_ID, PRNCPL_ID, IP_ADDR)
/

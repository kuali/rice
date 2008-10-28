/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
CREATE TABLE KR_COUNTY_T 
    (COUNTY_CD VARCHAR2(10) NOT NULL, 
    STATE_CD VARCHAR2(2) NOT NULL, 
    POSTAL_CNTRY_CD VARCHAR2(2) DEFAULT 'US' NOT NULL, 
    OBJ_ID VARCHAR2(36) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    COUNTY_NM VARCHAR2(100), 
    ACTV_IND VARCHAR2(1)) 
/ 
ALTER TABLE KR_COUNTY_T 
ADD CONSTRAINT KR_COUNTY_TP1 
PRIMARY KEY (COUNTY_CD, STATE_CD, POSTAL_CNTRY_CD) 
/ 
ALTER TABLE KR_COUNTY_T 
ADD CONSTRAINT KR_COUNTY_TC0 
UNIQUE (OBJ_ID) 
/ 
ALTER TABLE KR_COUNTY_T 
ADD CONSTRAINT KR_COUNTY_TR1 
FOREIGN KEY (STATE_CD, POSTAL_CNTRY_CD) 
REFERENCES KR_STATE_T 
/ 



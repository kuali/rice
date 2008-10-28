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
CREATE TABLE KR_POSTAL_CODE_T 
    (POSTAL_ZIP_CD VARCHAR2(20) NOT NULL, 
    POSTAL_CNTRY_CD VARCHAR2(2) DEFAULT 'US' NOT NULL, 
    OBJ_ID VARCHAR2(36) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    POSTAL_STATE_CD VARCHAR2(2), 
    COUNTY_CD VARCHAR2(10), 
    POSTAL_CITY_NM VARCHAR2(30), 
    BLDG_CD VARCHAR2(4), 
    BLDG_ROOM_NBR VARCHAR2(8), 
    ACTV_IND VARCHAR2(1) DEFAULT 'Y' NOT NULL) 
/ 
ALTER TABLE KR_POSTAL_CODE_T 
ADD CONSTRAINT KR_POSTAL_CODE_TP1 
PRIMARY KEY (POSTAL_ZIP_CD, POSTAL_CNTRY_CD) 
/ 
ALTER TABLE KR_POSTAL_CODE_T 
ADD CONSTRAINT KR_POSTAL_CODE_TC0 
UNIQUE (OBJ_ID) 
/ 
ALTER TABLE KR_POSTAL_CODE_T 
ADD CONSTRAINT KR_POSTAL_CODE_TR1 
FOREIGN KEY (POSTAL_CNTRY_CD) 
REFERENCES KR_COUNTRY_T 
/ 
ALTER TABLE KR_POSTAL_CODE_T 
ADD CONSTRAINT KR_POSTAL_CODE_TR2 
FOREIGN KEY (POSTAL_STATE_CD, POSTAL_CNTRY_CD) 
REFERENCES KR_STATE_T 
/ 
ALTER TABLE KR_POSTAL_CODE_T 
ADD CONSTRAINT KR_POSTAL_CODE_TR3 
FOREIGN KEY (COUNTY_CD, POSTAL_STATE_CD, POSTAL_CNTRY_CD) 
REFERENCES KR_COUNTY_T 
/ 


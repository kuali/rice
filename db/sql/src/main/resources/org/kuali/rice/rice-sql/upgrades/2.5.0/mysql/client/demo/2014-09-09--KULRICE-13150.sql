--
-- Copyright 2005-2018 The Kuali Foundation
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

CREATE  TABLE TRVL_TRAVELER_TYP_T
  (
    CODE VARCHAR(45) NOT NULL ,
    SRC_CODE VARCHAR(45) NULL ,
    NM VARCHAR(45) NULL ,
    ADVANCES_IND VARCHAR(45) NULL ,
    ACTV_IND VARCHAR(45) NULL ,
    OBJ_ID VARCHAR(45) NULL ,
    VER_NBR DECIMAL(8,0) NULL ,
    CONSTRAINT TRVL_TRAVELER_TYP_T_TC0 UNIQUE (OBJ_ID),
    CONSTRAINT TRVL_TRAVELER_TYP_T_TP1 PRIMARY KEY(CODE)
  )
 /
  insert into TRVL_TRAVELER_TYP_T (CODE, NM, ACTV_IND) values ('NON','NON-Employee','Y')
  /
  Insert into TRVL_TRAVELER_TYP_T (CODE, NM, ACTV_IND) values ('EMP','Employee','Y')
  /
  UPDATE TRVL_TRAVELER_DTL_T SET TRAVELER_TYP_CD='EMP' WHERE ID='1'
  /

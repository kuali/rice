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

TRUNCATE TABLE BK_ADDRESS_T DROP STORAGE
/
INSERT INTO BK_ADDRESS_T (ACTV_IND,ADDRESS_ID,ADDR_TYP,AUTHOR_ID,CITY,COUNTRY,OBJ_ID,PROVIENCE,STREET1,STREET2,VER_NBR)
  VALUES ('Y',1,'Residence',1,'CityR','CountryR','b8190679-7cfe-49c9-bd99-6b264f700f0d','ProvinceR','Strt1R','Strt2R',1)
/
INSERT INTO BK_ADDRESS_T (ACTV_IND,ADDRESS_ID,ADDR_TYP,AUTHOR_ID,CITY,COUNTRY,OBJ_ID,PROVIENCE,STREET1,STREET2,VER_NBR)
  VALUES ('Y',2,'Office',1,'CityO','CountryO','b8190679-7cfe-49c9-bd99-6b264f700f03','ProvinceO','Strt1O','Strt2O',1)
/

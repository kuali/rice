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
# BK_ADDRESS_TYP_T
# -----------------------------------------------------------------------
INSERT INTO BK_ADDRESS_TYP_T (ACTV_IND,ADDR_TYP,DESC_TXT,OBJ_ID,VER_NBR)
  VALUES ('Y','Office','Official Address','a03ad608-84fa-4c89-8410-0a91ed56cb66',1)
/
INSERT INTO BK_ADDRESS_TYP_T (ACTV_IND,ADDR_TYP,DESC_TXT,OBJ_ID,VER_NBR)
  VALUES ('Y','Residence','Residential Address','b8190679-7cfe-49c9-bd99-6b264f700f0d',1)
/

# -----------------------------------------------------------------------
# BK_ADDRESS_T
# -----------------------------------------------------------------------
INSERT INTO BK_ADDRESS_T (ACTV_IND,ADDRESS_ID,ADDR_TYP,AUTHOR_ID,CITY,COUNTRY,OBJ_ID,PROVIENCE,STREET1,STREET2,VER_NBR)
  VALUES ('Y',1,'Residence',1,'CityR','CountryR','b8190679-7cfe-49c9-bd99-6b264f700f0d','ProvinceR','Strt1R','Strt2R',1)
/
INSERT INTO BK_ADDRESS_T (ACTV_IND,ADDRESS_ID,ADDR_TYP,AUTHOR_ID,CITY,COUNTRY,OBJ_ID,PROVIENCE,STREET1,STREET2,VER_NBR)
  VALUES ('Y',2,'Office',1,'CityO','CountryO','b8190679-7cfe-49c9-bd99-6b264f700f03','ProvinceO','Strt1O','Strt2O',1)
/

# -----------------------------------------------------------------------
# BK_AUTHOR_T
# -----------------------------------------------------------------------
INSERT INTO BK_AUTHOR_T (ACTV_IND,AUTHOR_ID,EMAIL,NM,OBJ_ID,PHONE_NBR,VER_NBR)
  VALUES ('Y',1,'roshan@jimail.com','Roshan Mahanama','a03ad608-84fa-4c89-8410-0a91ed56cb66','123-123-1233',1)
/
INSERT INTO BK_AUTHOR_T (ACTV_IND,AUTHOR_ID,EMAIL,NM,OBJ_ID,PHONE_NBR,VER_NBR)
  VALUES ('Y',2,'jfranklin@jimail.com','James Franklin','a03ad608-84fa-4c89-8410-0a91ed56cb32','999-433-4323',1)
/

# -----------------------------------------------------------------------
# BK_AUTHOR_ACCOUNT_T
# -----------------------------------------------------------------------
INSERT INTO BK_AUTHOR_ACCOUNT_T (ACCOUNT_NUMBER,AUTHOR_ID,BANK_NAME)
  VALUES ('123123123123123',1,'Money Deposit Bank Ltd')
/
INSERT INTO BK_AUTHOR_ACCOUNT_T (ACCOUNT_NUMBER,AUTHOR_ID,BANK_NAME)
  VALUES ('123123456456456',2,'Money Deposit Bank Ltd')
/

# -----------------------------------------------------------------------
# BK_BOOK_TYP_T
# -----------------------------------------------------------------------
INSERT INTO BK_BOOK_TYP_T (ACTV_IND,DESC_TXT,NM,OBJ_ID,TYP_CD,VER_NBR)
  VALUES ('Y','Romantic Books','Romantic','6bbbdb82-d614-49c2-8716-4234e72f9f5e','ROM',1)
/
INSERT INTO BK_BOOK_TYP_T (ACTV_IND,DESC_TXT,NM,OBJ_ID,TYP_CD,VER_NBR)
  VALUES ('Y','Science Fiction Story','Science Fiction','482b3394-0327-4e93-bd80-c5dc3b2a9e1f','SCI-FI',1)
/

# -----------------------------------------------------------------------
# BK_BOOK_T
# -----------------------------------------------------------------------
INSERT INTO BK_BOOK_T (BOOK_ID,ISBN,OBJ_ID,PRICE,PUBLISHER,PUB_DATE,RATING,TITLE,TYP_CD,VER_NBR)
  VALUES (1,'9781402894626','482b3394-0327-4e93-bd80-c5dc3b2a9e34',34.43,'Rupa Publishers Ltd.',STR_TO_DATE( '20020901000000', '%Y%m%d%H%i%s' ),87,'i See','ROM',1)
/
INSERT INTO BK_BOOK_T (BOOK_ID,ISBN,OBJ_ID,PRICE,PUBLISHER,PUB_DATE,RATING,TITLE,TYP_CD,VER_NBR)
  VALUES (2,'9781402894634','482b3394-0327-4ee5-bd80-c5dc3b2a9e34',12.43,'Rupa Publishers Ltd.',STR_TO_DATE( '20020901000000', '%Y%m%d%H%i%s' ),90,'Galactico','SCI-FI',1)
/

# -----------------------------------------------------------------------
# BK_BOOK_AUTHOR_T
# -----------------------------------------------------------------------
INSERT INTO BK_BOOK_AUTHOR_T (AUTHOR_ID,BOOK_ID)
  VALUES (1,1)
/
INSERT INTO BK_BOOK_AUTHOR_T (AUTHOR_ID,BOOK_ID)
  VALUES (1,2)
/
INSERT INTO BK_BOOK_AUTHOR_T (AUTHOR_ID,BOOK_ID)
  VALUES (2,1)
/

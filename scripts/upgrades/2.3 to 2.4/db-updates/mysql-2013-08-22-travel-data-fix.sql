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

CREATE
    TABLE TRVL_TRAVELER_DTL_T
    (
        id INTEGER AUTO_INCREMENT
        , OBJ_ID VARCHAR(36) UNIQUE NOT NULL
        , VER_NBR DECIMAL(8,0) DEFAULT 1 NOT NULL
        , ACTV_IND VARCHAR(1) DEFAULT 'Y'
        , citizenship VARCHAR(40)
        , city_nm VARCHAR(50)
        , country_cd VARCHAR(2)
        , customer_num VARCHAR(40)
        , doc_nbr VARCHAR(14)
        , drive_lic_exp_dt DATE
        , drive_lic_num VARCHAR(20)
        , email_addr VARCHAR(50)
        , first_nm VARCHAR(40)
        , gender VARCHAR(1)
        , last_nm VARCHAR(40)
        , MIDDLENAME VARCHAR(40)
        , non_res_alien VARCHAR(1)
        , phone_nbr VARCHAR(20)
        , EMP_PRINCIPAL_ID VARCHAR(255)
        , postal_state_cd VARCHAR(2)
        , addr_line_1 VARCHAR(50)
        , addr_line_2 VARCHAR(50)
        , traveler_typ_cd VARCHAR(3)
        , postal_cd VARCHAR(11)
        , PRIMARY KEY (id)
    )
;

INSERT
INTO
	TRVL_TRAVELER_DTL_T(id 
	, ACTV_IND 
	, citizenship 
	, city_nm 
	, country_cd 
	, customer_num 
	, doc_nbr 
	, drive_lic_exp_dt 
	, drive_lic_num 
	, email_addr 
	, first_nm 
	, gender 
	, last_nm 
	, MIDDLENAME 
	, non_res_alien 
	, OBJ_ID 
	, phone_nbr 
	, EMP_PRINCIPAL_ID 
	, postal_state_cd 
	, addr_line_1 
	, addr_line_2 
	, traveler_typ_cd 
	, VER_NBR 
	, postal_cd) 
VALUES
	(
		1 
		, 'Y' 
		, 'US' 
		, 'Davis' 
		, 'US' 
		, 'CUST' 
		, '??' 
		, NULL 
		, NULL 
		, NULL 
		, 'Test' 
		, 'M' 
		, 'Traveler' 
		, 'A' 
		, 'N' 
		, 'IMAGUID' 
		, '8005551212' 
		, 'fred' 
		, 'CA' 
		, '123 Nowhere St.' 
		, NULL 
		, '123' 
		, 1 
		, '95616' 
	)
;


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

CREATE TABLE KRTST_CONF_SESS_T (
    ID varchar(10) not null,
    TITLE varchar(40),
    SESS_DT date,
    START_TIME varchar(10),
    END_TIME varchar(10),
    SESS_TYPE_CODE char(1),
    DESCRIPTION varchar(255),
    COORD_ID bigint,
    ALT_COORD1_ID bigint,
    ALT_COORD2_ID bigint,
    ROOM_ID varchar(10),
    ALT_ROOM1_ID varchar(10),
    ALT_ROOM2_ID varchar(10),
    PRIMARY KEY(id)
)
/
CREATE TABLE KRTST_CONF_PRESENTER_T (
    ID varchar(10) not null,
    NM varchar(40),
    INST_NM varchar(40),
    PRIMARY KEY(ID)
)
/
CREATE TABLE KRTST_CONF_ROOM_T (
    ID varchar(10) not null,
    BLDG_NM varchar(40),
    NUM varchar(10),
    PRIMARY KEY(ID)
)
/
CREATE TABLE KRTST_CONF_COORD_T (
    ID bigint not null,
    NM varchar(40),
    PRIMARY KEY (ID)
)
/
CREATE TABLE KRTST_CONF_SESS_PRES_T (
    ID varchar(10) not null,
    SESS_ID varchar(10),
    PRES_ID varchar(10),
    PRIMARY_IND char(1),
    PRIMARY KEY (ID)
)
/
CREATE TABLE KRTST_CONF_LBL_T (
    NM varchar(40) not null,
    PRIMARY KEY (NM)
)
/
CREATE TABLE KRTST_CONF_LBL_EXT_T (
    NM varchar(40) not null,
    EXTRA varchar(40),
    PRIMARY KEY (NM)
)
/
ALTER TABLE KRTST_CONF_SESS_T
    ADD CONSTRAINT KRTST_CONF_SESS_FK1
    FOREIGN KEY (COORD_ID)
    REFERENCES KRTST_CONF_COORD_T (ID)
/
ALTER TABLE KRTST_CONF_SESS_T
    ADD CONSTRAINT KRTST_CONF_SESS_FK2
    FOREIGN KEY (ALT_COORD1_ID)
    REFERENCES KRTST_CONF_COORD_T (ID)
/
ALTER TABLE KRTST_CONF_SESS_T
    ADD CONSTRAINT KRTST_CONF_SESS_FK3
    FOREIGN KEY (ALT_COORD2_ID)
    REFERENCES KRTST_CONF_COORD_T (ID)
/
ALTER TABLE KRTST_CONF_SESS_T
    ADD CONSTRAINT KRTST_CONF_SESS_FK4
    FOREIGN KEY (ROOM_ID)
    REFERENCES KRTST_CONF_ROOM_T (ID)
/
ALTER TABLE KRTST_CONF_SESS_T
    ADD CONSTRAINT KRTST_CONF_SESS_FK5
    FOREIGN KEY (ALT_ROOM1_ID)
    REFERENCES KRTST_CONF_ROOM_T (ID)
/
ALTER TABLE KRTST_CONF_SESS_T
    ADD CONSTRAINT KRTST_CONF_SESS_FK6
    FOREIGN KEY (ALT_ROOM2_ID)
    REFERENCES KRTST_CONF_ROOM_T (ID)
/
ALTER TABLE KRTST_CONF_SESS_PRES_T
    ADD CONSTRAINT KRTST_CONF_SESS_PRES_FK1
    FOREIGN KEY (SESS_ID)
    REFERENCES KRTST_CONF_SESS_T (ID)
/
ALTER TABLE KRTST_CONF_SESS_PRES_T
    ADD CONSTRAINT KRTST_CONF_SESS_PRES_FK2
    FOREIGN KEY (PRES_ID)
    REFERENCES KRTST_CONF_PRESENTER_T (ID)
/
CREATE TABLE KRTST_CONF_SESS_S (
	ID bigint(19) not null auto_increment, primary key (id)
) ENGINE MyISAM
/
ALTER TABLE KRTST_CONF_SESS_S auto_increment = 1
/
CREATE TABLE KRTST_CONF_PRESENTER_S (
	ID bigint(19) not null auto_increment, primary key (id)
) ENGINE MyISAM
/
ALTER TABLE KRTST_CONF_PRESENTER_S auto_increment = 1
/
CREATE TABLE KRTST_CONF_ROOM_S (
	ID bigint(19) not null auto_increment, primary key (id)
) ENGINE MyISAM
/
ALTER TABLE KRTST_CONF_ROOM_S auto_increment = 1
/
CREATE TABLE KRTST_CONF_COORD_S (
	ID bigint(19) not null auto_increment, primary key (id)
) ENGINE MyISAM
/
ALTER TABLE KRTST_CONF_COORD_S auto_increment = 1
/
CREATE TABLE KRTST_CONF_SESS_PRES_S (
	ID bigint(19) not null auto_increment, primary key (id)
) ENGINE MyISAM
/
ALTER TABLE KRTST_CONF_SESS_PRES_S auto_increment = 1
/


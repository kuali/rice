/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
ALTER TABLE NOTIFICATION_CONTENT_TYPES ADD (
   VERSION NUMBER(8) DEFAULT '0' NOT NULL,
   CUR_IND CHAR DEFAULT 'T' NOT NULL) /

UPDATE NOTIFICATION_CONTENT_TYPES SET VERSION = 0 WHERE VERSION = NULL /
UPDATE NOTIFICATION_CONTENT_TYPES SET CUR_IND = 'T' WHERE CUR_IND = NULL /

ALTER TABLE NOTIFICATION_CONTENT_TYPES
DROP CONSTRAINT NOTIFICATION_CONTENT_TYPE_UK1 /

ALTER TABLE NOTIFICATION_CONTENT_TYPES
ADD CONSTRAINT NOTIFICATION_CONTENT_TYPE_UK1 UNIQUE
(
NAME, VERSION
)
 ENABLE
/

drop table RECIPIENT_PREFERENCES cascade constraints /

DROP SEQUENCE RECIPIENT_PREFERENCES_SEQ /

drop table USER_DELIVERER_CONFIG cascade constraints /

DROP SEQUENCE USER_DELIVERER_CONFIG_SEQ /

alter table NOTIFICATION_MSG_DELIVS DROP CONSTRAINT NOTIF_MSG_DELIVS_UK1 /

ALTER TABLE NOTIFICATION_MSG_DELIVS
ADD CONSTRAINT NOTIF_MSG_DELIVS_UK1 UNIQUE
(
NOTIFICATION_ID,
USER_RECIPIENT_ID
)
 ENABLE
/

alter table NOTIFICATION_MSG_DELIVS drop column MESSAGE_DELIVERY_TYPE_NAME /

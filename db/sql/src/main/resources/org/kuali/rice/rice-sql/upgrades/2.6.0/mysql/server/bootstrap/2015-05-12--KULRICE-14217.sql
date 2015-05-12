--
-- Copyright 2005-2015 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License")/
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
-- KULRICE-14217   These SQL changes are only needed if the external.actn.list.notification.lifecycle.enabled
--                 parameter is set to true
--

CREATE TABLE KREW_ACTN_ITM_CHANGED_T
(
    ACTN_TYP VARCHAR(1),
    ACTN_ITM_ID VARCHAR(40),
    PRIMARY KEY (ACTN_ITM_ID, ACTN_TYP)
)
/

--
--Drop the trigger if it exists
--
DROP TRIGGER IF EXISTS actn_item_changed_insert
/

CREATE TRIGGER actn_item_changed_insert
AFTER INSERT ON KREW_ACTN_ITM_T
FOR EACH ROW
BEGIN
    INSERT INTO KREW_ACTN_ITM_CHANGED_T VALUES ('I', NEW.ACTN_ITM_ID);
END;
/

--
--Drop the trigger if it exists
--
DROP TRIGGER IF EXISTS actn_item_changed_update
/

CREATE TRIGGER actn_item_changed_update
AFTER UPDATE ON KREW_ACTN_ITM_T
FOR EACH ROW
BEGIN
    INSERT INTO KREW_ACTN_ITM_CHANGED_T VALUES ('U', NEW.ACTN_ITM_ID);
END;
/

--
--Drop the trigger if it exists
--
DROP TRIGGER IF EXISTS actn_item_changed_delete
/

CREATE TRIGGER actn_item_changed_delete
AFTER DELETE ON KREW_ACTN_ITM_T
FOR EACH ROW
BEGIN
    INSERT INTO KREW_ACTN_ITM_CHANGED_T VALUES ('D', OLD.ACTN_ITM_ID);
END;
/


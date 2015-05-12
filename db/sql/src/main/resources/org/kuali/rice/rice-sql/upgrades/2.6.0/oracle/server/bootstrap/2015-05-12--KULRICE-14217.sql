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
    ACTN_TYP VARCHAR2(1),
    ACTN_ITM_ID VARCHAR2(40),
    PRIMARY KEY(ACTN_ITM_ID, ACTN_TYP)
)
/


CREATE OR REPLACE TRIGGER actn_item_changed
AFTER INSERT OR UPDATE OR DELETE ON KREW_ACTN_ITM_T
FOR EACH ROW
BEGIN
    -- Use 'I' for an INSERT, 'U' for UPDATE, and 'D' for DELETE
	IF INSERTING THEN
      INSERT INTO KREW_ACTN_ITM_CHANGED_T VALUES ('I', :NEW.ACTN_ITM_ID);
	ELSIF UPDATING THEN
      INSERT INTO KREW_ACTN_ITM_CHANGED_T VALUES ('U', :NEW.ACTN_ITM_ID);
  ELSE
      INSERT INTO KREW_ACTN_ITM_CHANGED_T VALUES ('D', :OLD.ACTN_ITM_ID);
  END IF;
END;
/

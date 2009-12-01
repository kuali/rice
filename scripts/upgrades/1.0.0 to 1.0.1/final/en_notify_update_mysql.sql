-- 
-- Copyright 2009 The Kuali Foundation
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



DELIMITER //
DROP FUNCTION IF EXISTS convert_ken_event_time_to_local//
DROP PROCEDURE IF EXISTS update_ken_event_time_values//

CREATE FUNCTION convert_ken_event_time_to_local(old_date VARCHAR(100), new_tz VARCHAR(100)) RETURNS DATETIME
BEGIN
	-- --------------------------------------------------------------------------------------------------------
	-- This helper function takes a date string and converts it to a DATETIME in the time zone given by new_tz.
	-- WARNING! Ensure that the MySQL server's time-zone-related tables have been configured and populated
	-- properly, or the built-in CONVERT_TZ() function below (and hence this function) may return NULL.
	-- Also, depending on the time zone being converted to, "leap seconds" may have to be disabled on the
	-- appropriate table to prevent the time zone conversions from differing by an extra couple of seconds.
	-- Refer to MySQL's documentation for the time zone table setup procedure.
	-- --------------------------------------------------------------------------------------------------------
	DECLARE new_date DATETIME DEFAULT NULL;
	-- For now, ignore date formatting/conversion exceptions in case this script was executed after some
	-- notifications with the new date formats were already entered into the database.
	-- If ignoring or handling date format exceptions is not desired, then remove this handler.
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET new_date = NULL;
	-- Parse the time value and move it to the new time zone, while ignoring any exceptions that may occur.
	SET new_date = CONVERT_TZ(STR_TO_DATE(old_date, '%Y-%m-%dT%H:%i:%S'), '+00:00', new_tz);
	RETURN new_date;
END//

CREATE PROCEDURE update_ken_event_time_values()
BEGIN
	-- -----------------------------------------------------------------------------------------------------------------------------
	-- This procedure takes any KEN event notifications with UTC-format start & stop times and converts them to the local time zone.
	-- -----------------------------------------------------------------------------------------------------------------------------
	-- The ID of the current notification.
	DECLARE ev_id DECIMAL(8,0);
	-- The XML content of a notification.
	DECLARE xml_content LONGTEXT;
	-- The tags surrounding the date values that need modification.
	DECLARE start_tag_open VARCHAR(25) DEFAULT '<startDateTime>';
	DECLARE start_tag_close VARCHAR(25) DEFAULT '</startDateTime>';
	DECLARE stop_tag_open VARCHAR(25) DEFAULT '<stopDateTime>';
	DECLARE stop_tag_close VARCHAR(25) DEFAULT '</stopDateTime>';
	-- The lengths of the opening tags.
	DECLARE start_open_len INT DEFAULT CHAR_LENGTH(start_tag_open);
	DECLARE stop_open_len INT DEFAULT CHAR_LENGTH(stop_tag_open);
	-- The date values as strings.
	DECLARE datestr1 VARCHAR(100);
	DECLARE datestr2 VARCHAR(100);
	-- The string indices for marking the starts and ends of the date text content.
	DECLARE start_index1 INT;
	DECLARE start_index2 INT;
	DECLARE stop_index1 INT;
	DECLARE stop_index2 INT;
	-- Some temporary variables containing the new time values, in the form of timestamps.
	DECLARE newTime1 DATETIME;
	DECLARE newTime2 DATETIME;
	-- ------------------------------------------------------------------------------------------------------------------------------
	-- The variable below contains the time zone that the event start/stop times should be converted to.
	-- By default, this script will use the value obtained from the @@global.time_zone system variable.
	-- If a time zone other than the one from @@global.time_zone is desired, then modify local_tz_val accordingly.
	-- Note that if you want daylight savings support, the time zone must *not* be specified as an offset ("+01:00", "-05:00", etc.).
	-- Refer to MySQL's documentation for specifying region names or how to view/update the time zone variables.
	-- ------------------------------------------------------------------------------------------------------------------------------
	DECLARE local_tz_val VARCHAR(100) DEFAULT @@global.time_zone;

	-- Loop through all the rows that use a type of 'Event'.
	DECLARE finished_updating INT DEFAULT 0;
	DECLARE ev_cur CURSOR FOR SELECT KREN_NTFCTN_T.NTFCTN_ID, KREN_NTFCTN_T.CNTNT FROM KREN_CNTNT_TYP_T INNER JOIN KREN_NTFCTN_T ON KREN_CNTNT_TYP_T.CNTNT_TYP_ID=KREN_NTFCTN_T.CNTNT_TYP_ID WHERE KREN_CNTNT_TYP_T.NM='Event';
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished_updating = 1;
	OPEN ev_cur;

	FETCH ev_cur INTO ev_id, xml_content;
	WHILE NOT finished_updating DO
		-- Retrieve the start time value.
		SET start_index1 = INSTR(xml_content, start_tag_open) + start_open_len;
		SET start_index2 = INSTR(xml_content, start_tag_close);
		SET datestr1 = TRIM( SUBSTR(xml_content, start_index1, start_index2 - start_index1) );
		-- Retrieve the stop time value.
		SET stop_index1 = INSTR(xml_content, stop_tag_open) + stop_open_len;
		SET stop_index2 = INSTR(xml_content, stop_tag_close);
		SET datestr2 = TRIM( SUBSTR(xml_content, stop_index1, stop_index2 - stop_index1) );
		-- Parse the start/stop time values.
		SET newTime1 = convert_ken_event_time_to_local(datestr1, local_tz_val);
		SET newTime2 = convert_ken_event_time_to_local(datestr2, local_tz_val);
		-- Insert the new times back into the notification's text content, if no date parsing exceptions occured.
		IF newTime1 IS NOT NULL AND newTime2 IS NOT NULL THEN
			UPDATE KREN_NTFCTN_T SET CNTNT=CONCAT(SUBSTR(xml_content,1,start_index1-1), DATE_FORMAT(newTime1,'%m/%d/%Y %h:%i %p'), SUBSTR(xml_content,start_index2,stop_index1-start_index2), DATE_FORMAT(newTime2,'%m/%d/%Y %h:%i %p'), SUBSTR(xml_content,stop_index2)) WHERE NTFCTN_ID=ev_id;
		END IF;
		FETCH ev_cur INTO ev_id, xml_content;
	END WHILE;

	CLOSE ev_cur;
END//

DELIMITER ;
CALL update_ken_event_time_values();
DROP FUNCTION IF EXISTS convert_ken_event_time_to_local;
DROP PROCEDURE IF EXISTS update_ken_event_time_values;

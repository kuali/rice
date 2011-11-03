--
-- Copyright 2005-2011 The Kuali Foundation
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

DECLARE
	-- The XML content of a notification.
	xml_content KREN_NTFCTN_T.CNTNT%TYPE;
	-- The tags surrounding the date values that need modification.
	start_tag_open VARCHAR2(25) := '<startDateTime>';
	start_tag_close VARCHAR2(25) := '</startDateTime>';
	stop_tag_open VARCHAR2(25) := '<stopDateTime>';
	stop_tag_close VARCHAR2(25) := '</stopDateTime>';
	-- The lengths of the opening tags.
	start_open_len PLS_INTEGER := LENGTH(start_tag_open);
	stop_open_len PLS_INTEGER := LENGTH(stop_tag_open);
	-- The date values as strings.
	datestr1 VARCHAR2(100);
	datestr2 VARCHAR2(100);
	-- The string indices for marking the starts and ends of the date text content.
	start_index1 PLS_INTEGER;
	start_index2 PLS_INTEGER;
	stop_index1 PLS_INTEGER;
	stop_index2 PLS_INTEGER;
	-- Some temporary variables containing the new time values, in the form of timestamps.
	newTime1 TIMESTAMP WITH TIME ZONE;
	newTime2 TIMESTAMP WITH TIME ZONE;
	-- ---------------------------------------------------------------------------------------------------------------
	-- The variable below contains the time zone that the event start/stop times should be converted to.
	-- By default, this script will use the value obtained from DBTIMEZONE (see the line after the BEGIN line below).
	-- If a time zone other than the one from DBTIMEZONE is desired, then modify this variable's value accordingly.
	-- Note that if you want daylight savings support, the time zone *must* be the name of a region and not an offset.
	-- Refer to Oracle's documentation for allowable region names or how to view/update DBTIMEZONE.
	-- ---------------------------------------------------------------------------------------------------------------
	local_tz_val VARCHAR2(100);
BEGIN
	-- Remove the line below if you want to set the time zone value above explicitly; otherwise, ensure that DBTIMEZONE is configured correctly.
	SELECT DBTIMEZONE INTO local_tz_val FROM DUAL;
	-- Loop through all the rows that use a type of 'Event'.
	FOR ev_notify IN (SELECT KREN_NTFCTN_T.NTFCTN_ID, KREN_NTFCTN_T.CNTNT FROM KREN_CNTNT_TYP_T INNER JOIN KREN_NTFCTN_T ON KREN_CNTNT_TYP_T.CNTNT_TYP_ID=KREN_NTFCTN_T.CNTNT_TYP_ID WHERE KREN_CNTNT_TYP_T.NM='Event')
	LOOP
		xml_content := ev_notify.CNTNT;
		-- Retrieve the start time value.
		start_index1 := INSTR(xml_content, start_tag_open) + start_open_len;
		start_index2 := INSTR(xml_content, start_tag_close);
		datestr1 := TRIM( SUBSTR(xml_content, start_index1, start_index2 - start_index1) );
		-- Retrieve the stop time value.
		stop_index1 := INSTR(xml_content, stop_tag_open) + stop_open_len;
		stop_index2 := INSTR(xml_content, stop_tag_close);
		datestr2 := TRIM( SUBSTR(xml_content, stop_index1, stop_index2 - stop_index1) );
		-- Parse the start/stop time values to the new time zone, and catch any exceptions that may occur.
		BEGIN
			newTime1 := FROM_TZ(TO_TIMESTAMP(datestr1, 'YYYY-MM-DD"T"HH24:MI:SS'), '+00:00') AT TIME ZONE local_tz_val;
			newTime2 := FROM_TZ(TO_TIMESTAMP(datestr2, 'YYYY-MM-DD"T"HH24:MI:SS'), '+00:00') AT TIME ZONE local_tz_val;
		EXCEPTION
			-- For now, ignore date formatting/conversion exceptions in case this script was executed after some
			-- notifications with the new date formats were already entered into the database.
			-- If ignoring or handling date format exceptions is not desired, then remove this EXCEPTION block.
			WHEN OTHERS THEN
				GOTO bad_event_time;
		END;
		-- Insert the new times back into the notification's text content.
		UPDATE KREN_NTFCTN_T SET CNTNT=SUBSTR(xml_content,1,start_index1-1)||TO_CHAR(newTime1,'MM/DD/YYYY HH12:MI PM')||SUBSTR(xml_content,start_index2,stop_index1-start_index2)||TO_CHAR(newTime2,'MM/DD/YYYY HH12:MI PM')||SUBSTR(xml_content,stop_index2) WHERE NTFCTN_ID=ev_notify.NTFCTN_ID;
		-- If a date formatting/conversion exception occurred earlier, execution resumes below and the current notification is not updated.
		<<bad_event_time>>
		NULL;
	END LOOP;
END;
/

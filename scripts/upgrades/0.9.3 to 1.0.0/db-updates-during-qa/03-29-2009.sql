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

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse dates when DateTimeServiceImpl.convertToSqlDate(String) or DateTimeServiceImpl.convertToDate(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'STRING_TO_DATE_FORMATS',
'CONFG',
'MM/dd/yy;MM-dd-yy;MMMM dd, yyyy;MMddyy',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format dates to be used in a file name when DateTimeServiceImpl.toDateStringForFilename(Date) is called. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'DATE_TO_STRING_FORMAT_FOR_FILE_NAME',
'CONFG',
'yyyyMMdd',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date and time string to be used in a file name when DateTimeServiceImpl.toDateTimeStringForFilename(Date) is called.. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME',
'CONFG',
'yyyyMMdd-HH-mm-ss-S',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE',
'CONFG',
'MM/dd/yyyy',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date and time to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE',
'CONFG',
'MM/dd/yyyy hh:mm a',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse date and times when DateTimeServiceImpl.convertToDateTime(String) or DateTimeServiceImpl.convertToSqlTimestamp(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'STRING_TO_TIMESTAMP_FORMATS',
'CONFG',
'MM/dd/yyyy hh:mm a',
1
)
/

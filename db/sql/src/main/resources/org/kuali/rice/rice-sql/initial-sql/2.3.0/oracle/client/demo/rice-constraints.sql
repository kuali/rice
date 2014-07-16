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


ALTER TABLE BK_ORDER_ENTRY_T
    ADD CONSTRAINT BK_ORDER_ENTRY_T_KRNS_DO_ADV1 FOREIGN KEY (DOC_HDR_ID)
    REFERENCES KRNS_DOC_HDR_T (DOC_HDR_ID)
/

ALTER TABLE BK_ORDER_ENTRY_T
    ADD CONSTRAINT BK_ORDER_ENTRY_T_BK_BOOK_T FOREIGN KEY (BOOK_ID)
    REFERENCES BK_BOOK_T (BOOK_ID)
/


ALTER TABLE TRV_MULTI_ATT_SAMPLE
    ADD CONSTRAINT SYS_C003453453 FOREIGN KEY (ATTACHMENT_ID)
    REFERENCES TRV_ATT_SAMPLE (ATTACHMENT_ID)
/
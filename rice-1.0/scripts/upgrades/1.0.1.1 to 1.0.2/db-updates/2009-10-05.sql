--
-- Copyright 2009-2010 The Kuali Foundation
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
-- DO NOT add comments before the blank line below, or they will disappear.
drop sequence krew_doc_lnk_s 
/
create sequence krew_doc_lnk_s increment by 1 start with 2000 cache 20
/
drop index KREW_DOC_LNK_TI1 on krew_doc_lnk_t
/
drop table krew_doc_lnk_t
/
create table krew_doc_lnk_t(

           DOC_LNK_ID NUMBER(19),
           ORGN_DOC_ID NUMBER(14) NOT NULL,
           DEST_DOC_ID NUMBER(14) NOT NULL, 
           
           CONSTRAINT KREW_DOC_LNK_TP1 PRIMARY KEY (DOC_LNK_ID)
)
/
create INDEX KREW_DOC_LNK_TI1 on krew_doc_lnk_t(ORGN_DOC_ID)
/

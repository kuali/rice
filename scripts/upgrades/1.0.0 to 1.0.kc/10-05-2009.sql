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

--
--  KULRICE-7375: Rice master data source has KREW_DOC_TYP_PROC_T.INIT_RTE_NODE_ID still defined as a NUMBER
--

alter table KREW_DOC_TYP_PROC_T modify column INIT_RTE_NODE_ID varchar(40);
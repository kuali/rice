
--
--  KULRICE-7377: KREW_RTE_NODE_T still defines DOC_TYP_ID as NUMBER(19)
--

alter table KREW_RTE_NODE_T modify column DOC_TYP_ID varchar(40);
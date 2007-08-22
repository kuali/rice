-- Drop pre-1.6-release Tables --;

-- Suppress error messages when dropping non-existing tables --;
WHENEVER SQLERROR CONTINUE;

DROP TABLE EN_DOC_RTE_TYP_T CASCADE CONSTRAINTS;

-- Reinstate error messages and exit --;
WHENEVER SQLERROR EXIT SQL.SQLCODE;
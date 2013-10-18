TRUNCATE TABLE KREW_ACTN_ITM_T
/
INSERT INTO KREW_ACTN_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,ROLE_NM,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10222','2366',STR_TO_DATE( '20081222132636', '%Y%m%d%H%i%s' ),'http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2695','Travel Doc 2 - dfads','Travel Request','TravelRequest','director','director director','K','2028',1)
/
INSERT INTO KREW_ACTN_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10224','2367',STR_TO_DATE( '20090317101441', '%Y%m%d%H%i%s' ),'http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2701','Travel Request - test','Travel Request','TravelRequest','user4','A','2022',1)
/
INSERT INTO KREW_ACTN_ITM_T (ACTN_ITM_ID,ACTN_RQST_ID,ASND_DT,DLGN_PRNCPL_ID,DLGN_TYP,DOC_HDLR_URL,DOC_HDR_ID,DOC_HDR_TTL,DOC_TYP_LBL,DOC_TYP_NM,PRNCPL_ID,RQST_CD,RSP_ID,VER_NBR)
  VALUES ('10225','2368',STR_TO_DATE( '20090317101441', '%Y%m%d%H%i%s' ),'user4','S','http://localhost:8080/kr-dev/travelDocument2.do?methodToCall=docHandler','2701','Travel Request - test','Travel Request','TravelRequest','user2','A','2061',1)
/

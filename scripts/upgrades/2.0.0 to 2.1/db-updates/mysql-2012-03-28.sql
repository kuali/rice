-- KULRICE-5931

-- add 'appDocStatus' attr definition
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'appDocStatus', null, 'Y', 'KR-WKFLW', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/
-- assign it to 'Document Type & Routing Node or State' type
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-SYS' and NM='Document Type & Routing Node or State'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-WKFLW' and NM='appDocStatus'), 'Y')
/

-- create Recall permission template
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-WKFLW', 'Recall Document', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-SYS' and NM='Document Type & Routing Node or State'), 'Y')
/
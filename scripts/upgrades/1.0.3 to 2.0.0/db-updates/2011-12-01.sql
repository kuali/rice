-- PeopleFlow Name
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1001', 'PeopleFlow Name', 'KR_RULE', 'PeopleFlow Name', 'Y', null, 1, 'PeopleFlow Name')
/

insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1002', 3, '1000', '1001', 'Y', 1)
/
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1003', 3, '1001', '1001', 'Y', 1)
/
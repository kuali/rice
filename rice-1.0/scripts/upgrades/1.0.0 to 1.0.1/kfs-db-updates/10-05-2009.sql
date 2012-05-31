
-- before running this, first ingest 10-05-2009-AccountDelegateGDM.xml
insert into krim_rsp_t (rsp_id, obj_id, ver_nbr, rsp_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
values ('124', sys_guid(), 1, '1', 'KFS-COA', 'Review', '', 'Y')
/
insert into krim_rsp_attr_data_t(attr_data_id, obj_id, ver_nbr, rsp_id, kim_typ_id, kim_attr_defn_id, attr_val)
values('462', sys_guid(), 1, '124', '7', '40', 'true') /* required */
/
insert into krim_rsp_attr_data_t(attr_data_id, obj_id, ver_nbr, rsp_id, kim_typ_id, kim_attr_defn_id, attr_val)
values('463', sys_guid(), 1, '124', '7', '16', 'Account')
/
insert into krim_rsp_attr_data_t(attr_data_id, obj_id, ver_nbr, rsp_id, kim_typ_id, kim_attr_defn_id, attr_val)
values('464', sys_guid(), 1, '124', '7', '13', 'GDLG')
/
insert into krim_rsp_attr_data_t(attr_data_id, obj_id, ver_nbr, rsp_id, kim_typ_id, kim_attr_defn_id, attr_val)
values('465', sys_guid(), 1, '124', '7', '41', 'false') /* action details at role member level */
/
insert into krim_role_rsp_t (role_rsp_id, obj_id, ver_nbr, role_id, rsp_id, actv_ind)
values ('1125', sys_guid(), 1, '41', '124', 'Y')
/
insert into krim_role_rsp_actn_t (role_rsp_actn_id, obj_id, ver_nbr, actn_typ_cd, actn_plcy_cd, role_mbr_id, role_rsp_id, frc_actn)
values ('201', sys_guid(), 1, 'F', 'A', '*', '1125', 'Y')
/
delete from krew_doc_typ_t where doc_typ_nm = 'GDLG' and cur_ind = 0
/ 

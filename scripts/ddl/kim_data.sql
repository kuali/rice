-- kr_kim_ent_type_t

INSERT INTO kr_kim_ent_type_t ( ent_type_cd, ent_type_nm, display_sort_cd, obj_id )
    VALUES ( 'PERSON', 'Person', '01', SYS_GUID() )
/
INSERT INTO kr_kim_ent_type_t ( ent_type_cd, ent_type_nm, display_sort_cd, obj_id )
    VALUES ( 'SYSTEM', 'System', '02', SYS_GUID() )
/
INSERT INTO kr_kim_ent_type_t ( ent_type_cd, ent_type_nm, display_sort_cd, obj_id )
    VALUES ( 'VENDOR', 'External Vendor', '03', SYS_GUID() )
/
INSERT INTO kr_kim_ent_type_t ( ent_type_cd, ent_type_nm, display_sort_cd, obj_id )
    VALUES ( 'PAYEE', 'Other Payee', '04', SYS_GUID() )
/

-- kr_kim_ent_name_type_t

INSERT INTO kr_kim_ent_name_type_t ( ent_name_type_cd, ent_name_type_nm, display_sort_cd, obj_id )
    VALUES ( 'PREFERRED', 'Preferred Name', '01', SYS_GUID() )
/
INSERT INTO kr_kim_ent_name_type_t ( ent_name_type_cd, ent_name_type_nm, display_sort_cd, obj_id )
    VALUES ( 'NICKNAME', 'Nickname', '02', SYS_GUID() )
/
INSERT INTO kr_kim_ent_name_type_t ( ent_name_type_cd, ent_name_type_nm, display_sort_cd, obj_id )
    VALUES ( 'STREET', 'Street Name', '98', SYS_GUID() )
/
INSERT INTO kr_kim_ent_name_type_t ( ent_name_type_cd, ent_name_type_nm, display_sort_cd, obj_id )
    VALUES ( 'AKA', 'Also Known As', '03', SYS_GUID() )
/
INSERT INTO kr_kim_ent_name_type_t ( ent_name_type_cd, ent_name_type_nm, display_sort_cd, obj_id )
    VALUES ( 'PEN', 'Pen Name', '99', SYS_GUID() )
/

-- kr_kim_ext_key_type_t

INSERT INTO kr_kim_ext_key_type_t ( ext_key_type_cd, ext_key_type_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'LOGON', 'Logon ID', '01', 'N', SYS_GUID() )
/
INSERT INTO kr_kim_ext_key_type_t ( ext_key_type_cd, ext_key_type_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'SSN', 'Social Security Number', '02', 'Y', SYS_GUID() )
/
INSERT INTO kr_kim_ext_key_type_t ( ext_key_type_cd, ext_key_type_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'TAX', 'Tax ID', '03', 'Y', SYS_GUID() )
/
INSERT INTO kr_kim_ext_key_type_t ( ext_key_type_cd, ext_key_type_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'EMPLOYEE', 'Employee ID', '04', 'N', SYS_GUID() )
/
INSERT INTO kr_kim_ext_key_type_t ( ext_key_type_cd, ext_key_type_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'HR', 'Human Resources ID', '05', 'N', SYS_GUID() )
/
INSERT INTO kr_kim_ext_key_type_t ( ext_key_type_cd, ext_key_type_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'LICENSE', 'Driver''s License', '06', 'N', SYS_GUID() )
/
INSERT INTO kr_kim_ext_key_type_t ( ext_key_type_cd, ext_key_type_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'RFID', 'RFID Implant', '07', 'N', SYS_GUID() )
/

-- kr_kim_email_type_t
INSERT INTO kr_kim_email_type_t ( email_type_cd, email_type_nm, display_sort_cd, obj_id )
    VALUES ( 'CAMPUS', 'Campus', '01', SYS_GUID() )
/
INSERT INTO kr_kim_email_type_t ( email_type_cd, email_type_nm, display_sort_cd, obj_id )
    VALUES ( 'PERSONAL', 'Personal/Home', '03', SYS_GUID() )
/
INSERT INTO kr_kim_email_type_t ( email_type_cd, email_type_nm, display_sort_cd, obj_id )
    VALUES ( 'DEPARTMENT', 'Departmental', '02', SYS_GUID() )
/
INSERT INTO kr_kim_email_type_t ( email_type_cd, email_type_nm, display_sort_cd, obj_id )
    VALUES ( 'OTHER', 'Other', '99', SYS_GUID() )
/

-- kr_kim_phone_type_t

INSERT INTO kr_kim_phone_type_t ( phone_type_cd, phone_type_nm, display_sort_cd, obj_id )
    VALUES ( 'WORK', 'Work/Office', '01', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_type_cd, phone_type_nm, display_sort_cd, obj_id )
    VALUES ( 'HOME', 'Home/Personal', '02', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_type_cd, phone_type_nm, display_sort_cd, obj_id )
    VALUES ( 'MOBILE', 'Mobile/Cellular', '03', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_type_cd, phone_type_nm, display_sort_cd, obj_id )
    VALUES ( 'FAX', 'Fax', '04', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_type_cd, phone_type_nm, display_sort_cd, obj_id )
    VALUES ( 'BUSINESS', 'Business', '05', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_type_cd, phone_type_nm, display_sort_cd, obj_id )
    VALUES ( 'PIZZA', 'Favorite Pizza Place''s Number', '06', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_type_cd, phone_type_nm, display_sort_cd, obj_id )
    VALUES ( 'OTHER', 'Other', '99', SYS_GUID() )
/

-- kr_kim_addr_type_t

INSERT INTO kr_kim_addr_type_t ( addr_type_cd, addr_type_nm, display_sort_cd, obj_id )
    VALUES ( 'WORK', 'Work', '01', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_type_cd, addr_type_nm, display_sort_cd, obj_id )
    VALUES ( 'HOME', 'Home', '02', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_type_cd, addr_type_nm, display_sort_cd, obj_id )
    VALUES ( 'SUMMER', 'Summer Home', '03', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_type_cd, addr_type_nm, display_sort_cd, obj_id )
    VALUES ( 'YACHT', 'Yacht Harbor Address', '04', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_type_cd, addr_type_nm, display_sort_cd, obj_id )
    VALUES ( 'FAKE', 'Fictional', '05', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_type_cd, addr_type_nm, display_sort_cd, obj_id )
    VALUES ( 'OTHER', 'Other', '99', SYS_GUID() )
/

-- kr_kim_afltn_type_t

INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'STAFF', 'Staff', 'Y', '01', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'FACULTY', 'Faculty', 'Y', '03', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'STUDENT', 'Student', 'N', '02', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'AFFILIATE', 'Affiliate', 'N', '04', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'ALUMNI', 'Alumni', 'N', '05', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'BOOK', 'Book Club', 'N', '09', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'LUG', 'Linux User''s Group', 'N', '08', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'NACUBO', 'NACUBO', 'N', '06', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_type_cd, afltn_type_nm, emp_afltn_type_ind, display_sort_cd, obj_id )
    VALUES ( 'KUALI', 'Kuali Foundation', 'N', '07', SYS_GUID() )
/

-- updated from latest erd

INSERT INTO kr_kim_entity_principal_t ( entity_prncpl_id, entity_prncpl_nm, entity_id, prncpl_pswd, obj_id, ver_nbr, actv_ind )
    VALUES ( 'KULUSER', 'KULUSER', 'KULUSER', '', SYS_GUID(), 1, 'Y' )
/

INSERT INTO kr_kim_entity_principal_t ( entity_prncpl_id, entity_prncpl_nm, entity_id, prncpl_pswd, obj_id, ver_nbr, actv_ind )
    VALUES ( '6162502038', 'KHUNTLEY', '6162502038', '', SYS_GUID(), 1, 'Y' )
/

INSERT INTO kr_kim_entity_principal_t ( entity_prncpl_id, entity_prncpl_nm, entity_id, prncpl_pswd, obj_id, ver_nbr, actv_ind )
    VALUES ( 'admin', 'admin', 'admin', '', SYS_GUID(), 1 ,'Y' )
/

COMMIT
/

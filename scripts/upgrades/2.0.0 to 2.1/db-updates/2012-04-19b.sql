create table TRV_ATT_SAMPLE (attachment_id varchar2(30),
                              description varchar2(4000),
                              attachment_filename varchar2(300),
                              attachment_file_content_type varchar2(255),
                              attachment_file blob,
                              obj_id varchar2(36) not null,
                              ver_nbr number(8) default 0 not null,
                              primary key (attachment_id))
/
create table TRV_MULTI_ATT_SAMPLE (gen_id number(14,0) not null,
                              attachment_id varchar2(30),
                              description varchar2(4000),
                              attachment_filename varchar2(300),
                              attachment_file_content_type varchar2(255),
                              attachment_file blob,
                              obj_id varchar2(36) not null,
                              ver_nbr number(8) default 0 not null,
                              primary key (gen_id),
                              foreign key (attachment_id) references TRV_ATT_SAMPLE(attachment_id))
/
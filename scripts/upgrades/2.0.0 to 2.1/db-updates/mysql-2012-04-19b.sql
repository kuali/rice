create table TRV_ATT_SAMPLE (attachment_id varchar(30),
                              description varchar(4000),
                              attachment_filename varchar(300),
                              attachment_file_content_type varchar(255),
                              attachment_file longblob,
                              obj_id varchar(36) not null,
                              ver_nbr decimal(8) default 0 not null,
                              primary key (attachment_id));

create table TRV_MULTI_ATT_SAMPLE (gen_id decimal(14,0) not null,
                              attachment_id varchar(30),
                              description varchar(4000),
                              attachment_filename varchar(300),
                              attachment_file_content_type varchar(255),
                              attachment_file longblob,
                              obj_id varchar(36) not null,
                              ver_nbr decimal(8) default 0 not null,
                              primary key (gen_id),
                              foreign key (attachment_id) references TRV_ATT_SAMPLE(attachment_id));
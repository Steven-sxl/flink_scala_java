create DATABASE flink;

create table user_domain_config(
id int unsigned auto_increment,
user_id varchar(40) not null,
domain varchar(40) not null,
primary key (id)
);


insert into user_domain_config(user_id,domain) values('8000000','v1.go2yd.com');
insert into user_domain_config(user_id,domain) values('8000000','v2.go2yd.com');
insert into user_domain_config(user_id,domain) values('8000000','v3.go2yd.com');
insert into user_domain_config(user_id,domain) values('8000000','v4.go2yd.com');
insert into user_domain_config(user_id,domain) values('8000000','vmi.go2yd.com');
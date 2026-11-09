drop database paymentsapidb;
drop user paymentsapi;
create user paymentsapi with password 'password';
create database paymentsapidb with template=template0 owner=paymentsapi;
\connect paymentsapidb;
alter default privileges grant all on tables to paymentsapi;
alter default privileges grant all on sequences to paymentsapi;

create table pa_users(
user_id integer primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
email varchar(30) not null,
password text not null
);

create table pa_categories(
category_id integer primary key not null,
user_id integer not null,
title varchar(20) not null,
description varchar(50) not null
);
alter table pa_categories add constraint cat_users_fk
foreign key (user_id) references pa_users(user_id);

create table pa_transactions(
transaction_id integer primary key not null,
category_id integer not null,
user_id integer not null,
amount numeric(10,2) not null,
note varchar(50) not null,
transaction_date bigint not null
);
alter table pa_transactions add constraint trans_cat_fk
foreign key (category_id) references pa_categories(category_id);
alter table pa_transactions add constraint trans_users_fk
foreign key (user_id) references pa_users(user_id);

create sequence pa_users_seq increment 1 start 1;
create sequence pa_categories_seq increment 1 start 1;
create sequence pa_transactions_seq increment 1 start 1000;
CREATE USER collectivity_user with password '7891';

CREATE DATABASE collectivity_db;

GRANT CONNECT ON DATABASE collectivity_db to collectivity_user;

\c collectivity_db;

GRANT CREATE ON SCHEMA public to collectivity_user;

alter default privileges in schema public
      grant select ,insert,update,delete on tables to collectivity_user;

alter default privileges in schema public
      grant usage,update,select on sequences to collectivity_user;

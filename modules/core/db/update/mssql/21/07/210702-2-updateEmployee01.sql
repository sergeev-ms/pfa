alter table PFA_EMPLOYEE add constraint FK_PFA_EMPLOYEE_ON_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_PFA_EMPLOYEE_ON_USER on PFA_EMPLOYEE (USER_ID);

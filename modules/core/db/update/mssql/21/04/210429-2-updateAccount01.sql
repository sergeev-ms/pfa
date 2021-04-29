alter table PFA_ACCOUNT add constraint FK_PFA_ACCOUNT_ON_PARENT foreign key (PARENT_ID) references PFA_ACCOUNT(ID);
create index IDX_PFA_ACCOUNT_ON_PARENT on PFA_ACCOUNT (PARENT_ID);

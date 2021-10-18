alter table PFA_PROJECT add constraint FK_PFA_PROJECT_ON_ACCOUNT foreign key (ACCOUNT_ID) references PFA_ACCOUNT(ID);
create index IDX_PFA_PROJECT_ON_ACCOUNT on PFA_PROJECT (ACCOUNT_ID);

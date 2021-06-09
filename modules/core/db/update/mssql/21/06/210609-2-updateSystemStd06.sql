alter table PFA_SYSTEM_STD add constraint FK_PFA_SYSTEM_STD_ON_DEPTH foreign key (DEPTH_ID) references PFA_DEPTH(ID);
create index IDX_PFA_SYSTEM_STD_ON_DEPTH on PFA_SYSTEM_STD (DEPTH_ID);

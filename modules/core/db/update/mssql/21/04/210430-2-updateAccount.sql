exec sp_rename 'PFA_ACCOUNT.TYPE_', 'TYPE___U67867', 'COLUMN' ^
alter table PFA_ACCOUNT add ACTUAL_REVISION_ID uniqueidentifier ;

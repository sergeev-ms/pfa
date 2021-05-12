exec sp_rename 'PFA_SYSTEM_DETAIL.PART_NUMBER', 'PART_NUMBER__U83038', 'COLUMN' ^
alter table PFA_SYSTEM_DETAIL add PART_NUMBER_ID uniqueidentifier ;

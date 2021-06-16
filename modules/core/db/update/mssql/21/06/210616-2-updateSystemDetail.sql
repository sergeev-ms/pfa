exec sp_rename 'PFA_SYSTEM_DETAIL.QTY', 'QTY__U86571', 'COLUMN' ^
alter table PFA_SYSTEM_DETAIL add LENGTH decimal(19,6) ;

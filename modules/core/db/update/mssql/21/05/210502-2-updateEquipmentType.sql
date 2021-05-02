exec sp_rename 'PFA_EQUIPMENT_TYPE.CATEGORY', 'CATEGORY__U29259', 'COLUMN' ^
alter table PFA_EQUIPMENT_TYPE add CATEGORY_ID uniqueidentifier ;

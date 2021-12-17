exec sp_rename 'PFA_DIRECT_SALE_DETAIL.PRICE', 'PRICE__U27015', 'COLUMN' ^
alter table PFA_DIRECT_SALE_DETAIL add PRICE decimal(19, 2) ;

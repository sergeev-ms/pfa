exec sp_rename 'PFA_SYSTEM_STD.MOTOR', 'MOTOR__U47587', 'COLUMN' ^
exec sp_rename 'PFA_SYSTEM_STD.HEAD', 'HEAD__U79438', 'COLUMN' ^
exec sp_rename 'PFA_SYSTEM_STD.MOTOR_POWER', 'MOTOR_POWER__U38684', 'COLUMN' ^
exec sp_rename 'PFA_SYSTEM_STD.PUMP', 'PUMP__U48270', 'COLUMN' ^
alter table PFA_SYSTEM_STD add PUMP_MATERIALS_ID uniqueidentifier ;
alter table PFA_SYSTEM_STD add MOTOR_TYPE_ID uniqueidentifier ;
alter table PFA_SYSTEM_STD add SEAL_MATERIALS_ID uniqueidentifier ;
alter table PFA_SYSTEM_STD add INTAKE_CONFIG_ID uniqueidentifier ;
alter table PFA_SYSTEM_STD add PUMP_TYPE_ID uniqueidentifier ;
alter table PFA_SYSTEM_STD add DEPTH_ID uniqueidentifier ;
alter table PFA_SYSTEM_STD add VAPRO_CONFIG_ID uniqueidentifier ;
alter table PFA_SYSTEM_STD add MOTOR_MATERIALS_ID uniqueidentifier ;
alter table PFA_SYSTEM_STD add SEAL_CONFIG_ID uniqueidentifier ;

create table PFA_COUNTRY_SETTING_SYSTEM_ALLOCATION_REMAP (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    COUNTRY_SETTING_ID uniqueidentifier,
    UTILIZATION_VALUE_TYPE_ID uniqueidentifier,
    REMAP_SCRIPT nvarchar(max),
    --
    primary key nonclustered (ID)
);
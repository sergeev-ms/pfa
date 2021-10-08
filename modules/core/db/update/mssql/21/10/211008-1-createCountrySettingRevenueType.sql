create table PFA_COUNTRY_SETTING_REVENUE_TYPE (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    COUNTRY_SETTING_ID uniqueidentifier not null,
    REVENUE_TYPE_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
);
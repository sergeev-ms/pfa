create table PFA_COUNTRY_SETTING_EQUIPMENT_TYPE (
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
    EQUIPMENT_TYPE_ID uniqueidentifier,
    ORDER_ integer,
    MANDATORY tinyint,
    SHOW_IN_UTIL_MODEL tinyint,
    --
    primary key nonclustered (ID)
);
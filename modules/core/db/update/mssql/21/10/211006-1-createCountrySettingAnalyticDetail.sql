create table PFA_COUNTRY_SETTING_ANALYTIC_DETAIL (
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
    ANALYTIC_SET_ID uniqueidentifier,
    PRICE_LIST tinyint,
    ACTIVITY_PLAN tinyint,
    --
    primary key nonclustered (ID)
);
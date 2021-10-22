create table PFA_PROJECT_ASSIGNMENT (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    PROJECT_ID uniqueidentifier,
    ACCOUNT_ID uniqueidentifier not null,
    DATE_START datetime2,
    DATE_END datetime2,
    --
    primary key nonclustered (ID)
);
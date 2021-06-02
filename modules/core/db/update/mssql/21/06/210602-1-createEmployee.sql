create table PFA_EMPLOYEE (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    FIRST_NAME nvarchar(255),
    LAST_NAME nvarchar(255),
    NAME nvarchar(255),
    --
    primary key nonclustered (ID)
);
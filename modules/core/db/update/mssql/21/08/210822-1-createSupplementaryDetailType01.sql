create table PFA_SUPPLEMENTARY_DETAIL_TYPE (
    ID uniqueidentifier,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    NAME nvarchar(255),
    --
    primary key nonclustered (ID)
);
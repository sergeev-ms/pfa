create table PFA_ANALYTIC_SET (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    CONTRACT_TYPE varchar(50),
    JOB_TYPE varchar(50),
    WELL_EQUIP varchar(50),
    WELL_TAG varchar(50),
    --
    primary key nonclustered (ID)
);
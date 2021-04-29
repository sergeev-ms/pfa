-- begin PFA_ACCOUNT
create table PFA_ACCOUNT (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    TYPE_ varchar(50),
    PARENT_ID uniqueidentifier,
    CUSTOMER_ID decimal(7),
    --
    primary key nonclustered (ID)
)^
-- end PFA_ACCOUNT
-- begin PFA_ACTIVITY
create table PFA_ACTIVITY (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    ACCOUNT_ID uniqueidentifier,
    RECORD_TYPE varchar(50),
    YEAR_ integer,
    --
    primary key nonclustered (ID)
)^
-- end PFA_ACTIVITY
-- begin PFA_ACTIVITY_DETAIL
create table PFA_ACTIVITY_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    RECORD_TYPE varchar(50),
    ANALYTIC_ID uniqueidentifier,
    YEAR_ integer,
    MONTH_ integer,
    VALUE_ integer,
    ACTIVITY_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
)^
-- end PFA_ACTIVITY_DETAIL
-- begin PFA_ANALYTIC_SET
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
)^
-- end PFA_ANALYTIC_SET
-- begin PFA_REVENUE_TYPE
create table PFA_REVENUE_TYPE (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    --
    primary key nonclustered (ID)
)^
-- end PFA_REVENUE_TYPE
-- begin PFA_PRICE_LIST
create table PFA_PRICE_LIST (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    ACCOUNT_ID uniqueidentifier,
    RECORD_TYPE varchar(50),
    --
    primary key nonclustered (ID)
)^
-- end PFA_PRICE_LIST
-- begin PFA_PRICE_LIST_DETAIL
create table PFA_PRICE_LIST_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    PRICE_LIST_ID uniqueidentifier not null,
    ANALYTIC_ID uniqueidentifier,
    REVENUE_TYPE_ID uniqueidentifier,
    VALUE_ decimal(19, 2),
    --
    primary key nonclustered (ID)
)^
-- end PFA_PRICE_LIST_DETAIL

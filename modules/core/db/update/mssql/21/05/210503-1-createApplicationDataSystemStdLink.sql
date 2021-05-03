create table PFA_APPLICATION_DATA_SYSTEM_STD_LINK (
    APPLICATION_DATA_ID uniqueidentifier,
    SYSTEM_STD_ID uniqueidentifier,
    primary key (APPLICATION_DATA_ID, SYSTEM_STD_ID)
);

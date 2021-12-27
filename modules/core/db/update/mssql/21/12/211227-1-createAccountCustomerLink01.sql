create table PFA_ACCOUNT_CUSTOMER_LINK (
    ACCOUNT_ID uniqueidentifier,
    CUSTOMER_ID uniqueidentifier,
    primary key (ACCOUNT_ID, CUSTOMER_ID)
);

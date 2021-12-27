insert into PFA_CUSTOMER (ID, VERSION, CREATE_TS, CREATED_BY, NAME, DIM_CUSTOMER_ID)
select NEWID()           as ID,
       1                 as VERSION,
       CURRENT_TIMESTAMP as CREATE_TS,
       'pk\baranov_sv'   as CREATED_BY,
       'Unnamed'         as NAME,
       DIM_CUSTOMER_ID
FROM (
         SELECT DISTINCT a.CUSTOMER_ID as DIM_CUSTOMER_ID
         from PFA_ACCOUNT a
         where a.CUSTOMER_ID is not null ) x;

insert into PFA_ACCOUNT_CUSTOMER_LINK (ACCOUNT_ID, CUSTOMER_ID)
select a.ID, c.ID
from PFA_ACCOUNT a
         inner join PFA_CUSTOMER c on a.CUSTOMER_ID = c.DIM_CUSTOMER_ID
where a.CUSTOMER_ID is not null;





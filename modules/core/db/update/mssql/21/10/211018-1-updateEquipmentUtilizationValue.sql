insert into PFA_EQUIPMENT_UTILIZATION_DETAIL_VALUE
select newid(),
       1,
       current_timestamp,
       'admin',
       current_timestamp,
       null,
       null,
       null,
       ID,
       T,
       V
from (
         select ID, 'b524216b-9ada-a5be-4030-e69c9ebfbb36' as T, FIRST_RUN_VALUE as V
         from PFA_EQUIPMENT_UTILIZATION_DETAIL
         where FIRST_RUN_VALUE is not null
         union all
         select ID, 'adfba5eb-0d68-00ab-18da-8279c4322d39' as T, SEQUENT_RUN_VALUE as V
         from PFA_EQUIPMENT_UTILIZATION_DETAIL
         where SEQUENT_RUN_VALUE is not null
         union all
         select ID, '3c0f9fd0-6355-31fa-06c0-5dca6ad42d35' as T, SEQUENT_RUN_COMPETITOR_VALUE as V
         from PFA_EQUIPMENT_UTILIZATION_DETAIL
         where SEQUENT_RUN_COMPETITOR_VALUE is not null) as COMB
order by ID
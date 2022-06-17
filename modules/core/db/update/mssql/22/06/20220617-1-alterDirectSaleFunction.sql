CREATE OR ALTER function report_internal_DirectSales(
    @threshold_date Datetime2 = NULL,
    @query_mode   varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period   DATETIME2 = '9999-12-31'
)

    RETURNS TABLE
        AS
        RETURN
        WITH
            DIRECT_SALES AS (
                SELECT DS.ID AS DIRECT_SALE_ID,
                       DS.PARENT_ID,
                       DS.CREATE_TS AS DIRECT_SALE_CREATE_TS,
                       DS.ACCOUNT_ID,
                       DS.RECORD_TYPE,
                       DS.STATUS,
                       DS.PROBABILITY,
                       DS.B_SHARE
                FROM PFA_DIRECT_SALE DS
                WHERE DS.DELETE_TS IS NULL
                  AND DS.CREATE_TS <= COALESCE(@threshold_date, GETDATE())
                  AND dbo.calcRecordTypeWeight(DS.RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)
            ),
            WITH_NO_CHILDS_DS AS (
                SELECT DS.DIRECT_SALE_ID,
                       DS.DIRECT_SALE_CREATE_TS,
                       DS.ACCOUNT_ID,
                       DS.RECORD_TYPE,
                       DS.STATUS,
                       DS.PROBABILITY,
                       DS.B_SHARE,
                       ROW_NUMBER() over (PARTITION BY DS.ACCOUNT_ID ORDER BY DS.DIRECT_SALE_CREATE_TS DESC) AS RN
                       --,CHILD_DS.ID
                FROM DIRECT_SALES DS
                         LEFT JOIN DIRECT_SALES CHILD_DS ON CHILD_DS.PARENT_ID = DS.DIRECT_SALE_ID
                WHERE CHILD_DS.DIRECT_SALE_ID IS NULL AND DS.PARENT_ID IS NOT NULL
                UNION
                SELECT DS.DIRECT_SALE_ID,
                       DS.DIRECT_SALE_CREATE_TS,
                       DS.ACCOUNT_ID,
                       DS.RECORD_TYPE,
                       DS.STATUS,
                       DS.PROBABILITY,
                       DS.B_SHARE,
                       1 AS RN
                       --,CHILD_DS.ID
                FROM DIRECT_SALES DS
                         LEFT JOIN DIRECT_SALES CHILD_DS ON CHILD_DS.PARENT_ID = DS.DIRECT_SALE_ID
                WHERE CHILD_DS.DIRECT_SALE_ID IS NULL AND DS.PARENT_ID IS NULL
            ),

            LATEST_DETAIL AS (
                SELECT WITH_NO_CHILDS_DS.DIRECT_SALE_ID,
                       WITH_NO_CHILDS_DS.DIRECT_SALE_CREATE_TS,
                       WITH_NO_CHILDS_DS.ACCOUNT_ID,
                       WITH_NO_CHILDS_DS.RECORD_TYPE,
                       WITH_NO_CHILDS_DS.STATUS,
                       WITH_NO_CHILDS_DS.PROBABILITY,
                       WITH_NO_CHILDS_DS.B_SHARE,
                       DSD.PART_ID,
                       DSD.LENGTH,
                       DSD.PRICE,
                       DSD.LENGTH / 0.3048 * PRICE * B_SHARE AS TOTAL, --hardcoded foot conversion
                       DSD.REVENUE_TYPE_ID,
                       DSD.DATE_
                FROM WITH_NO_CHILDS_DS
                         JOIN PFA_DIRECT_SALE_DETAIL DSD ON DSD.DIRECT_SALE_ID = WITH_NO_CHILDS_DS.DIRECT_SALE_ID
                WHERE RN = 1 -- takes only the latest
                  AND DSD.DATE_ >= @start_period
                  AND DSD.DATE_ <= @end_period
            ),
            SUMMED AS (
                SELECT *,
                       SUM(TOTAL) over (PARTITION BY ACCOUNT_ID, DATE_)                                  AS GRAND_TOTAL,
                       ROW_NUMBER() over (PARTITION BY ACCOUNT_ID, DATE_ ORDER BY DIRECT_SALE_CREATE_TS) AS RN2
                FROM LATEST_DETAIL
            )
        SELECT S.ACCOUNT_ID,
               A.NAME AS ACCOUNT_NAME,
               dbo.getAccountTypeOrder(PAR.TYPE_)  AS ACCOUNT_ORDER,
               dbo.getAccountTypeTitle(PAR.TYPE_)  AS ACCOUNT_TIER,
               dbo.getContractTypeTitle(A.CONTRACT_TYPE) AS CONTRACT_TYPE,
               dbo.getAccountApplicationTypeTitle(A.APPLICATION_TYPE) AS APPLICATION_TYPE,
               PARA.NAME AS PARENT_ACCOUNT_NAME,
               E.NAME AS MANAGER_NAME,
               REVENUE_TYPE_ID,
               DATE_ AS YEAR_MONTH,
               GRAND_TOTAL AS REVENUE
        FROM SUMMED S
                 JOIN PFA_ACCOUNT A ON A.ID = S.ACCOUNT_ID
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR ON PAR.ID = A.ACTUAL_REVISION_ID
                 LEFT JOIN PFA_ACCOUNT PARA ON A.PARENT_ID = PARA.ID
                 LEFT JOIN PFA_EMPLOYEE E ON PAR.MANAGER_ID = E.ID
        WHERE RN2 = 1


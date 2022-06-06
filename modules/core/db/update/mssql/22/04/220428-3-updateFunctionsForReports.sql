CREATE OR ALTER FUNCTION report_ActivityPlan(
    @threshold_date DATETIME2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31'
)

    RETURNS TABLE
        AS
        RETURN
        WITH NUNBERED_DETAILS AS (
            SELECT PA.ACCOUNT_ID,
                   PAD.ANALYTIC_ID,
                   PA.CREATE_TS                            AS ACTIVITY_CREATE_TS,
                   PA.RECORD_TYPE,
                   DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1) AS YEAR_MONTH,
                   PAD.VALUE_,
                   ROW_NUMBER() OVER (PARTITION BY ACCOUNT_ID, ANALYTIC_ID, DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1)
                       ORDER BY PA.CREATE_TS DESC)         AS R_NUMBER
            FROM PFA_ACTIVITY_DETAIL PAD
                     JOIN PFA_ACTIVITY PA on PAD.ACTIVITY_ID = PA.ID
            WHERE PAD.DELETE_TS IS NULL
              AND PAD.CREATE_TS < COALESCE(@threshold_date, GETDATE())
              AND DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1) >= @start_period
              AND DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1) <= @end_period
              AND dbo.calcRecordTypeWeight(PA.RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)
        )

        SELECT ND.ACCOUNT_ID,
               A.NAME                                                           AS ACCOUNT_NAME,
               PAR.ACTIVE,
               dbo.getContractTypeTitle(A.CONTRACT_TYPE)                        AS CONTRACT_TYPE,
               dbo.getAccountApplicationTypeTitle(A.APPLICATION_TYPE)           AS APPLICATION_TYPE,
               PARA.NAME                                                        AS PARENT_ACCOUNT_NAME,
               A.DELETE_TS                                                      AS ACCOUNT_DELETE_TS,
               dbo.getAccountTypeTitle(PAR.TYPE_)                               AS ACCOUNT_TIER,
               dbo.getAccountTypeOrder(PAR.TYPE_)                               AS ACCOUNT_ORDER,
               E.NAME                                                           AS MANAGER_NAME,
               ND.ANALYTIC_ID,
               S.ORDER_                                                         AS ANALYTIC_ORDER,
               dbo.getAnalyticTitle(PAS.JOB_TYPE, PAS.WELL_EQUIP, PAS.WELL_TAG) AS ANALYTIC_TITLE,
               ND.ACTIVITY_CREATE_TS,
               ND.RECORD_TYPE,
               ND.YEAR_MONTH,
               ND.VALUE_
        FROM NUNBERED_DETAILS ND
                 JOIN PFA_ACCOUNT A ON A.ID = ND.ACCOUNT_ID
                 JOIN PFA_ANALYTIC_SET PAS on PAS.ID = ND.ANALYTIC_ID AND PAS.DELETE_TS IS NULL
                 LEFT JOIN PFA_ACCOUNT PARA ON A.PARENT_ID = PARA.ID
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR ON A.ACTUAL_REVISION_ID = PAR.ID
                 LEFT JOIN PFA_EMPLOYEE E ON PAR.MANAGER_ID = E.ID
                 LEFT JOIN SETTING_ANALYTIC_ORDER_vw S
                           ON S.ANALYTIC_SET_ID = ND.ANALYTIC_ID AND S.COUNTRY_ID = A.COUNTRY_ID
        WHERE R_NUMBER = 1^

CREATE OR ALTER FUNCTION report_Revenue (
    @threshold_date Datetime2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31'
)

    RETURNS TABLE
        AS
        RETURN
        WITH
            PRICE_DETAIL_WITH_RULES AS (
                SELECT APD.ACCOUNT_ID,
                       APD.REVENUE_TYPE_ID,
                       APD.ANALYTIC_ID AS PRICE_ANALYTIC_ID,
                       RCR.ANALYTIC_SET_ID AS RULE_ANALYTIC_ID,
                       APD.DETAIL_CREATE_TS,
                       APD.VALID_FROM AS PRICE_VALID_FROM,
                       APD.VALUE_
                FROM dbo.report_internal_PriceDetails (@threshold_date, @query_mode) APD
                         JOIN REVENUE_CALC_RULES RCR on APD.REVENUE_TYPE_ID = RCR.REVENUE_TYPE_ID
                WHERE APD.ANALYTIC_ID = RCR.ANALYTIC_SET_ID
            ),
            PRICE_DETAILS_WITH_RN AS (
                SELECT AD.ACCOUNT_ID,
                       AD.ACCOUNT_NAME,
                       AD.CONTRACT_TYPE,
                       AD.APPLICATION_TYPE,
                       AD.PARENT_ACCOUNT_NAME,
                       AD.ACCOUNT_TIER,
                       AD.ACCOUNT_ORDER,
                       AD.MANAGER_NAME,
                       AD.ANALYTIC_ID,
                       AD.ANALYTIC_ORDER,
                       AD.ANALYTIC_TITLE,
                       AD.YEAR_MONTH,
                       PDSD.REVENUE_TYPE_ID,
                       PDSD.PRICE_VALID_FROM,
                       ROW_NUMBER() OVER (
                           PARTITION BY AD.ACCOUNT_ID, PDSD.REVENUE_TYPE_ID, PDSD.PRICE_ANALYTIC_ID, AD.YEAR_MONTH
                           ORDER BY PDSD.PRICE_VALID_FROM DESC, PDSD.DETAIL_CREATE_TS DESC
                           ) AS RN,
                       AD.VALUE_ AS ACTIVITY_VALUE,
                       PDSD.VALUE_ AS PRICE_VALUE,
                       AD.VALUE_ * PDSD.VALUE_ AS COST
                FROM report_ActivityPlan(@threshold_date, @query_mode, @start_period, @end_period) AD
                         JOIN PRICE_DETAIL_WITH_RULES PDSD ON PDSD.ACCOUNT_ID = AD.ACCOUNT_ID AND
                                                              PDSD.PRICE_ANALYTIC_ID = AD.ANALYTIC_ID AND
                                                              AD.YEAR_MONTH >= PDSD.PRICE_VALID_FROM
            ),
            PRICE_DETAILS_WITH_LATEST_PL AS (
                SELECT ACCOUNT_ID,
                       ACCOUNT_NAME,
                       ACCOUNT_ORDER,
                       ACCOUNT_TIER,
                       CONTRACT_TYPE,
                       APPLICATION_TYPE,
                       PARENT_ACCOUNT_NAME,
                       MANAGER_NAME,
                       REVENUE_TYPE_ID,
                       YEAR_MONTH,
                       ACTIVITY_VALUE,
                       PRICE_VALUE,
                       COST,
                       SUM(COST) OVER (PARTITION BY ACCOUNT_ID, REVENUE_TYPE_ID, YEAR_MONTH) AS REVENUE,
                       RN
                FROM PRICE_DETAILS_WITH_RN
                WHERE RN = 1
            )


        SELECT DISTINCT
            ACCOUNT_ID,
            ACCOUNT_NAME,
            ACCOUNT_ORDER,
            ACCOUNT_TIER,
            CONTRACT_TYPE,
            APPLICATION_TYPE,
            PARENT_ACCOUNT_NAME,
            MANAGER_NAME,
            REVENUE_TYPE_ID,
            RT.NAME AS REVENUE_TYPE_NAME,
            RT.ORDER_ AS REVENUE_TYPE_ORDER,
            YEAR_MONTH,
            REVENUE
        FROM PRICE_DETAILS_WITH_LATEST_PL
                 LEFT JOIN PFA_REVENUE_TYPE RT ON RT.ID = PRICE_DETAILS_WITH_LATEST_PL.REVENUE_TYPE_ID
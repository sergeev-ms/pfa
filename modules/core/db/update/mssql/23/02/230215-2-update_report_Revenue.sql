CREATE   FUNCTION report_Revenue (
    @threshold_date Datetime2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31',
    @country_id UNIQUEIDENTIFIER
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
                FROM dbo.report_internal_PriceDetails (@threshold_date, IIF(@query_mode = 'FACT', 'FC', @query_mode), @country_id) APD
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
                FROM report_ActivityPlan(@threshold_date, @query_mode, @start_period, @end_period, @country_id) AD
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
                       SUM(COST) OVER (PARTITION BY ACCOUNT_ID, REVENUE_TYPE_ID, YEAR_MONTH) AS REVENUE
                FROM PRICE_DETAILS_WITH_RN
                WHERE RN = 1
                UNION ALL
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
                       REVENUE
                FROM dbo.report_internal_DirectSales(@threshold_date, IIF(@query_mode = 'FACT', 'FC', @query_mode), @start_period, @end_period)
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
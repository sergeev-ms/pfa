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
            ACTIVITY_DETAILS AS (
                SELECT ACCOUNT_ID,
                       ACCOUNT_NAME,
                       ACCOUNT_ORDER,
                       ACCOUNT_TIER,
                       CONTRACT_TYPE,
                       APPLICATION_TYPE,
                       PARENT_ACCOUNT_NAME,
                       MANAGER_NAME,
                       ANALYTIC_ID,
                       ANALYTIC_TITLE,
                       YEAR_,
                       MONTH_,
                       VALUE_
                FROM report_ActivityPlan(@threshold_date, @query_mode, @start_period, @end_period)
--                 FROM report_ActivityPlan(DEFAULT, DEFAULT, DEFAULT, DEFAULT)
            ),
            PRICE_DETAIL_WITH_RULES AS (
                SELECT APD.ACCOUNT_ID,
                       APD.ACCOUNT_NAME,
                       APD.REVENUE_TYPE_ID,
                       APD.REVENUE_TYPE_NAME,
                       APD.REVENUE_TYPE_ORDER,
                       APD.ANALYTIC_ID AS PRICE_ANALYTIC_ID,
                       RCR.ANALYTIC_SET_ID AS RULE_ANALYTIC_ID,
                       APD.DETAIL_CREATE_TS,
                       APD.YEAR_ AS PRICE_VALID_YEAR,
                       APD.MONTH_ AS PRICE_VALID_MONTH,
                       APD.VALUE_
                FROM dbo.report_PriceDetails(@threshold_date, @query_mode) APD
--                 FROM dbo.report_PriceDetails(DEFAULT, DEFAULT) APD
                         JOIN REVENUE_CALC_RULES RCR on APD.REVENUE_TYPE_ID = RCR.REVENUE_TYPE_ID
                WHERE APD.ANALYTIC_ID = RCR.ANALYTIC_SET_ID
            ),
            PRICE_DETAILS_WITH_RN AS (
                SELECT AD.*,
                       PDSD.REVENUE_TYPE_ID,
                       PDSD.REVENUE_TYPE_NAME,
                       PDSD.REVENUE_TYPE_ORDER,
                       PDSD.PRICE_VALID_YEAR,
                       PDSD.PRICE_VALID_MONTH,
                       ROW_NUMBER() over (
                           PARTITION BY AD.ACCOUNT_ID, PDSD.REVENUE_TYPE_ID, PDSD.PRICE_ANALYTIC_ID, AD.YEAR_, AD.MONTH_--, PDSD.PRICE_VALID_YEAR, PDSD.PRICE_VALID_MONTH
                           ORDER BY PDSD.PRICE_VALID_YEAR DESC , PDSD.PRICE_VALID_MONTH DESC, PDSD.DETAIL_CREATE_TS DESC
                           ) AS RN,
                       PDSD.VALUE_ AS PRICE_VALUE,
                       AD.VALUE_ AS ACTIVITY_VALUE,
                       AD.VALUE_ * PDSD.VALUE_ AS COST
                FROM ACTIVITY_DETAILS AD
                         JOIN PRICE_DETAIL_WITH_RULES PDSD ON PDSD.ACCOUNT_ID = AD.ACCOUNT_ID AND
                                                              PDSD.PRICE_ANALYTIC_ID = AD.ANALYTIC_ID AND
                                                              AD.YEAR_ >= PDSD.PRICE_VALID_YEAR AND
                                                              AD.MONTH_ >= PDSD.PRICE_VALID_MONTH
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
                       REVENUE_TYPE_NAME,
                       REVENUE_TYPE_ORDER,
                       YEAR_,
                       MONTH_,
                       ACTIVITY_VALUE,
                       PRICE_VALUE,
                       COST,
--                REVENUE,
                       SUM (ACTIVITY_VALUE * PRICE_VALUE) OVER ( PARTITION BY ACCOUNT_ID, REVENUE_TYPE_ID, YEAR_, MONTH_) AS REVENUE,
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
            REVENUE_TYPE_NAME,
            REVENUE_TYPE_ORDER,
            YEAR_,
            MONTH_,
            DATEFROMPARTS(YEAR_, MONTH_, 1) AS YEAR_MONTH,
            REVENUE
        FROM PRICE_DETAILS_WITH_LATEST_PL
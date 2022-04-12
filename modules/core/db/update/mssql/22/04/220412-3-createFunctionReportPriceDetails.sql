CREATE OR ALTER FUNCTION report_PriceDetails (
    @threshold_date Datetime2 = NULL,
    @query_mode varchar(50) = 'FC'
)

    RETURNS TABLE
        AS
        RETURN
        WITH PLD AS (
            SELECT PLD.ID,
                   PLD.CREATE_TS AS DETAIL_CREATE_TS,
                   PRICE_LIST_ID,
                   PL.ACCOUNT_ID,
                   ACC.NAME AS ACCOUNT_NAME,
                   PAR.ACTIVE AS ACTIVE,
                   ACC.PARENT_ID AS PARENT_ACCOUNT_ID,
                   PL.RECORD_TYPE,
                   PL.YEAR_,
                   PL.MONTH_,
                   PL.CREATE_TS AS PRICE_LIST_CREATE_TS,
                   ANALYTIC_ID,
                   PAS.JOB_TYPE,
                   PAS.WELL_EQUIP,
                   PAS.WELL_TAG,
                   REVENUE_TYPE_ID,
                   VALUE_

            FROM PFA_PRICE_LIST_DETAIL PLD
                     JOIN PFA_PRICE_LIST PL ON PLD.PRICE_LIST_ID = PL.ID AND PL.DELETE_TS IS NULL
                     JOIN PFA_ACCOUNT ACC on PL.ACCOUNT_ID = ACC.ID AND ACC.DELETE_TS IS NULL
                     LEFT JOIN PFA_ACCOUNT_REVISION PAR on ACC.ID = PAR.ACCOUNT_ID
                     LEFT JOIN PFA_ANALYTIC_SET PAS on PLD.ANALYTIC_ID = PAS.ID
            WHERE PL.DELETE_TS IS NULL AND PLD.DELETE_TS IS NULL
              AND PL.CREATE_TS < COALESCE(@threshold_date, GETDATE())
              AND dbo.calcRecordTypeWeight(PL.RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)
        ),
             NUNBERED_DETAILS AS (
                 SELECT *,
                        ROW_NUMBER() OVER (
                            PARTITION BY ACCOUNT_ID, ANALYTIC_ID, REVENUE_TYPE_ID, PLD.YEAR_, PLD.MONTH_
                            ORDER BY PLD.YEAR_ DESC, PLD.MONTH_ DESC, PLD.PRICE_LIST_CREATE_TS DESC
                            ) RN
                 FROM PLD
             )
        SELECT ACCOUNT_ID,
               ACCOUNT_NAME,
               ACTIVE,
               PA.NAME AS PARENT_ACCOUNT_NAME,
               ANALYTIC_ID,
               dbo.getAnalyticTitle(JOB_TYPE, WELL_EQUIP, WELL_TAG) AS ANALYTIC_TITLE,
               REVENUE_TYPE_ID,
               RT.FULL_NAME AS REVENUE_TYPE_NAME,
               RT.ORDER_ AS REVENUE_TYPE_ORDER,
               DETAIL_CREATE_TS,
               RECORD_TYPE,
               YEAR_,
               MONTH_,
               VALUE_
        FROM NUNBERED_DETAILS ND
                 LEFT JOIN PFA_ACCOUNT PA ON PA.ID = ND.PARENT_ACCOUNT_ID
                 LEFT JOIN PFA_REVENUE_TYPE RT ON RT.ID = ND.REVENUE_TYPE_ID
        WHERE RN = 1^


CREATE OR ALTER FUNCTION report_ActivityPlan (
    @threshold_date DATETIME2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31'
)

    RETURNS TABLE
        AS
        RETURN
        WITH
            SETTINGS AS (
                SELECT PCSAD.ANALYTIC_SET_ID,
                       PCS.COUNTRY_ID,
                       PCSAD.ORDER_
                FROM PFA_COUNTRY_SETTING PCS
                         JOIN PFA_COUNTRY_SETTING_ANALYTIC_DETAIL PCSAD ON PCS.ID = PCSAD.COUNTRY_SETTING_ID
            ),
            DETAILS AS (
                SELECT PA.ACCOUNT_ID,
                       PAD.ANALYTIC_ID,
                       PAS.JOB_TYPE,
                       PAS.WELL_EQUIP,
                       PAS.WELL_TAG,
                       PA.CREATE_TS AS ACTIVITY_CREATE_TS,
                       PA.RECORD_TYPE,
                       PAD.YEAR_,
                       PAD.MONTH_,
                       PAD.VALUE_
                FROM PFA_ACTIVITY_DETAIL PAD
                         JOIN PFA_ACTIVITY PA on PAD.ACTIVITY_ID = PA.ID
                         JOIN PFA_ANALYTIC_SET PAS on PAD.ANALYTIC_ID = PAS.ID AND PAS.DELETE_TS IS NULL

                WHERE PAD.DELETE_TS IS NULL
                  AND PAD.CREATE_TS < COALESCE(@threshold_date, GETDATE())
                  AND DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1) >= @start_period
                  AND DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1) <= @end_period
                  AND dbo.calcRecordTypeWeight(PA.RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)

            ),
            NUNBERED_DETAILS AS (
                SELECT *,
                       ROW_NUMBER() OVER (PARTITION BY D.ACCOUNT_ID, D.ANALYTIC_ID, D.YEAR_, D.MONTH_
                           ORDER BY D.ACTIVITY_CREATE_TS DESC) AS R_NUMBER
                FROM DETAILS D
            )

        SELECT ND.ACCOUNT_ID,
               A.NAME AS ACCOUNT_NAME,
               PAR.ACTIVE,
               dbo.getContractTypeTitle(A.CONTRACT_TYPE) AS CONTRACT_TYPE,
               dbo.getAccountApplicationTypeTitle(A.APPLICATION_TYPE) AS APPLICATION_TYPE,
               PARA.NAME AS PARENT_ACCOUNT_NAME,
               A.DELETE_TS AS ACCOUNT_DELETE_TS,
               dbo.getAccountTypeTitle(PAR.TYPE_) AS ACCOUNT_TIER,
               dbo.getAccountTypeOrder(PAR.TYPE_) AS ACCOUNT_ORDER,
               E.NAME AS MANAGER_NAME,
               ND.ANALYTIC_ID,
               S.ORDER_ AS ANALYTIC_ORDER,
               dbo.getAnalyticTitle(ND.JOB_TYPE, ND.WELL_EQUIP, ND.WELL_TAG) AS ANALYTIC_TITLE,
               ND.ACTIVITY_CREATE_TS,
               ND.RECORD_TYPE,
               DATEFROMPARTS(ND.YEAR_, ND.MONTH_, 1) AS YEAR_MONTH,
               ND.YEAR_,
               ND.MONTH_,
               ND.VALUE_
        FROM NUNBERED_DETAILS ND
                 JOIN PFA_ACCOUNT A ON A.ID = ND.ACCOUNT_ID
                 LEFT JOIN PFA_ACCOUNT PARA ON A.PARENT_ID = PARA.ID
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR ON A.ACTUAL_REVISION_ID = PAR.ID
                 LEFT JOIN PFA_EMPLOYEE E ON PAR.MANAGER_ID = E.ID
                 LEFT JOIN SETTINGS S ON S.ANALYTIC_SET_ID = ND.ANALYTIC_ID AND S.COUNTRY_ID = A.COUNTRY_ID
        WHERE R_NUMBER = 1


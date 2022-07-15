CREATE OR ALTER FUNCTION report_internal_ApplicationData(
    @threshold_date DATETIME2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31'
)
    RETURNS TABLE
        AS
        RETURN
        WITH APP_DATA AS (
            SELECT ID,
                   CREATE_TS AS APP_DATA_CREATE_TS,
--            YEAR_,
--            MONTH_,
--            RECORD_TYPE,
                   ACCOUNT_ID,
                   ROW_NUMBER() over (PARTITION BY ACCOUNT_ID ORDER BY CREATE_TS DESC) AS RN
            FROM PFA_APPLICATION_DATA
            WHERE DELETE_TS IS NULL
              AND CREATE_TS < COALESCE(@threshold_date, GETDATE())
              AND dbo.calcRecordTypeWeight(RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)
        ),
             SYSTEM_ALLOC AS (
                 SELECT AD.ID AS APPLICATION_DATA_ID,
                        AD.APP_DATA_CREATE_TS,
                        AD.ACCOUNT_ID,
                        SA.CREATE_TS AS SYSTEM_ALLOC_CREATE_TS,
                        SA.SYSTEM_ID,
                        ROW_NUMBER() over (PARTITION BY ACCOUNT_ID, AD.ID ORDER BY SA.CREATE_TS DESC) AS SYSTEM_NO,
                        SA.RUN1,
                        SA.RUN2
                 FROM APP_DATA AS AD
                          JOIN PFA_SYSTEM_ALLOCATION SA ON SA.APPLICATION_DATA_ID = AD.ID
                 WHERE SA.DELETE_TS IS NULL
                   AND AD.RN = 1
             ),
             EQUIPMENT AS (
                 SELECT SYSTEM_ALLOC.APPLICATION_DATA_ID,
                        SYSTEM_ALLOC.APP_DATA_CREATE_TS,
                        SYSTEM_ALLOC.ACCOUNT_ID,
                        SYSTEM_ALLOC.SYSTEM_NO,
                        SYSTEM_ALLOC.RUN1,
                        SYSTEM_ALLOC.RUN2,
                        PSD.PART_NUMBER_ID,
                        PSD.EQUIPMENT_TYPE_ID,
                        PSD.LENGTH / 0.3048 AS LENGTH, -- hack to convert meters to foots
                        COUNT(*) over (
                            PARTITION BY SYS.ID, PART_NUMBER_ID, EQUIPMENT_TYPE_ID
                            ) AS EQUIPMENT_COUNT
                 FROM SYSTEM_ALLOC
                          JOIN PFA_SYSTEM_STD SYS ON SYS.ID = SYSTEM_ALLOC.SYSTEM_ID
                          JOIN PFA_SYSTEM_DETAIL PSD ON SYS.ID = PSD.SYSTEM_ID AND PSD.DELETE_TS IS NULL
             )
        SELECT DISTINCT APPLICATION_DATA_ID,
                        APP_DATA_CREATE_TS,
                        ACCOUNT_ID,
                        SYSTEM_NO,
                        PART_NUMBER_ID,
                        EQUIPMENT_TYPE_ID,
                        COALESCE(LENGTH, EQUIPMENT_COUNT) AS QTY,
                        IIF(LENGTH IS NULL, 'ea', 'ft')  AS UOM,
                        RUN1,
                        RUN2
        FROM EQUIPMENT
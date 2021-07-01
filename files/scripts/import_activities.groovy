

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.activity.*
import com.borets.pfa.entity.analytic.AnalyticSet
import com.haulmont.cuba.core.global.CommitContext
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.EntitySet
import groovy.transform.Field
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.time.LocalDate
import java.time.YearMonth

@Field private DataFormatter formatter = new DataFormatter()
@Field private CommitContext commitContext = new CommitContext()
@Field private int year = 2021
@Field private int month = 1

def skipFirstRow = true
def sheetIndex = 3

final File file = new File('D:\\LoadToPFA\\activity\\Activity_transformed.xlsx')
Workbook workbook = new XSSFWorkbook(file)

Sheet sheet = workbook.getSheetAt(sheetIndex)
log.debug("sheetName: ${sheet.sheetName}")

ArrayList<ImportActivityEntry> importEntries = getImportEntries(skipFirstRow, sheet)
importEntries.forEach {log.debug(it.toString())}

def entitySet = createEntities(importEntries)
return entitySet.toListString()


private EntitySet createEntities(ArrayList<ImportActivityEntry> importEntries) {
    def accountMap = importEntries.groupBy {  it.parent + " - " + it.account }
    accountMap.forEach {accountName, entries ->
        def account = getAccount(entries.get(0))
        def byRecordType = entries.groupBy { it.recordType }
        byRecordType.forEach {recordType, entriesByRecordType ->
            def activity = createActivity(account, recordType)
            entriesByRecordType.forEach{
                createDetail(activity, it)
            }
        }
    }
    return (dataManager as DataManager).commit(commitContext)
}

private void createDetail(Activity activity, ImportActivityEntry importActivityEntry) {
    def detail = (dataManager as DataManager).create(ActivityDetail.class)
    detail.activity = activity
    detail.year = importActivityEntry.year.toInteger()
    detail.month = importActivityEntry.month.toInteger()
    detail.analytic = findAnalytic(importActivityEntry)
    detail.value = importActivityEntry.qty.toInteger()
    commitContext.addInstanceToCommit(detail)
}

private AnalyticSet findAnalytic(ImportActivityEntry entry) {
    log.debug(getContractType(entry.contractType))
    log.debug(getJobType(entry.jobType))
    log.debug(getWellEquip(entry.wellEquip))
    log.debug(getWellTag(entry.wellTag))

    def analyticSet = (dataManager as DataManager).load(AnalyticSet.class)
            .query("""select e from pfa_AnalyticSet e where e.jobType = :jobType 
                        and e.wellEquip = :wellEquip and e.wellTag = :wellTag""".stripIndent())
            .parameter('jobType', getJobType(entry.jobType))
            .parameter('wellEquip', getWellEquip(entry.wellEquip))
            .parameter('wellTag', getWellTag(entry.wellTag))
            .one()

    log.debug("found analiticSet: ${analyticSet}")
    return analyticSet
}

private static ContractType getContractType(String typeName) {
    ContractType.valueOf(typeName.toUpperCase())
}
private static WellEquip getWellEquip(String wellEquipName) {
    WellEquip.valueOf(wellEquipName.toUpperCase())
}
private static WellTag getWellTag(String wellTagName) {
    WellTag.valueOf(wellTagName.toUpperCase())
}
private static JobType getJobType(String jobType) {
    JobType.valueOf(jobType.toUpperCase())
}

private Activity createActivity(Account account, String recordType) {
    def activity = (dataManager as DataManager).create(Activity.class)
    activity.account = account
    activity.recordType = getRecordType(recordType)

    def yearMonth = YearMonth.of(this.year, this.month)
    activity.periodFrom = yearMonth.atDay(1)
    activity.periodTo = yearMonth.withMonth(12).atEndOfMonth()
    commitContext.addInstanceToCommit(activity)
    return activity
}

private Account getAccount(ImportActivityEntry importEntry) {
    (dataManager as DataManager).load(Account.class)
            .query('select e from pfa_Account e where e.name = :accountName and e.parent.name = :parentName')
            .parameter('accountName', importEntry.account)
            .parameter('parentName', importEntry.parent)
            .optional()
            .orElseGet {
                def account = (dataManager as DataManager).create(Account.class)
                account.name = importEntry.account
                return dataManager.commit(account)
            }
}

private ArrayList<ImportActivityEntry> getImportEntries(boolean skipFirstRow, XSSFSheet sheet) {
    def importEntries = new ArrayList<ImportActivityEntry>()
    def firstSkipped = false
    for (Row row : sheet) {
        if (skipFirstRow && !firstSkipped) {
            firstSkipped = true
            continue
        }
        def entry = new ImportActivityEntry()
        entry.parent = getCellValue(row, 0)
        entry.account = getCellValue(row, 1)
        entry.jobType = getCellValue(row, 2)
        entry.wellTag = getCellValue(row, 3)
        entry.wellEquip = getCellValue(row, 4)
        entry.contractType = getCellValue(row, 5)
        entry.recordType = getCellValue(row, 6)
        entry.year = getCellValue(row, 7)
        entry.month = getCellValue(row, 8)
        entry.qty = getCellValue(row, 9)
        importEntries.add(entry)
    }
    return importEntries
}

private RecordType getRecordType(String name) {
    log.debug("recordTypeName = ${name}")
    log.debug('Budget' == name)
    def value
    switch (name) {
        case 'Budget': value = RecordType.KPI; break;
        case 'Budget Correction 1': value = RecordType.Q1; break;
        case 'Budget Correction 2': value = RecordType.Q2; break;
        case 'Budget Correction 3': value = RecordType.Q3; break;
        case 'Budget Correction 4': value = RecordType.Q4; break;
        default: value = RecordType.FORECAST
    }
    log.debug("recordType = ${value}")
    return value
}

private String getCellValue(Row row, int cellIndex) {
    return this.formatter.formatCellValue(row.getCell(cellIndex)).trim()
}

class ImportActivityEntry {
    public String parent
    public String account
    public String jobType
    public String wellTag
    public String wellEquip
    public String contractType
    public String recordType
    public String year
    public String month
    public String qty

    @Override
    String toString() {
        return "${parent} - ${account} - ${jobType} - ${wellTag} - ${wellEquip} - ${contractType} -" +
                "${recordType} - ${year} - ${month} - ${qty}"
    }
}
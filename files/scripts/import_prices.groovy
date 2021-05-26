@Field private DataFormatter formatter = new DataFormatter()
@Field private CommitContext commitContext = new CommitContext()
@Field private int year = 2021
@Field private int month = 1
@Field private String pathName = 'D:/LoadToPFA/Price_load.xlsx'


import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.activity.*
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.price.PriceListDetail
import com.borets.pfa.entity.price.RevenueType
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

def skipFirstRow = true
def sheetIndex = 0

final File file = new File(pathName)
Workbook workbook = new XSSFWorkbook(file)

Sheet sheet = workbook.getSheetAt(sheetIndex)
if (sheet == null) {
    log.error('sheet is null')
    return
}

log.debug("sheetName: ${sheet.sheetName}")
log.debug("rows: ${sheet.getLastRowNum()}")

ArrayList<ImportEntry> importEntries = getImportEntries(skipFirstRow, sheet)
importEntries.forEach {log.debug(it.toString())}

def entitySet = createEntities(importEntries)
return entitySet.toListString()


private EntitySet createEntities(ArrayList<ImportEntry> importEntries) {
    def accountMap = importEntries.groupBy { it.account }
    accountMap.forEach {accountName, entries ->
        def account = getAccount(accountName)
        def byRecordType = entries.groupBy { it.recordType }
        byRecordType.forEach {recordType, entriesByRecordType ->
            def priceList = createPriceList(account, recordType)
            entriesByRecordType.forEach{
                createDetail(priceList, it)
            }
        }
    }
    return (dataManager as DataManager).commit(commitContext)
}

private void createDetail(PriceList priceList, ImportEntry importActivityEntry) {
    def detail = (dataManager as DataManager).create(PriceListDetail.class)
    detail.priceList = priceList
    detail.analytic = findAnalytic(importActivityEntry)
    detail.revenueType = getRevenueType(importActivityEntry.revenueType)
    detail.value = importActivityEntry.price.toInteger()
    commitContext.addInstanceToCommit(detail)
}

private AnalyticSet findAnalytic(ImportEntry entry) {
    log.debug(getContractType(entry.contractType))
    log.debug(getJobType(entry.jobType))
    log.debug(getWellEquip(entry.wellEquip))
    log.debug(getWellTag(entry.wellTag))

    def analyticSet = (dataManager as DataManager).load(AnalyticSet.class)
            .query("""select e from pfa_AnalyticSet e where e.contractType = :contractType 
            and e.jobType = :jobType and e.wellEquip = :wellEquip and e.wellTag = :wellTag""".stripIndent())
            .parameter('contractType', getContractType(entry.contractType))
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

private PriceList createPriceList(Account account, String recordType ) {
    def price = (dataManager as DataManager).create(PriceList.class)
    price.account = account
    price.recordType = getRecordType(recordType)
    price.year = this.year
    price.month = this.month
    commitContext.addInstanceToCommit(price)
    return price
}

private Account getAccount(String accountName) {
    (dataManager as DataManager).load(Account.class)
            .query('select e from pfa_Account e where e.name = :accountName')
            .parameter('accountName', accountName)
            .optional()
            .orElseGet {
                def account = (dataManager as DataManager).create(Account.class)
                account.name = accountName
                return dataManager.commit(account)
            }
}

private ArrayList<ImportEntry> getImportEntries(boolean skipFirstRow, XSSFSheet sheet) {
    def importEntries = new ArrayList<ImportEntry>()
    def firstSkipped = false
    for (Row row : sheet) {
        if (skipFirstRow && !firstSkipped) {
            firstSkipped = true
            continue
        }
        def entry = new ImportEntry()
        entry.account = getCellValue(row, 0)
        entry.jobType = getCellValue(row, 1)
        entry.wellTag = getCellValue(row, 2)
        entry.wellEquip = getCellValue(row, 3)
        entry.contractType = getCellValue(row, 4)
        entry.recordType = getCellValue(row, 5)
        entry.revenueType = getCellValue(row, 6)
        entry.price = getCellValue(row, 7)
        importEntries.add(entry)
    }
    return importEntries
}

private RecordType getRecordType(String name) {
    log.debug("recordTypeName = ${name}")
    def value
    switch (name) {
        case 'Budget': value = RecordType.KPI; break
        case 'Budget Correction 1': value = RecordType.Q1; break
        case 'Budget Correction 2': value = RecordType.Q2; break
        case 'Budget Correction 3': value = RecordType.Q3; break
        case 'Budget Correction 4': value = RecordType.Q4; break
        default: value = RecordType.FORECAST
    }
    log.debug("recordType = ${value}")
    return value
}

private RevenueType getRevenueType(String name) {
    (dataManager as DataManager).load(RevenueType.class)
            .query('select e from pfa_RevenueType e where e.name = :revenueName')
            .parameter('revenueName', name)
            .optional()
            .orElseGet {
                log.debug("revenueType ${name} was not fount. create new one")
                def revenueType = (dataManager as DataManager).create(RevenueType.class)
                revenueType.name = name
                return dataManager.commit(revenueType)
            }
}

private String getCellValue(Row row, int cellIndex) {
    return this.formatter.formatCellValue(row.getCell(cellIndex)).trim()
}

class ImportEntry {
    public String account
    public String jobType
    public String wellTag
    public String wellEquip
    public String contractType
    public String recordType
    public String revenueType
    public String price

    @Override
    String toString() {
        return "${account} - ${jobType} - ${wellTag} - ${wellEquip} - ${contractType} -" +
                "${recordType} - ${revenueType} - ${price}"
    }
}
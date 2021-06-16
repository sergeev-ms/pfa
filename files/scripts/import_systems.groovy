import com.borets.addon.mu.datatypes.Length
import com.borets.addon.pn.entity.Part
import com.borets.pfa.entity.account.appdata.EquipmentCategory
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.borets.pfa.entity.account.system.classification.*
import com.haulmont.chile.core.datatypes.Datatype
import com.haulmont.chile.core.datatypes.DatatypeRegistry
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.CommitContext
import com.haulmont.cuba.core.global.DataManager
import groovy.transform.Field
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

@Field private DataFormatter formatter = new DataFormatter()
@Field private CommitContext commitContext = new CommitContext()

def skipFirstRow = true
def sheetIndex = 0

final File file = new File('D:\\LoadToPFA\\systems\\Import.XLSX')
Workbook workbook = new XSSFWorkbook(file)
def datatypeRegistry = AppBeans.get(DatatypeRegistry.class)
def lengthDataType = datatypeRegistry.get(Length.NAME)

Sheet sheet = workbook.getSheetAt(sheetIndex)
log.debug("sheetName: ${sheet.sheetName}")

ArrayList<ImportEntry> importEntries = getImportEntries(skipFirstRow, sheet)
importEntries.forEach {log.debug(it.toString())}

def groupBySystem = importEntries.groupBy { Integer.parseInt(it.number) }
log.debug("groups qty: ${groupBySystem.keySet().size()}")

DataManager dataManager = dataManager as DataManager

groupBySystem.values().forEach {
    def firstImportEntry = it.get(0)
    def systemStd = dataManager.create(SystemStd.class)
    commitContext.addInstanceToCommit(systemStd)

    systemStd.casingSize = getCasingSize(firstImportEntry.casingOd)
    systemStd.casingWeight = getCasingWeight(firstImportEntry.casingWeight)
    systemStd.pumpModel = getClassifierEntity(firstImportEntry.pumpModel, PumpType.class)
    systemStd.depth = getClassifierEntity(firstImportEntry.depth, Depth.class)
    systemStd.motorType = getClassifierEntity(firstImportEntry.motorType, MotorType.class)
    systemStd.intakeConfig = getClassifierEntity(firstImportEntry.intake, IntakeConfig.class)
    systemStd.vaproConfig = getClassifierEntity(firstImportEntry.vapro, VaproConfig.class)
    systemStd.sealConfig = getClassifierEntity(firstImportEntry.seal, SealConfig.class)
    systemStd.pumpConfig = getClassifierEntity(firstImportEntry.pumpConfig, PumpConfig.class)
    systemStd.pumpMaterials = getClassifierEntity(firstImportEntry.pumpMaterials, PumpMaterials.class)
    systemStd.sealMaterials = getClassifierEntity(firstImportEntry.sealMaterials, OtherMaterials.class)
    systemStd.motorMaterials = getClassifierEntity(firstImportEntry.motorMaterials, OtherMaterials.class)
    systemStd.comment = firstImportEntry.comment

    it.forEach {importEntity ->
        def qty = Integer.parseInt(importEntity.qty)
        if ('ESP Cable' == importEntity.type) {
            def detail = createSystemDetail(systemStd, importEntity)
            detail.length = lengthDataType.parse(qty.toString()) as BigDecimal
        } else {
            def i = 1
            while (i++ <= qty) {
                createSystemDetail(systemStd, importEntity)
            }
        }

    }
}

private SystemDetail createSystemDetail(SystemStd systemStd, ImportEntry importEntity) {
    def detail = (dataManager as DataManager).create(SystemDetail.class)
    detail.system = systemStd
    commitContext.addInstanceToCommit(detail)

    detail.equipmentType = getEquipmentType(importEntity.type, importEntity.category)
    detail.partNumber = getPartNumber(importEntity.pn)
    return detail
}

dataManager.commit(commitContext)

return groupBySystem.keySet().size()

private Part getPartNumber(String partNumber) {
    DataManager dataManager = dataManager as DataManager
    if (StringUtils.isBlank(partNumber))
        return null
    return dataManager.load(Part.class)
            .query("where e.wtPartNumber like ?1", "%${partNumber}%".toString())
            .optional()
            .orElse(null)
}

private EquipmentType getEquipmentType(String name, String categoryName) {
    DataManager dataManager = dataManager as DataManager
    if (StringUtils.isBlank(name))
        return null
    return dataManager.load(EquipmentType.class)
            .query("where e.name = ?1", name)
            .optional()
            .orElseGet({
                def equipmentType = dataManager.create(EquipmentType.class)
                equipmentType.setValue("name", name)
                equipmentType.category = getClassifierEntity(categoryName, EquipmentCategory.class)
                return dataManager.commit(equipmentType)
            })
}


private <T extends Entity> T getClassifierEntity(String name, T) {
    DataManager dataManager = dataManager as DataManager
    if (StringUtils.isBlank(name))
        return null
    return dataManager.load(T)
            .query("where e.name = ?1", name)
            .optional()
            .orElseGet({
                def entity = dataManager.create(T)
                entity.setValue("name", name)
                return dataManager.commit(entity)
            })
}


private static WellCasingWeight getCasingWeight(String weight) {
    return WellCasingWeight.fromId(weight)
}

private static WellCasingSize getCasingSize(String size) {
    return WellCasingSize.FIVE_AND_HALF
}

private String getCellValue(Row row, int cellIndex) {
    return this.formatter.formatCellValue(row.getCell(cellIndex)).trim()
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
        entry.number = getCellValue(row, 0)
        entry.casingOd = getCellValue(row, 1)
        entry.casingWeight = getCellValue(row, 2)
        entry.pumpModel = getCellValue(row, 3)
        entry.depth = getCellValue(row, 4)
        entry.motorType = getCellValue(row, 5)
        entry.intake = getCellValue(row, 6)
        entry.vapro = getCellValue(row, 7)
        entry.seal = getCellValue(row, 8)
        entry.pumpConfig = getCellValue(row, 9)
        entry.pumpMaterials = getCellValue(row, 10)
        entry.sealMaterials = getCellValue(row, 11)
        entry.motorMaterials = getCellValue(row, 12)
        entry.comment = getCellValue(row, 13)
        entry.category = getCellValue(row, 14)
        entry.type = getCellValue(row, 15)
        entry.pn = getCellValue(row, 16)
        entry.pnDescription = getCellValue(row, 17)
        entry.uom = getCellValue(row, 18)
        entry.qty = getCellValue(row, 19)
        importEntries.add(entry)
    }
    return importEntries
}


class ImportEntry {
    public String number
    public String casingOd
    public String casingWeight
    public String pumpModel
    public String depth
    public String motorType
    public String intake
    public String vapro
    public String seal
    public String pumpConfig
    public String pumpMaterials
    public String sealMaterials
    public String motorMaterials
    public String comment
    public String category
    public String type
    public String pn
    public String pnDescription
    public String uom
    public String qty

    @Override
    String toString() {
        final StringBuffer sb = new StringBuffer("ImportEntry{");
        sb.append("number=").append(number);
        sb.append(", casingOd='").append(casingOd).append('\'');
        sb.append(", casingWeight='").append(casingWeight).append('\'');
        sb.append(", pumpModel='").append(pumpModel).append('\'');
        sb.append(", depth='").append(depth).append('\'');
        sb.append(", motorType='").append(motorType).append('\'');
        sb.append(", intake='").append(intake).append('\'');
        sb.append(", vapro='").append(vapro).append('\'');
        sb.append(", seal='").append(seal).append('\'');
        sb.append(", pumpConfig='").append(pumpConfig).append('\'');
        sb.append(", pumpMaterials='").append(pumpMaterials).append('\'');
        sb.append(", sealMaterials='").append(sealMaterials).append('\'');
        sb.append(", motorMaterials='").append(motorMaterials).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", pn='").append(pn).append('\'');
        sb.append(", pnDescription='").append(pnDescription).append('\'');
        sb.append(", uom='").append(uom).append('\'');
        sb.append(", qty='").append(qty).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
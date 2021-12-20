package com.borets.pfa.core.role

import com.borets.addon.country.entity.Country
import com.borets.addon.mu.entity.MeasurementUnit
import com.borets.addon.mu.entity.MeasurementUnitSet
import com.borets.addon.pn.entity.*
import com.borets.attachments.entity.Attachment
import com.borets.pfa.entity.Employee
import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.AccountRevision
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.EquipmentCategory
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.directsale.DirectSale
import com.borets.pfa.entity.account.directsale.DirectSaleDetail
import com.borets.pfa.entity.account.marketdata.MarketData
import com.borets.pfa.entity.account.supplementary.Supplementary
import com.borets.pfa.entity.account.supplementary.SupplementaryDetail
import com.borets.pfa.entity.account.supplementary.SupplementaryDetailType
import com.borets.pfa.entity.account.system.System
import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.borets.pfa.entity.account.system.classification.*
import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetail
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetailValue
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationValueType
import com.borets.pfa.entity.activity.Activity
import com.borets.pfa.entity.activity.ActivityDetail
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.customer.DimCustomers
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.price.PriceListDetail
import com.borets.pfa.entity.price.RevenueType
import com.borets.pfa.entity.project.Project
import com.borets.pfa.entity.project.ProjectAssignment
import com.borets.pfa.entity.setting.*
import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition
import com.haulmont.cuba.security.app.role.annotation.*
import com.haulmont.cuba.security.entity.EntityOp
import com.haulmont.cuba.security.role.*

@Role(name = ApplicationEngineerRole.NAME)
class ApplicationEngineerRole : AnnotatedRoleDefinition() {
    companion object {
        const val NAME = "application-engineer"
    }

    @ScreenAccess(
        screenIds = ["application-pfa", "pfa_Account.browse", "references-group", "pfa_DimCustomers.browse",
            "pfa_SystemStd.browse", "pfa_Employee.browse", "pfa_Account.edit", "pfa_ApplicationDataFragment",
            "pfa_ActivityPivot.edit", "pfa_PriceListPivot.edit", "pfa_MarketData.browse", "pfa_MarketData.edit",
            "pfa_MarketDataFragment", "pfa_AccountRevision.browse", "pfa_AccountRevision.edit", "pfa_SystemStd.edit",
            "pfa_ApplicationData.edit", "pfa_ApplicationData.browse", "pfa_EquipmentUtilizationFragment",
            "pfa_EquipmentUtilization.edit", "pfa_EquipmentUtilization.browse", "application-pn", "pn_Part.browse",
            "pn_PartDrive.browse", "pn_PartPump.browse", "pn_PartMotor.browse", "pn_PartMotorSeal.browse",
            "pn_PartGC.browse", "pn_PartGH.browse", "pn_PartGS.browse", "pn_PartMLE.browse", "pn_PartSensor.browse",
            "pn_PartCable.browse", "pn_PartBoltDischargeHead.browse", "pn_PartBoltIntake.browse", "pn_PartXFMR.browse",
            "pfa_AnalyticSet.browse", "pfa_RevenueType.browse", "pfa_EquipmentType.browse", "pfa_EquipmentCategory.browse",
            "system-classification", "pfa_PumpType.browse", "pfa_Depth.browse", "pfa_MotorType.browse",
            "pfa_IntakeConfig.browse", "pfa_VaproConfig.browse", "pfa_SealConfig.browse", "pfa_PumpConfig.browse",
            "pfa_PumpMaterials.browse", "pfa_OtherMaterials.browse", "pfa_AnalyticSet.edit", "pfa_Depth.edit",
            "pfa_Employee.edit", "pfa_EquipmentCategory.edit", "pfa_EquipmentType.edit", "pfa_IntakeConfig.edit",
            "pfa_MotorType.edit", "pfa_OtherMaterials.edit", "pfa_PriceList.edit", "pfa_PriceListDetail.edit",
            "pfa_PumpConfig.edit", "pfa_PumpMaterials.edit", "pfa_PumpType.edit", "pfa_RevenueType.edit",
            "pfa_SealConfig.edit", "pfa_VaproConfig.edit", "pn_PartBoltDischargeHead.edit", "pn_PartBoltIntake.edit",
            "pn_PartCable.edit", "pn_PartDrive.edit", "pn_PartGC.edit", "pn_PartGH.edit", "pn_PartGS.edit",
            "pn_PartMLE.edit", "pn_PartMotor.edit", "pn_PartMotorSeal.edit", "pn_PartPump.edit", "pn_PartSensor.edit",
            "pn_PartXFMR.edit", "help", "aboutWindow", "settings", "pn_PartUMB.browse", "pn_PartOther.browse",
            "pn_PartOther.edit", "pn_PartUMB.edit", "pfa_DirectSale.edit"]
    )
    override fun screenPermissions(): ScreenPermissionsContainer {
        return super.screenPermissions()
    }

    @EntityAccessContainer(
        EntityAccess(entityClass = Account::class, operations = [EntityOp.READ, EntityOp.UPDATE]),
        EntityAccess(entityClass = AccountRevision::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = MarketData::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = SystemAllocation::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = SystemDetail::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = SystemStd::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = System::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = PriceList::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PriceListDetail::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = ActivityDetail::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = KeyValueEntity::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = DimCustomers::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = AnalyticSet::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = RevenueType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = EquipmentType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = EquipmentCategory::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = Employee::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = EquipmentUtilizationDetail::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = Activity::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = ApplicationData::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE]
        ),
        EntityAccess(entityClass = EquipmentUtilization::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE]
        ),
        EntityAccess(entityClass = Depth::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = IntakeConfig::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = Materials::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = OtherMaterials::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = PumpConfig::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = PumpMaterials::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = PumpType::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = SealConfig::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = VaproConfig::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = Part::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartBoltDischargeHead::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartBoltIntake::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartCable::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartDrive::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartGC::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartGH::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartGS::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartMLE::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartMotor::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartMotorSeal::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartPump::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartSensor::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartXFMR::class, operations = [EntityOp.READ]),
        EntityAccess(
            entityClass = MotorType::class,
            operations = [EntityOp.CREATE, EntityOp.UPDATE, EntityOp.READ, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = Supplementary::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = SupplementaryDetail::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = SupplementaryDetailType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = Attachment::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = FileDescriptor::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = Country::class, operations = [EntityOp.READ]),

        EntityAccess(entityClass = CountrySetting::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = CountrySettingAnalyticDetail::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = CountrySettingEquipmentType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = CountrySettingRevenueType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = CountrySettingUtilizationValueType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = EquipmentUtilizationDetailValue::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = EquipmentUtilizationValueType::class, operations = [EntityOp.READ]),

        EntityAccess(entityClass = MeasurementUnit::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = MeasurementUnitSet::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = Project::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = ProjectAssignment::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = DirectSale::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = DirectSaleDetail::class, operations = [EntityOp.READ])
    )
    override fun entityPermissions(): EntityPermissionsContainer {
        return super.entityPermissions()
    }

    @EntityAttributeAccessContainer(
        EntityAttributeAccess(entityClass = MarketData::class, view = ["*"]),
        EntityAttributeAccess(entityClass = AccountRevision::class, view = ["*"]),
        EntityAttributeAccess(entityClass = SystemAllocation::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = SystemDetail::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = SystemStd::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = System::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = PriceList::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PriceListDetail::class, view = ["*"]),
        EntityAttributeAccess(entityClass = ActivityDetail::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = KeyValueEntity::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = DimCustomers::class, view = ["*"]),
        EntityAttributeAccess(entityClass = AnalyticSet::class, view = ["*"]),
        EntityAttributeAccess(entityClass = RevenueType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentCategory::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Employee::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentUtilizationDetail::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = Account::class,
            modify = ["appDetails", "actualAppDetail", "equipmentUtilizations", "actualEquipmentUtilization"],
            view = ["*"]
        ),
        EntityAttributeAccess(entityClass = Activity::class, view = ["*"]),
        EntityAttributeAccess(entityClass = ApplicationData::class, modify = ["*"], view = ["recordType"]),
        EntityAttributeAccess(entityClass = EquipmentUtilization::class, modify = ["*"], view = ["recordType"]),
        EntityAttributeAccess(entityClass = Depth::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = IntakeConfig::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = Materials::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = OtherMaterials::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = PumpConfig::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = PumpMaterials::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = PumpType::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = SealConfig::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = VaproConfig::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = Part::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartBoltDischargeHead::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartBoltIntake::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartCable::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartDrive::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartGC::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartGH::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartGS::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartMLE::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartMotor::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartMotorSeal::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartPump::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartSensor::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartXFMR::class, view = ["*"]),
        EntityAttributeAccess(entityClass = MotorType::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = Supplementary::class, view = ["*"]),
        EntityAttributeAccess(entityClass = SupplementaryDetail::class, view = ["*"]),
        EntityAttributeAccess(entityClass = SupplementaryDetailType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Attachment::class, view = ["*"]),
        EntityAttributeAccess(entityClass = FileDescriptor::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Country::class, view = ["*"]),
        EntityAttributeAccess(entityClass = CountrySetting::class, view = ["*"]),
        EntityAttributeAccess(entityClass = CountrySettingAnalyticDetail::class, view = ["*"]),
        EntityAttributeAccess(entityClass = CountrySettingEquipmentType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = CountrySettingRevenueType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = CountrySettingUtilizationValueType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentUtilizationDetailValue::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentUtilizationValueType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Project::class, view = ["*"]),
        EntityAttributeAccess(entityClass = ProjectAssignment::class, view = ["*"]),
        EntityAttributeAccess(entityClass = DirectSale::class, view = ["*"]),
        EntityAttributeAccess(entityClass = DirectSaleDetail::class, view = ["*"])
    )
    override fun entityAttributePermissions(): EntityAttributePermissionsContainer {
        return super.entityAttributePermissions()
    }

    @ScreenComponentAccess(screenId = "pfa_Account.edit",
        view = ["attachmentFragment.filesMultiUpload"]
    )
    override fun screenComponentPermissions(): ScreenComponentPermissionsContainer {
        return super.screenComponentPermissions()
    }

    @SpecificAccess(permissions = ["cuba.gui.filter.edit"])
    override fun specificPermissions(): SpecificPermissionsContainer {
        return super.specificPermissions()
    }

    override fun getLocName(): String {
        return "Application Engineer"
    }

    override fun getDescription(): String {
        return super.getDescription()
    }
}
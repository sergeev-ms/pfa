package com.borets.pfa.core.role

import com.borets.addon.pn.entity.*
import com.borets.attachments.entity.Attachment
import com.borets.pfa.entity.Employee
import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.AccountRevision
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.EquipmentCategory
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.appdata.SystemAllocation
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
import com.borets.pfa.entity.activity.Activity
import com.borets.pfa.entity.activity.ActivityDetail
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.customer.DimCustomers
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.price.PriceListDetail
import com.borets.pfa.entity.price.RevenueType
import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition
import com.haulmont.cuba.security.app.role.annotation.*
import com.haulmont.cuba.security.entity.EntityOp
import com.haulmont.cuba.security.role.*

@Role(name = SalesManagerRole.NAME, description = SalesManagerRole.DESCRIPTION)
class SalesManagerRole : AnnotatedRoleDefinition() {
    companion object {
        const val NAME = "sales-manager"
        const val DESCRIPTION = "Creates Activity plans for all Accounts, etc."
    }

    @ScreenAccess(screenIds = ["application-pfa", "pfa_Account.browse", "references-group", "pfa_DimCustomers.browse",
        "pfa_SystemStd.browse", "pfa_Employee.browse", "pfa_Account.edit", "pfa_ApplicationDataFragment",
        "pfa_ActivityPivot.edit", "pfa_PriceListPivot.edit", "pfa_MarketData.browse", "pfa_MarketData.edit",
        "pfa_MarketDataFragment", "pfa_AccountRevision.browse", "pfa_AccountRevision.edit", "pfa_SystemStd.edit",
        "pfa_ApplicationData.edit", "pfa_ApplicationData.browse", "pfa_EquipmentUtilizationFragment",
        "pfa_EquipmentUtilization.edit", "pfa_EquipmentUtilization.browse"])
    override fun screenPermissions(): ScreenPermissionsContainer {
        return super.screenPermissions()
    }

    @EntityAccessContainer(
        EntityAccess(entityClass = Account::class, operations = [EntityOp.READ, EntityOp.UPDATE, EntityOp.CREATE]),
        EntityAccess(entityClass = AccountRevision::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE]
        ),
        EntityAccess(entityClass = MarketData::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = ApplicationData::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE]
        ),
        EntityAccess(entityClass = SystemAllocation::class,
            operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = SystemDetail::class, operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE]),
        EntityAccess(entityClass = SystemStd::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = System::class, operations = [EntityOp.READ, EntityOp.CREATE, EntityOp.UPDATE]),
        EntityAccess(entityClass = PriceList::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PriceListDetail::class, operations = [EntityOp.READ]),
        EntityAccess(
            entityClass = Activity::class,
            operations = [EntityOp.CREATE, EntityOp.UPDATE, EntityOp.READ]
        ),
        EntityAccess(
            entityClass = ActivityDetail::class,
            operations = [EntityOp.CREATE, EntityOp.UPDATE, EntityOp.READ]
        ),
        EntityAccess(
            entityClass = KeyValueEntity::class,
            operations = [EntityOp.CREATE, EntityOp.UPDATE, EntityOp.READ, EntityOp.DELETE]
        ),
        EntityAccess(entityClass = DimCustomers::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = AnalyticSet::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = RevenueType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = EquipmentType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = EquipmentCategory::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = Employee::class, operations = [EntityOp.READ, EntityOp.UPDATE, EntityOp.CREATE]),
        EntityAccess(entityClass = EquipmentUtilization::class,
            operations = [EntityOp.CREATE, EntityOp.UPDATE, EntityOp.READ]
        ),
        EntityAccess(entityClass = EquipmentUtilizationDetail::class,
            operations = [EntityOp.READ, EntityOp.UPDATE, EntityOp.CREATE]
        ),
        EntityAccess(entityClass = Supplementary::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = SupplementaryDetail::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = SupplementaryDetailType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = FileDescriptor::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = OtherMaterials::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = MotorType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = Materials::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = IntakeConfig::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = Depth::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PumpConfig::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PumpMaterials::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PumpType::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = SealConfig::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = VaproConfig::class, operations = [EntityOp.READ]),
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
        EntityAccess(entityClass = PartOther::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartPump::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartSensor::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartUMB::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = PartXFMR::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = Attachment::class, operations = [EntityOp.READ])
    )
    override fun entityPermissions(): EntityPermissionsContainer {
        return super.entityPermissions()
    }

    @EntityAttributeAccessContainer(
        EntityAttributeAccess(entityClass = Account::class,
            modify = ["*"],
            view = ["supplementary", "marketDetails", "actualMarketDetail", "appDetails", "actualAppDetail",
                "equipmentUtilizations", "actualEquipmentUtilization"]
        ),
        EntityAttributeAccess(entityClass = MarketData::class, view = ["*"]),
        EntityAttributeAccess(entityClass = AccountRevision::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = ApplicationData::class, modify = ["*"], view = ["recordType"]),
        EntityAttributeAccess(entityClass = SystemAllocation::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = SystemDetail::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = SystemStd::class, view = ["*"]),
        EntityAttributeAccess(entityClass = System::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = PriceList::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PriceListDetail::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Activity::class, modify = ["*"], view = ["recordType"]),
        EntityAttributeAccess(entityClass = ActivityDetail::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = KeyValueEntity::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = DimCustomers::class, view = ["*"]),
        EntityAttributeAccess(entityClass = AnalyticSet::class, view = ["*"]),
        EntityAttributeAccess(entityClass = RevenueType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentCategory::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Employee::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentUtilization::class, modify = ["*"], view = ["recordType"]),
        EntityAttributeAccess(entityClass = EquipmentUtilizationDetail::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = Supplementary::class, view = ["*"]),
        EntityAttributeAccess(entityClass = SupplementaryDetail::class, view = ["*"]),
        EntityAttributeAccess(entityClass = SupplementaryDetailType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = FileDescriptor::class, view = ["*"]),
        EntityAttributeAccess(entityClass = OtherMaterials::class, view = ["*"]),
        EntityAttributeAccess(entityClass = MotorType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Materials::class, view = ["*"]),
        EntityAttributeAccess(entityClass = IntakeConfig::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Depth::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PumpConfig::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PumpMaterials::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PumpType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = SealConfig::class, view = ["*"]),
        EntityAttributeAccess(entityClass = VaproConfig::class, view = ["*"]),
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
        EntityAttributeAccess(entityClass = PartOther::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartPump::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartSensor::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartUMB::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PartXFMR::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Attachment::class, view = ["*"])
    )
    override fun entityAttributePermissions(): EntityAttributePermissionsContainer {
        return super.entityAttributePermissions()
    }

    @ScreenComponentAccess(screenId = "pfa_Account.edit",
        deny = ["createMarketDataBtn", "createAppDataBtn", "createUtilizationBtn"],
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
        return "Sales Manager"
    }
}
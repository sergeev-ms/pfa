package com.borets.pfa.core.role

import com.borets.pfa.entity.Employee
import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.AccountRevision
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.EquipmentCategory
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.marketdata.MarketData
import com.borets.pfa.entity.account.system.System
import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetail
import com.borets.pfa.entity.activity.Activity
import com.borets.pfa.entity.activity.ActivityDetail
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.customer.DimCustomers
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.price.PriceListDetail
import com.borets.pfa.entity.price.RevenueType
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition
import com.haulmont.cuba.security.app.role.annotation.*
import com.haulmont.cuba.security.entity.EntityOp
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer
import com.haulmont.cuba.security.role.EntityPermissionsContainer
import com.haulmont.cuba.security.role.ScreenComponentPermissionsContainer
import com.haulmont.cuba.security.role.ScreenPermissionsContainer

@Role(name = SalesPersonRole.NAME)
class SalesPersonRole : AnnotatedRoleDefinition() {
    companion object {
        const val NAME = "sales-person"
    }

    @ScreenAccess(screenIds = ["application-pfa", "pfa_Account.browse", "references-group", "pfa_DimCustomers.browse",
        "pfa_SystemStd.browse", "pfa_Employee.browse", "pfa_Account.edit", "pfa_ApplicationDataFragment", "pfa_ActivityPivot.edit",
        "pfa_PriceListPivot.edit", "pfa_MarketData.browse", "pfa_MarketData.edit", "pfa_MarketDataFragment",
        "pfa_AccountRevision.browse", "pfa_AccountRevision.edit", "pfa_SystemStd.edit", "pfa_ApplicationData.edit",
        "pfa_ApplicationData.browse", "pfa_EquipmentUtilizationFragment", "pfa_EquipmentUtilization.edit",
        "pfa_EquipmentUtilization.browse"])
    override fun screenPermissions(): ScreenPermissionsContainer {
        return super.screenPermissions()
    }

    @EntityAccessContainer(
        EntityAccess(entityClass = Account::class, operations = [EntityOp.READ, EntityOp.UPDATE]),
        EntityAccess(entityClass = AccountRevision::class, operations = [EntityOp.READ]),
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
        EntityAccess(entityClass = Employee::class, operations = [EntityOp.READ]),
        EntityAccess(entityClass = EquipmentUtilization::class,
            operations = [EntityOp.CREATE, EntityOp.UPDATE, EntityOp.READ]
        ),
        EntityAccess(entityClass = EquipmentUtilizationDetail::class,
            operations = [EntityOp.READ, EntityOp.UPDATE, EntityOp.CREATE]
        )
    )
    override fun entityPermissions(): EntityPermissionsContainer {
        return super.entityPermissions()
    }

    @EntityAttributeAccessContainer(
        EntityAttributeAccess(entityClass = Account::class,
            view = ["*"],
            modify = ["actualMarketDetail", "marketDetails", "appDetails", "actualAppDetail", "equipmentUtilizations", "actualEquipmentUtilization"]
        ),
        EntityAttributeAccess(entityClass = MarketData::class, view = ["*"]),
        EntityAttributeAccess(entityClass = AccountRevision::class, view = ["*"]),
        EntityAttributeAccess(entityClass = ApplicationData::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = SystemAllocation::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = SystemDetail::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = SystemStd::class, view = ["*"]),
        EntityAttributeAccess(entityClass = System::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = PriceList::class, view = ["*"]),
        EntityAttributeAccess(entityClass = PriceListDetail::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Activity::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = ActivityDetail::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = KeyValueEntity::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = DimCustomers::class, view = ["*"]),
        EntityAttributeAccess(entityClass = AnalyticSet::class, view = ["*"]),
        EntityAttributeAccess(entityClass = RevenueType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentType::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentCategory::class, view = ["*"]),
        EntityAttributeAccess(entityClass = Employee::class, view = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentUtilization::class, modify = ["*"]),
        EntityAttributeAccess(entityClass = EquipmentUtilizationDetail::class, modify = ["*"])
    )
    override fun entityAttributePermissions(): EntityAttributePermissionsContainer {
        return super.entityAttributePermissions()
    }

    @ScreenComponentAccess(screenId = "pfa_Account.edit", deny = ["createRevisionBtn", "createMarketDataBtn"])
    override fun screenComponentPermissions(): ScreenComponentPermissionsContainer {
        return super.screenComponentPermissions()
    }
}
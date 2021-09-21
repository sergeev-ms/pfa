package com.borets.pfa.web.screens.account

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.AccountRevision
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.marketdata.MarketData
import com.borets.pfa.entity.account.system.System
import com.borets.pfa.entity.account.supplementary.Supplementary
import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.borets.pfa.entity.activity.Activity
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.web.screens.account.appdata.applicationdata.ApplicationDataFragment
import com.borets.pfa.web.screens.account.marketdata.marketdata.MarketDataFragment
import com.borets.pfa.web.screens.account.system.copyToSystem
import com.borets.pfa.web.screens.account.system.reloadForCopy
import com.borets.pfa.web.screens.account.utilization.equipmentutilization.EquipmentUtilizationFragment
import com.borets.pfa.web.screens.activity.activity.input.ActivityPivotEdit
import com.borets.pfa.web.screens.price.pricelist.input.PriceListPivotEdit
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.DatatypeFormatter
import com.haulmont.cuba.core.global.EntityStates
import com.haulmont.cuba.core.global.ViewBuilder
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.model.*
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import com.haulmont.cuba.security.global.UserSession
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@UiController("pfa_Account.edit")
@UiDescriptor("account-edit.xml")
@EditedEntityContainer("accountDc")
class AccountEdit : StandardEditor<Account>() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var screenBuilders: ScreenBuilders
    @Inject
    private lateinit var entityStates: EntityStates
    @Inject
    private lateinit var userSession: UserSession
    @Inject
    private lateinit var datatypeFormatter: DatatypeFormatter

    @Inject
    private lateinit var actualRevisionDc: InstancePropertyContainer<AccountRevision>
    @Inject
    private lateinit var marketDataDc: InstancePropertyContainer<MarketData>
    @Inject
    private lateinit var applicationDataDc: InstancePropertyContainer<ApplicationData>
    @Inject
    private lateinit var equipmentUtilizationDc: InstanceContainer<EquipmentUtilization>
    @Inject
    private lateinit var equipmentUtilizationDl: InstanceLoader<EquipmentUtilization>
    @Inject
    private lateinit var accountDl: InstanceLoader<Account>
    @Inject
    private lateinit var priceListsDc: CollectionContainer<PriceList>
    @Inject
    private lateinit var priceListsDl: CollectionLoader<PriceList>
    @Inject
    private lateinit var activityPlansDc: CollectionContainer<Activity>
    @Inject
    private lateinit var activityPlansDl: CollectionLoader<Activity>
    @Inject
    private lateinit var supplementaryDl: InstanceLoader<Supplementary>
    @Inject
    private lateinit var supplementaryDc: InstanceContainer<Supplementary>

    @Inject
    private lateinit var applicationDataFragment: ApplicationDataFragment
    @Inject
    private lateinit var equipmentUtilizationFragment: EquipmentUtilizationFragment
    @Inject
    private lateinit var marketDataFragment: MarketDataFragment
    @Inject
    private lateinit var marketDataGb: GroupBoxLayout
    @Inject
    private lateinit var accountDataGb: GroupBoxLayout
    @Inject
    private lateinit var appDataGb: GroupBoxLayout
    @Inject
    private lateinit var equipmentUtilizationGb: GroupBoxLayout
    @Inject
    private lateinit var priceListsTable: Table<PriceList>
    @Inject
    private lateinit var activityPlansTable: Table<Activity>
    @Inject
    private lateinit var screenHeader: Label<String>

    @Subscribe
    private fun onAfterInit(@Suppress("UNUSED_PARAMETER") event: AfterInitEvent) {
        applicationDataFragment.setEditable(false)
        equipmentUtilizationFragment.setEditable(false)
        marketDataFragment.setEditable(false)
    }

    @Subscribe
    private fun onBeforeShow(@Suppress("UNUSED_PARAMETER") event: BeforeShowEvent) {
        accountDl.load()
    }

    @Subscribe
    private fun onAfterShow(@Suppress("UNUSED_PARAMETER") event: AfterShowEvent) {
        setWindowCaption()
    }

    @Subscribe("createRevisionBtn")
    private fun onCreateRevisionBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        screenBuilders.editor(AccountRevision::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer {
                it.account = editedEntity
                it.manager = editedEntity.actualRevision?.manager
                it.setType(editedEntity.actualRevision?.getType())
            }
            .withOpenMode(OpenMode.DIALOG)
            .build()
            .also {
                it.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        actualRevisionDc.setItem((event.screen as StandardEditor<AccountRevision>).editedEntity)
                    }
                }
            }.show()
    }

    @Subscribe("showRevisionsBtn")
    private fun onShowRevisionsBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        screenBuilders.lookup(AccountRevision::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }

    @Subscribe("createMarketDataBtn")
    private fun onCreateMarketDataBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        screenBuilders.editor(MarketData::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer { new ->
                new.account = editedEntity
                editedEntity.actualMarketDetail?.let { actual ->
                    new.copyFrom(actual)
                }
            }
            .withOpenMode(OpenMode.NEW_TAB)
            .build()
            .also {
                it.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        marketDataDc.setItem((event.screen as StandardEditor<MarketData>).editedEntity)
                    }
                }
            }.show()
    }

    @Subscribe("showMarketDetailsBtn")
    private fun onShowMarketDetailsBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        screenBuilders.lookup(MarketData::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }

    @Subscribe(id = "actualRevisionDc", target = Target.DATA_CONTAINER)
    private fun onActualRevisionDcItemChange(event: InstanceContainer.ItemChangeEvent<AccountRevision>) {
        event.item?.let {
            accountDataGb.caption = accountDataGb.caption?.format(
                it.getYearMonth()?.format(DateTimeFormatter.ofPattern("MMM yyyy", userSession.locale)))
            accountDataGb.contextHelpText = "Updated by ${it.createdBy} at ${datatypeFormatter.formatDateTime(it.createTs)}"
        }
    }

    @Subscribe(id = "marketDataDc", target = Target.DATA_CONTAINER)
    private fun onActualMarketDataDcItemChange(event: InstanceContainer.ItemChangeEvent<MarketData>) {
        event.item?.let {
            marketDataGb.caption = marketDataGb.caption?.format(
                it.getYearMonth()?.format(DateTimeFormatter.ofPattern("MMM yyyy", userSession.locale)))
            marketDataGb.contextHelpText = "Updated by ${it.createdBy} at ${datatypeFormatter.formatDateTime(it.createTs)}"
        }
    }

    @Subscribe(id = "applicationDataDc", target = Target.DATA_CONTAINER)
    private fun onActualAppDetailDcItemChange(event: InstanceContainer.ItemChangeEvent<ApplicationData>) {
        event.item?.let {
            appDataGb.caption = appDataGb.caption?.format(
                it.getYearMonth()?.format(DateTimeFormatter.ofPattern("MMM yyyy", userSession.locale)))
            appDataGb.contextHelpText = "Updated by ${it.createdBy} at ${datatypeFormatter.formatDateTime(it.createTs)}"
        }
    }

    @Subscribe(id = "equipmentUtilizationDc", target = Target.DATA_CONTAINER)
    private fun onEquipmentUtilizationDcItemChange(event: InstanceContainer.ItemChangeEvent<EquipmentUtilization>) {
        event.item?.let {
            equipmentUtilizationGb.caption = equipmentUtilizationGb.caption?.format(
                it.getYearMonth()?.format(DateTimeFormatter.ofPattern("MMM yyyy", userSession.locale)))
            equipmentUtilizationGb.contextHelpText = "Updated by ${it.createdBy} at ${datatypeFormatter.formatDateTime(it.createTs)}"
        }
    }


    @Subscribe("createAppDataBtn")
    private fun onCreateAppDataBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        screenBuilders.editor(ApplicationData::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer {
                it.account = editedEntity
                editedEntity.actualAppDetail?.let { currentAppData -> it.copyFrom(currentAppData) }
            }
            .withOpenMode(OpenMode.NEW_TAB)
            .build()
            .also {
                it.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        applicationDataDc.setItem((event.screen as StandardEditor<ApplicationData>).editedEntity)
                    }
                }
            }.show()
    }

    @Subscribe("showAppDetailsBtn")
    private fun onShowAppDetailsBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        screenBuilders.lookup(ApplicationData::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }

    private fun setWindowCaption() {
        if (!entityStates.isNew(editedEntity)) {
            screenHeader.value = getHeaderRecursive(editedEntity)
            window.caption = screenHeader.value
        }
    }

    private inline fun MarketData.copyFrom(other : MarketData) {
        listOf("trl", "arl", "runsNumber", "firstRunDuration", "wellCheckRate",
            "secondRunDuration", "thirdRunDuration", "thirdPlusRunDuration", "wellCount", "conversionRate", "oilPermits", "rigQty", "ducQty",
            "completion", "activityRate", "budget", "bShare", "wellMonitorQty", "bWellCount", "rentalCapex")
            .forEach {
                this.setValue(it, other.getValue(it))
            }
    }

    private inline fun ApplicationData.copyFrom(other : ApplicationData) {
        val newSystemAllocationList = other.systemAllocations?.map { otherSystemAllocation ->
            val newSystemAllocation = dataContext.create(SystemAllocation::class.java)
            newSystemAllocation.applicationData = this
            newSystemAllocation.system = otherSystemAllocation.system!!
                .reloadForCopy(dataManager)
                .copyToSystem<System>(dataManager)
                .also { dataContext.merge(it) }
            newSystemAllocation.run1 = otherSystemAllocation.run1
            newSystemAllocation.run2 = otherSystemAllocation.run2
            newSystemAllocation.run3 = otherSystemAllocation.run3
            newSystemAllocation.run3plus = otherSystemAllocation.run3plus
            return@map newSystemAllocation
        }
        this.systemAllocations = newSystemAllocationList?.toMutableList()
    }

    @Subscribe("priceListsTable.view")
    private fun onPriceListsTableView(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.editor(priceListsTable)
            .withScreenClass(PriceListPivotEdit::class.java)
            .editEntity(priceListsTable.singleSelected!!)
            .show()
    }

    @Subscribe("activityPlansTable.view")
    private fun onActivityPlansTableView(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.editor(activityPlansTable)
            .withScreenClass(ActivityPivotEdit::class.java)
            .editEntity(activityPlansTable.singleSelected!!)
            .show()
    }

    @Subscribe("tabSheet")
    private fun onTabSheetSelectedTabChange(@Suppress("UNUSED_PARAMETER") event: TabSheet.SelectedTabChangeEvent) {
        when (event.selectedTab.name) {
            "priceListsTab" ->
                if (priceListsDc.items.isEmpty()) {
                    priceListsDl.setParameter("container_accountDc", editedEntity)
                    priceListsDl.load()
                }
            "activityPlansTab" -> {
                if (activityPlansDc.items.isEmpty()) {
                    activityPlansDl.setParameter("container_accountDc", editedEntity)
                    activityPlansDl.load()
                }
            }
            "utilizationTab" -> {
                if (equipmentUtilizationDc.itemOrNull == null) {
                    editedEntity.actualEquipmentUtilization?.let {
                        equipmentUtilizationDl.setParameter("equipmentUtilizationId", it.id)
                        equipmentUtilizationDl.load()
                    }
                }
            }
            "supplementaryTab" -> {
                if (entityStates.isNew(editedEntity)) {
                    supplementaryDc.setItem(editedEntity.supplementary)
                } else {
                    supplementaryDl.setParameter("container_accountDc", editedEntity)
                    supplementaryDl.load()
                }
            }
        }
    }

    @Subscribe("createUtilizationBtn")
    private fun onCreateUtilizationBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        screenBuilders.editor(EquipmentUtilization::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer { it.account = editedEntity }
            .withOptions(MapScreenOptions(mutableMapOf(Pair("copyFrom", editedEntity.actualEquipmentUtilization)) as Map<String, Any>))
            .withOpenMode(OpenMode.NEW_TAB)
            .build()
            .also {
                it.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        val committedEntity = (event.screen as StandardEditor<EquipmentUtilization>).editedEntity
                        equipmentUtilizationDc.setItem(committedEntity)
                        editedEntity.actualEquipmentUtilization = committedEntity
                    }
                }
            }
            .show()
    }

    @Subscribe("showUtilizationsBtn")
    private fun onShowUtilizationsBtnClick(event: Button.ClickEvent) {
        screenBuilders.lookup(EquipmentUtilization::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }


    @Subscribe("priceListsTable.create")
    private fun onPriceListsTableCreate(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.editor(priceListsTable)
            .newEntity()
            .withScreenClass(PriceListPivotEdit::class.java)
            .withInitializer { it.account = editedEntity }
            .show()
    }

    @Subscribe("activityPlansTable.create")
    private fun onCreateActivityPlanBtnClick(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.editor(activityPlansTable)
            .newEntity()
            .withScreenClass(ActivityPivotEdit::class.java)
            .withInitializer { it.account = editedEntity }
            .show()
    }

    private fun getHeaderRecursive(account: Account): String? {
        val view = ViewBuilder.of(Account::class.java)
            .addAll("name", "parent")
            .build()
        var accountWithParent : Account = account
        if (!entityStates.isLoaded(accountWithParent, "parent")) {
            accountWithParent = dataManager.reload(account, view)
        }
        val header : String? = accountWithParent.name
        return if (accountWithParent.parent != null) {
            "${getHeaderRecursive(accountWithParent.parent!!)} \u2012 $header"
        } else
            header
    }
}



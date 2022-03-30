package com.borets.pfa.web.screens.account

import com.borets.addon.country.entity.Country
import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.AccountRevision
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.directsale.DirectSale
import com.borets.pfa.entity.account.directsale.DirectSaleDetail
import com.borets.pfa.entity.account.marketdata.MarketData
import com.borets.pfa.entity.account.supplementary.Supplementary
import com.borets.pfa.entity.account.system.System
import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetailValue
import com.borets.pfa.entity.activity.Activity
import com.borets.pfa.entity.customer.Customer
import com.borets.pfa.entity.customer.DimCustomers
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.project.Project
import com.borets.pfa.entity.project.ProjectAssignment
import com.borets.pfa.web.screens.account.appdata.applicationdata.ApplicationDataFragment
import com.borets.pfa.web.screens.account.marketdata.marketdata.MarketDataFragment
import com.borets.pfa.web.screens.account.system.copyToSystem
import com.borets.pfa.web.screens.account.system.reloadForCopy
import com.borets.pfa.web.screens.account.utilization.equipmentutilization.EquipmentUtilizationFragment
import com.borets.pfa.web.screens.activity.activity.input.ActivityPivotEdit
import com.borets.pfa.web.screens.price.pricelist.input.PriceListPivotEdit
import com.google.common.collect.ImmutableSet
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.gui.Dialogs
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.app.core.inputdialog.DialogActions
import com.haulmont.cuba.gui.app.core.inputdialog.DialogOutcome
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.model.*
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import com.haulmont.cuba.security.entity.EntityAttrAccess
import com.haulmont.cuba.security.entity.EntityOp
import com.haulmont.cuba.security.global.UserSession
import com.haulmont.cuba.web.widgets.CubaGrid
import com.vaadin.shared.ui.dnd.DropEffect
import com.vaadin.shared.ui.dnd.EffectAllowed
import com.vaadin.shared.ui.grid.DropMode
import com.vaadin.ui.components.grid.GridDragSource
import com.vaadin.ui.components.grid.GridDropTarget
import java.time.LocalDate
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
    private lateinit var timeSource: TimeSource
    @Inject
    private lateinit var dialogs: Dialogs
    @Inject
    private lateinit var messageBundle: MessageBundle
    @Inject
    private lateinit var security: Security

    @Inject
    private lateinit var actualRevisionDc: InstancePropertyContainer<AccountRevision>
    @Inject
    private lateinit var marketDataDc: InstancePropertyContainer<MarketData>
    @Inject
    private lateinit var applicationDataDc: InstancePropertyContainer<ApplicationData>
    @Inject
    private lateinit var equipmentUtilizationDc: InstancePropertyContainer<EquipmentUtilization>
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
    private lateinit var countryOptionsDl: CollectionLoader<Country>
    @Inject
    private lateinit var countryOptionsDc: CollectionContainer<Country>
    @Inject
    private lateinit var equipmentUtilizationDetailValueDl: CollectionLoader<EquipmentUtilizationDetailValue>
    @Inject
    private lateinit var equipmentUtilizationDetailValueDc: CollectionContainer<EquipmentUtilizationDetailValue>
    @Inject
    private lateinit var projectsOptionDl: CollectionLoader<Project>
    @Inject
    private lateinit var projectsDc: CollectionPropertyContainer<ProjectAssignment>
    @Inject
    private lateinit var projectsOptionDc: CollectionContainer<Project>
    @Inject
    private lateinit var directSalesDc: CollectionContainer<DirectSale>
    @Inject
    private lateinit var directSalesDl: CollectionLoader<DirectSale>
    @Inject
    private lateinit var customersDc: CollectionPropertyContainer<Customer>

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
    @Inject
    private lateinit var createMarketDataBtn: LinkButton
    @Inject
    private lateinit var createRevisionBtn: LinkButton
    @Inject
    private lateinit var createAppDataBtn: LinkButton
    @Inject
    private lateinit var createUtilizationBtn: LinkButton
    @Inject
    private lateinit var countryField: LookupField<Country>

    @Inject
    private lateinit var projectOptionTable: DataGrid<Project>
    @Inject
    private lateinit var assignedProjectsTable: DataGrid<ProjectAssignment>
    @Inject
    private lateinit var directSalesTable: Table<DirectSale>

    private var dragged: MutableSet<Project> = mutableSetOf()

    @Inject
    private lateinit var customersGrid: DataGrid<Customer>

    @Inject
    private lateinit var addCustomerBtn: Button

    @Inject
    private lateinit var deleteCustomerBtn: Button

    @Subscribe
    private fun onInit(@Suppress("UNUSED_PARAMETER") event: InitEvent) {
        applicationDataFragment.setEditable(false)
        equipmentUtilizationFragment.setEditable(false)
        marketDataFragment.setEditable(false)

        setupDragAndDrop()
        setupButtonsVisibility()
    }

    @Subscribe
    private fun onAfterInit(@Suppress("UNUSED_PARAMETER") event: AfterInitEvent) {
        countryOptionsDl.load() //early load to use results in InitEntityEvent
    }

    @Subscribe
    private fun onBeforeShow(@Suppress("UNUSED_PARAMETER") event: BeforeShowEvent) {
        accountDl.load()

        // TODO: 17.10.2021         Looks like there is the performance problem. Same data will be loaded with accountDl
        equipmentUtilizationDetailValueDl
            .setParameter("actualEquipmentUtilization", editedEntity.actualEquipmentUtilization)
        equipmentUtilizationDetailValueDl.load()

        projectsOptionDl.load()
    }

    @Subscribe
    private fun onInitEntity(event: InitEntityEvent<Account>) {
        if (countryOptionsDc.items.size == 1) {
            event.entity.country = countryOptionsDc.items[0]
        }
    }

    @Subscribe
    private fun onAfterShow(@Suppress("UNUSED_PARAMETER") event: AfterShowEvent) {
        setWindowCaption()
        if (countryOptionsDc.items.size == 1) {
            countryField.isEditable = false
        }
    }


    @Subscribe
    private fun onAfterCommitChanges(@Suppress("UNUSED_PARAMETER") event: AfterCommitChangesEvent) {
        reEnableButtons()
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
                it.active = editedEntity.actualRevision?.active ?: true
            }
            .withOpenMode(OpenMode.DIALOG)
            .build()
            .also {
                it.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        actualRevisionDc.setItem((event.screen as StandardEditor<AccountRevision>).editedEntity)
                        createRevisionBtn.isEnabled = false
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
                        createMarketDataBtn.isEnabled = false
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
                        createAppDataBtn.isEnabled = false
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

    @Install(to = "projectsOptionDl", target = Target.DATA_LOADER, type = Any::class, subject = "", required = true)
    private fun projectsOptionDlLoadDelegate(@Suppress("UNUSED_PARAMETER") loadContext: LoadContext<Project>?): MutableList<Project> {
        val customerIds = editedEntity.customers?.map { it.dimCustomerId }
        if (customerIds != null) {
            return dataManager.load(Project::class.java)
                .query(
                    """select p from pfa_Project p
                |where p.customerNo IN :customerIds
                |and NOT EXISTS( 
                |   select pa 
                |   from pfa_ProjectAssignment pa
                |   where pa.project = p and (pa.dateEnd IS NULL or pa.dateEnd > :today))
                |order by p.well""".trimMargin()
                )
                .parameter("customerIds", customerIds)
                .parameter("today", timeSource.now().toLocalDateTime())
                .view { it.addView(View.LOCAL) }
                .list()
        } else return mutableListOf()
    }

    private fun setWindowCaption() {
        if (!entityStates.isNew(editedEntity)) {
            screenHeader.value = getHeaderRecursive(editedEntity)
            window.caption = screenHeader.value
        }
    }

    private inline fun MarketData.copyFrom(other : MarketData) {
        listOf("trl", "arl", "runsNumber", "firstRunDuration", "wellCheckRate",
            "secondRunDuration", "thirdRunDuration", "thirdPlusRunDuration", "wellCount", "conversionRate", "oilPermits",
            "rigQty", "ducQty", "completion", "activityRate", "budget", "bShare", "wellMonitorQty", "bWellCount",
            "rentalCapex", "customerPullsInYear", "delayFactor", "customerRunbackInYear", "customerInstallInYear",
            "customerWellsClosingInYear", "newWellYear")
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
            "supplementaryTab" -> {
                if (entityStates.isNew(editedEntity)) {
                    supplementaryDc.setItem(editedEntity.supplementary)
                } else {
                    supplementaryDl.setParameter("container_accountDc", editedEntity)
                    supplementaryDl.load()
                }
            }
            "directSalesTab" -> {
                if (directSalesDc.items.isEmpty()) {
                    directSalesDl.setParameter("container_accountDc", editedEntity)
                    directSalesDl.load()
                }
            }
        }
    }

    @Subscribe("createUtilizationBtn")
    private fun onCreateUtilizationBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        @Suppress("UNCHECKED_CAST")
        val copyFromOption = MapScreenOptions(mutableMapOf(Pair("copyFrom", editedEntity.actualEquipmentUtilization)) as Map<String, Any>)
        screenBuilders.editor(EquipmentUtilization::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer { it.account = editedEntity }
            .withOptions(copyFromOption)
            .withOpenMode(OpenMode.NEW_TAB)
            .build()
            .also { screen ->
                screen.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        val committedEntity = (event.screen as StandardEditor<EquipmentUtilization>).editedEntity

                        equipmentUtilizationDetailValueDc.mutableItems.clear()
                        dataContext.modified
                            .filterIsInstance<EquipmentUtilizationDetailValue>()
                            .filter { it.detail?.equipmentUtilization == committedEntity }
                            .forEach { equipmentUtilizationDetailValueDc.mutableItems.add(it) }

                        equipmentUtilizationDc.setItem(committedEntity)
                        editedEntity.actualEquipmentUtilization = committedEntity
                        equipmentUtilizationFragment.initPivot()

                        createUtilizationBtn.isEnabled = false
                    }
                }
            }
            .show()
    }

    @Subscribe("showUtilizationsBtn")
    private fun onShowUtilizationsBtnClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
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

    @Subscribe("directSalesTable.create")
    private fun onDirectSalesTableCreate(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.editor(directSalesTable)
            .newEntity()
            .withInitializer { it.account = editedEntity }
            .show()
    }

    @Subscribe("directSalesTable.addForecast")
    private fun onDirectSalesTableAddForecast(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.editor(directSalesTable)
            .newEntity()
            .withInitializer {
                val directSaleReloaded = dataManager.reload(directSalesDc.item,
                    ViewBuilder.of(DirectSale::class.java)
                        .addView(View.LOCAL)
                        .add("account", View.MINIMAL)
                        .add("details") { detailsVb ->
                            detailsVb.addView(View.LOCAL).add("part", View.MINIMAL) }
                        .build()
                )
                it.copyFrom(directSaleReloaded)
                it.parent = directSaleReloaded
            }
            .show()
    }

    @Install(to = "countryField", subject = "optionIconProvider", type = Any::class, required = true,
        target = Target.COMPONENT)
    private fun countryFieldOptionIconProvider(country: Country?): String? {
        return country?.picture
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

    private fun reEnableButtons() {
        //These buttons disabled on click. Enable buttons after commit.
        createRevisionBtn.isEnabled = true
        createMarketDataBtn.isEnabled = true
        createAppDataBtn.isEnabled = true
        createUtilizationBtn.isEnabled = true
    }

    @Subscribe("projectOptionTable.assignToAccount")
    private fun onProjectOptionTableAssignToAccount(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        moveToRight(projectOptionTable.selected)
    }

    @Subscribe("assignedProjectsTable.removeAssign")
    private fun onAssignedProjectsTableRemoveAssign(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        dialogs.createInputDialog(this)
            .withCaption(messageBundle.getMessage("projectRemoveAssignInputDialog.caption"))
            .withParameter(InputParameter
                .localDateTimeParameter("date")
                .withCaption(messageBundle.getMessage("projectRemoveAssignInputDialog.dateParameter"))
                .withRequired(true)
                .withDefaultValue(timeSource.now().toLocalDateTime()))
            .withActions(DialogActions.OK_CANCEL)
            .withCloseListener { closeEvent ->
                if (closeEvent.closedWith(DialogOutcome.OK)) {
                    assignedProjectsTable.selected.forEach {
                        it.dateEnd = closeEvent.getValue("date")
                    }
                }
            }
            .show()
    }

    private fun moveToRight(projects: Set<Project>) {
        dialogs.createInputDialog(this)
            .withCaption(messageBundle.getMessage("projectAssignInputDialog.caption"))
            .withParameter(InputParameter
                .localDateTimeParameter("date")
                .withCaption(messageBundle.getMessage("projectAssignInputDialog.dateParameter"))
                .withRequired(true)
                .withDefaultValue(timeSource.now().toLocalDateTime()))
            .withActions(DialogActions.OK_CANCEL)
            .withCloseListener { closeEvent ->
                if (closeEvent.closedWith(DialogOutcome.OK)) {
                    projects.map {
                        dataContext.create(ProjectAssignment::class.java).apply {
                            account = editedEntity
                            project = it
                            dateStart = closeEvent.getValue("date")
                        }
                    }.also { projectsDc.mutableItems.addAll(it) }
                        .map { it.project }
                        .run { projectsOptionDc.mutableItems.removeAll(this) }
                }
            }
            .show()
    }

    private fun setupDragAndDrop() {
        @Suppress("UNCHECKED_CAST")
        val projectOptionsCubaGrid = (projectOptionTable.unwrap(CubaGrid::class.java) as CubaGrid<Project>)
        GridDragSource(projectOptionsCubaGrid).apply {
            effectAllowed = EffectAllowed.MOVE

            addGridDragStartListener {
                dragged.addAll(projectOptionTable.selected)
            }

            addGridDragEndListener {
                dragged.clear()
            }
        }

        @Suppress("UNCHECKED_CAST")
        val cubaGrid = assignedProjectsTable.unwrap(CubaGrid::class.java) as CubaGrid<Project>
        GridDropTarget(cubaGrid, DropMode.ON_GRID).apply {
            dropEffect = DropEffect.MOVE
            addGridDropListener {
                if (dragged.isNotEmpty())
                    moveToRight(ImmutableSet.copyOf(dragged))
            }
        }
    }

    @Install(to = "directSalesTable.date", subject = "formatter", type = Any::class, required = true,
        target = Target.COMPONENT)
    private fun directSalesTableDateFormatter(localDate: LocalDate?): String {
        return localDate?.format(DateTimeFormatter.ofPattern("MMM yyyy", userSession.locale)) ?: ""
    }

    private fun DirectSale.copyFrom(item: DirectSale) {
        probability = item.probability
        bShare = item.bShare
        account = item.account
        setStatus(item.getStatus())
        val copiedDetails = item.details
            ?.map {
                dataManager.create(DirectSaleDetail::class.java).apply {
                    directSale = this@copyFrom
                    part = it.part
                    price = it.price
                    length = it.length
                }
            }
            ?.toMutableList()
        details = copiedDetails
    }

    private fun setupButtonsVisibility() {
        createRevisionBtn.isVisible = security.isEntityOpPermitted(AccountRevision::class.java, EntityOp.CREATE)
        createMarketDataBtn.isVisible = security.isEntityOpPermitted(MarketData::class.java, EntityOp.CREATE)
        createAppDataBtn.isVisible = security.isEntityOpPermitted(ApplicationData::class.java, EntityOp.CREATE)
        createUtilizationBtn.isVisible = security.isEntityOpPermitted(EquipmentUtilization::class.java, EntityOp.CREATE)

        addCustomerBtn.isVisible = security.isEntityAttrPermitted(Account::class.java, "customers", EntityAttrAccess.MODIFY)
        deleteCustomerBtn.isVisible = security.isEntityAttrPermitted(Account::class.java, "customers", EntityAttrAccess.MODIFY)
    }

    @Subscribe("addCustomerBtn")
    private fun onAddCustomerButtonClick(@Suppress("UNUSED_PARAMETER") event: Button.ClickEvent) {
        screenBuilders.lookup(DimCustomers::class.java, this)
            .withOpenMode(OpenMode.DIALOG)
            .withSelectHandler {
                var aCustomer: Customer? = null
                it.map(getCustomer()).run {
                    customersDc.mutableItems.addAll(this)
                    if (this.isNotEmpty()) {
                        aCustomer = this[0]
                    }
                }
                if (aCustomer != null) {
                    customersGrid.scrollTo(aCustomer!!, DataGrid.ScrollDestination.END)
                }
            }
            .show()
    }

    private fun getCustomer(): (DimCustomers) -> Customer = {
        dataManager.load(Customer::class.java)
            .query("where e.dimCustomerId = :dimCustomerId")
            .view {vb -> vb.addView(View.MINIMAL).add("dimCustomer", View.MINIMAL)}
            .parameter("dimCustomerId", it.customerNo!!)
            .optional()
            .orElseGet {
                dataContext.create(Customer::class.java).apply {
                    name = it.customerName
                    dimCustomer = it
                }
            }
    }

}





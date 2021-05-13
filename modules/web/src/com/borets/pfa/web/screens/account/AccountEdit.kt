package com.borets.pfa.web.screens.account

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.AccountRevision
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.marketdata.MarketData
import com.borets.pfa.web.screens.account.appdata.applicationdata.ApplicationDataFragment
import com.haulmont.cuba.core.global.DatatypeFormatter
import com.haulmont.cuba.core.global.EntityStates
import com.haulmont.cuba.core.global.MetadataTools
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Button
import com.haulmont.cuba.gui.components.GroupBoxLayout
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.model.InstancePropertyContainer
import com.haulmont.cuba.gui.screen.Target
import com.haulmont.cuba.security.global.UserSession
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@UiController("pfa_Account.edit")
@UiDescriptor("account-edit.xml")
@EditedEntityContainer("accountDc")
@LoadDataBeforeShow
class AccountEdit : StandardEditor<Account>() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var screenBuilders: ScreenBuilders
    @Inject
    private lateinit var entityStates: EntityStates
    @Inject
    private lateinit var metadataTools: MetadataTools
    @Inject
    private lateinit var userSession: UserSession
    @Inject
    private lateinit var datatypeFormatter: DatatypeFormatter

    @Inject
    private lateinit var actualRevisionDc: InstancePropertyContainer<AccountRevision>
    @Inject
    private lateinit var actualMarketDataDc: InstancePropertyContainer<MarketData>
    @Inject
    private lateinit var applicationDataDc: InstancePropertyContainer<ApplicationData>

    @Inject
    private lateinit var marketDataGb: GroupBoxLayout
    @Inject
    private lateinit var accountDataGb: GroupBoxLayout
    @Inject
    private lateinit var appDataGb: GroupBoxLayout
    @Inject
    private lateinit var applicationDataFragment: ApplicationDataFragment

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        applicationDataFragment.setEditable(false)
    }


    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        setWindowCaption()
    }


    @Subscribe("createRevisionBtn")
    private fun onCreateRevisionBtnClick(event: Button.ClickEvent) {
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
    private fun onShowRevisionsBtnClick(event: Button.ClickEvent) {
        screenBuilders.lookup(AccountRevision::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }

    @Subscribe("createMarketDataBtn")
    private fun onCreateMarketDataBtnClick(event: Button.ClickEvent) {
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
                        actualMarketDataDc.setItem((event.screen as StandardEditor<MarketData>).editedEntity)
                    }
                }
            }.show()
    }

    @Subscribe("showMarketDetailsBtn")
    private fun onShowMarketDetailsBtnClick(event: Button.ClickEvent) {
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

    @Subscribe(id = "actualMarketDataDc", target = Target.DATA_CONTAINER)
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


    @Subscribe("createAppDataBtn")
    private fun onCreateAppDataBtnClick(event: Button.ClickEvent) {
        screenBuilders.editor(ApplicationData::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer {
                it.account = editedEntity
                it.copyFrom(applicationDataDc.item)
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
    private fun onShowAppDetailsBtnClick(event: Button.ClickEvent) {
        screenBuilders.lookup(ApplicationData::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }

    private fun setWindowCaption() {
        if (!entityStates.isNew(editedEntity)) {
            window.caption = metadataTools.getInstanceName(editedEntity)
        }
    }

    private inline fun MarketData.copyFrom(other : MarketData) {
        listOf("contractType", "applicationType", "fieldType", "trl", "arl", "runsNumber", "firstRunDuration",
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
            newSystemAllocation.system = otherSystemAllocation.system
            newSystemAllocation.run1 = otherSystemAllocation.run1
            newSystemAllocation.run2 = otherSystemAllocation.run2
            newSystemAllocation.run3 = otherSystemAllocation.run3
            newSystemAllocation.run3plus = otherSystemAllocation.run3plus
            return@map newSystemAllocation
        }
        this.systemAllocations = newSystemAllocationList?.toMutableList()
    }
}
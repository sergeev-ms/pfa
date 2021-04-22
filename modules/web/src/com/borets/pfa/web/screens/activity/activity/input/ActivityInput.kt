package com.borets.pfa.web.screens.activity.activity.input

import com.borets.pfa.entity.activity.*
import com.haulmont.cuba.core.app.keyvalue.KeyValueMetaProperty
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.gui.Notifications
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.components.data.ValueSource
import com.haulmont.cuba.gui.components.data.options.ListOptions
import com.haulmont.cuba.gui.components.data.value.ContainerValueSource
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.haulmont.cuba.gui.model.KeyValueContainer
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val mutableList: MutableList<YearMonth>
    get() {
        val months = mutableListOf<YearMonth>()
        return months
    }

@UiController("pfa_ActivityInput")
@UiDescriptor("activity-input.xml")
@LoadDataBeforeShow
class ActivityInput : Screen() {
    @Inject
    private lateinit var timeSource: TimeSource
    @Inject
    private lateinit var uiComponents: UiComponents
    @Inject
    private lateinit var userSessionSource: UserSessionSource
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var screenValidation: ScreenValidation

    @Inject
    private lateinit var activityInputDc: KeyValueContainer
    @Inject
    private lateinit var detailsDc: KeyValueCollectionContainer
    @Inject
    private lateinit var detailDg: DataGrid<KeyValueEntity>
    @Inject
    private lateinit var yearField: LookupField<Int>
    @Inject
    private lateinit var yearMonthField: LookupField<YearMonth>
    @Inject
    private lateinit var headerForm: Form
    @Inject
    private lateinit var createActivityBtn: Button

    private val months = mutableListOf<YearMonth>()

    private val commitContext : CommitContext = CommitContext()

    @Inject
    private lateinit var notifications: Notifications

    @Inject
    private lateinit var messageBundle: MessageBundle


    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        initYearsRange()
        initRows()
    }

    @Subscribe
    private fun onBeforeShow(event: BeforeShowEvent) {
        activityInputDc.setItem(dataManager.create(KeyValueEntity::class.java))
    }


    @Subscribe("yearField")
    private fun onYearFieldValueChange(event: HasValue.ValueChangeEvent<Int>) {
        event.value?.let {
            val options = mutableListOf<YearMonth>()
            for (i in 1..12) {
                options.add(YearMonth.of(it, i))
            }
            yearMonthField.options = ListOptions(options)
        }
    }

    @Subscribe("yearMonthField")
    private fun onYearMonthFieldValueChange(event: HasValue.ValueChangeEvent<YearMonth>) {
        event.value?.let {
            addMonthsToDetails(it, 12)
        }
    }

    private fun initYearsRange() {
        timeSource.now().year.let {
            yearField.options = ListOptions(it.rangeTo(it + 1).toList())
        }
    }

    private fun initRows() {
        JobType.values().forEach { jobType ->
            WellTag.values().forEach { wellTag ->
                WellEquip.values().forEach { wellEquip ->
                    ContractType.values().forEach { contractType ->
                        val element = KeyValueEntity().apply {
                            setValue("jobType", jobType)
                            setValue("wellTag", wellTag)
                            setValue("wellEquip", wellEquip)
                            setValue("contractType", contractType)
                        }
                        detailsDc.mutableItems.add(element)
                    }
                }
            }
        }
    }


    private fun addMonthsToDetails(startMonth: YearMonth, qty: Int) {

        for (i in 0..qty) {
            val month = startMonth.plusMonths(i.toLong())
            detailsDc.addProperty(month.toString(), Integer::class.java)

            //internal condition
            months.add(month)

            detailDg.addGeneratedColumn(month.toString(),
                    object : DataGrid.ColumnGenerator<KeyValueEntity, Component> {
                        override fun getValue(event: DataGrid.ColumnGeneratorEvent<KeyValueEntity>): Component {
                            @Suppress("UnstableApiUsage")
                            val textField = uiComponents.create(TextField.TYPE_INTEGER).apply {
                                setWidth("50px")
                                addValueChangeListener {
                                    event.item.setValue(month.toString(), it.value)
                                }
                                value = event.item.getValue(month.toString())
                            }
                            return textField
                        }

                        override fun getType(): Class<Component> {
                            return Component::class.java
                        }
                    })
                    .apply {
                        renderer = WebComponentRenderer<KeyValueEntity>()
                        caption = month.format(DateTimeFormatter.ofPattern("MMM yy", userSessionSource.locale))
                    }
        }
    }

    @Subscribe("closeBtn")
    private fun onCloseBtnClick(event: Button.ClickEvent) {
        close(WINDOW_DISCARD_AND_CLOSE_ACTION)
    }

    @Subscribe("createActivityBtn")
    private fun onCreateActivityClick(event: Button.ClickEvent) {
        screenValidation.validateUiComponents(headerForm).also {
            if (!it.isEmpty) {
                screenValidation.showValidationErrors(this, it)
                return
            }
        }

        val activity = createActivity(activityInputDc.item)
        createDetails(activity)

        dataManager.commit(commitContext)
        createActivityBtn.isEnabled = false
        notifications.create(Notifications.NotificationType.HUMANIZED)
                .withCaption(messageBundle.getMessage("successNotification.caption"))
                .show()

    }

    private fun createActivity(item: KeyValueEntity) : Activity {
        return dataManager.create(Activity::class.java).apply {
            this.account = item.getValue("account")
            this.year = item.getValue("year")
            setRecordType(item.getValue("recordType"))
        }.also {
            commitContext.addInstanceToCommit(it)
        }
    }

    private fun createDetails(activity: Activity) {
        detailsDc.items.forEach { kvEntity ->
            months.forEach { yearMonth ->
                kvEntity.getValue<Int?>(yearMonth.toString())?.let {
                    dataManager.create(ActivityDetail::class.java).apply {
                        this.activity = activity
                        this.setYearMonth(yearMonth)
                        this.setJobType(kvEntity.getValue("jobType"))
                        this.setWellTag(kvEntity.getValue("welTag"))
                        this.setWellEquip(kvEntity.getValue("wellEquip"))
                        this.setContractType(kvEntity.getValue("contractType"))
                        this.setRecordType(activity.getRecordType())
                        this.value = it
                    }.also {
                        commitContext.addInstanceToCommit(it)
                    }
                }
            }
        }
    }

}


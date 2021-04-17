package com.borets.pfa.web.screens.activity.activity

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.activity.Activity

@UiController("pfa_Activity.edit")
@UiDescriptor("activity-edit.xml")
@EditedEntityContainer("activityDc")
@LoadDataBeforeShow
class ActivityEdit : StandardEditor<Activity>()
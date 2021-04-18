package com.borets.pfa.web.screens.activity.activitydetail

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.activity.ActivityDetail

@UiController("pfa_ActivityDetail.edit")
@UiDescriptor("activity-detail-edit.xml")
@EditedEntityContainer("activityDetailDc")
@LoadDataBeforeShow
class ActivityDetailEdit : StandardEditor<ActivityDetail>()
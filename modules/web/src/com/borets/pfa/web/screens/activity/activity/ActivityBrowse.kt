package com.borets.pfa.web.screens.activity.activity

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.activity.Activity

@UiController("pfa_Activity.browse")
@UiDescriptor("activity-browse.xml")
@LookupComponent("activitiesTable")
@LoadDataBeforeShow
class ActivityBrowse : StandardLookup<Activity>()
package com.borets.pfa.web.screens.project.project

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.project.Project

@UiController("pfa_Project.browse")
@UiDescriptor("project-browse.xml")
@LookupComponent("projectsTable")
@LoadDataBeforeShow
class ProjectBrowse : StandardLookup<Project>()
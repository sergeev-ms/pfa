package com.borets.pfa.web.screens.project.project

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.project.Project

@UiController("pfa_Project.edit")
@UiDescriptor("project-edit.xml")
@EditedEntityContainer("projectDc")
@LoadDataBeforeShow
class ProjectEdit : StandardEditor<Project>()
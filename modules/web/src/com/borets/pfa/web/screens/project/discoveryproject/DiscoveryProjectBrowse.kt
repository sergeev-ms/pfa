package com.borets.pfa.web.screens.project.discoveryproject

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.project.DiscoveryProject

@UiController("pfa_DiscoveryProject.browse")
@UiDescriptor("discovery-project-browse.xml")
@LookupComponent("discoveryProjectsTable")
@LoadDataBeforeShow
class DiscoveryProjectBrowse : StandardLookup<DiscoveryProject>()
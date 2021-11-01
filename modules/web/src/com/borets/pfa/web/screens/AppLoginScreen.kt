package com.borets.pfa.web.screens

import com.haulmont.cuba.gui.Route
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.HBoxLayout
import com.haulmont.cuba.gui.components.Label
import com.haulmont.cuba.gui.screen.Subscribe
import com.haulmont.cuba.gui.screen.UiController
import com.haulmont.cuba.gui.screen.UiDescriptor
import com.haulmont.cuba.web.app.login.LoginScreen
import com.haulmont.cuba.web.gui.screen.ScreenDependencyUtils
import com.vaadin.ui.Dependency
import javax.inject.Inject


@Route(path = "login", root = true)
@UiController("app-login")
@UiDescriptor("app-login-screen.xml")
class AppLoginScreen : LoginScreen(){

    @Inject
    private lateinit var bottomPanel: HBoxLayout

    @Inject
    private lateinit var poweredByLink: Label<String>

    @Subscribe
    fun onAppLoginScreenInit(event: InitEvent) {
        loadStyles()
        initBottomPanel()
    }

    @Subscribe("submit")
    fun onSubmit(event: Action.ActionPerformedEvent) {
        login()
    }

    private fun loadStyles() {
        ScreenDependencyUtils.addScreenDependency(this,
                "vaadin://brand-login-screen/login.css", Dependency.Type.STYLESHEET)
    }

    private fun initBottomPanel() {
        if (!globalConfig.localeSelectVisible) {
            poweredByLink.alignment = Component.Alignment.MIDDLE_CENTER;

            if (!webConfig.loginDialogPoweredByLinkVisible) {
                bottomPanel.isVisible = false
            }
        }
    }
}

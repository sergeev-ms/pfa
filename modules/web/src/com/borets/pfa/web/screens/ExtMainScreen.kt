package com.borets.pfa.web.screens

import com.haulmont.cuba.gui.components.Label
import com.haulmont.cuba.gui.screen.MessageBundle
import com.haulmont.cuba.gui.screen.Subscribe
import com.haulmont.cuba.gui.screen.UiController
import com.haulmont.cuba.gui.screen.UiDescriptor
import com.haulmont.cuba.web.app.main.MainScreen
import javax.inject.Inject


@UiController("extMainScreen")
@UiDescriptor("ext-main-screen.xml")
class ExtMainScreen : MainScreen() {
    @Inject
    private lateinit var messageBundle: MessageBundle

    @Inject
    private lateinit var initialLayoutLabel: Label<String>

    @Subscribe
    private fun onInit(event: InitEvent) {
        initialLayoutLabel.value = messageBundle.getMessage("initialLayoutLabel.value")
    }

}
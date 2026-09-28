package com.nuvio.app.features.player

import kotlin.test.Test
import kotlin.test.assertContains

class DesktopPlayerKeyboardShortcutsTest {
    private val controlsSource by lazy {
        checkNotNull(javaClass.classLoader.getResource("player-ui/controls.js")).readText()
    }

    @Test
    fun `enhanced shortcut keys are handled by desktop controls`() {
        assertContains(controlsSource, "event.code === \"Space\"")
        assertContains(controlsSource, "case \"ArrowLeft\"")
        assertContains(controlsSource, "case \"ArrowRight\"")
        assertContains(controlsSource, "event.key === \"Escape\"")
    }

    @Test
    fun `shortcuts yield to modals and text entry`() {
        assertContains(controlsSource, "if (activeModal || isTextEntryTarget(event.target)) return;")
        assertContains(controlsSource, "if (event.key === \"Escape\" && activeModal)")
    }
}

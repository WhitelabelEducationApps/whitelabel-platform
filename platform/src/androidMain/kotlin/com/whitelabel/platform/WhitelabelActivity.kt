package com.whitelabel.platform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Base Activity for all whitelabel apps.
 * Subclasses must implement [createContent] to provide app-specific theme and navigation.
 */
abstract class WhitelabelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            createContent()
        }
    }

    /**
     * Provide the app's theme wrapper and navigation.
     * Example:
     * ```
     * @Composable
     * override fun createContent() {
     *     MuseumTheme {
     *         Surface(...) {
     *             AppNavigation()
     *         }
     *     }
     * }
     * ```
     */
    @Composable
    abstract fun createContent()
}

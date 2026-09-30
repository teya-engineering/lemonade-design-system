package com.teya.lemonade

import android.os.Build
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.N_MR1, Build.VERSION_CODES.R])
class ToastNavigationBarTest {
    @Test
    fun `a toast that hides the navigation bar shows a window that never takes focus`() {
        val activity = Robolectric
            .buildActivity(ComponentActivity::class.java)
            .setup()
            .get()
        var toasts: LemonadeToastState? = null
        activity.setContent {
            LemonadeTheme {
                LemonadeToastHost(hideNavigationBar = true) {
                    toasts = LocalLemonadeToastState.current
                }
            }
        }
        shadowOf(Looper.getMainLooper()).idle()

        checkNotNull(toasts).show(label = "Added to the cart")
        shadowOf(Looper.getMainLooper()).idle()

        val dialog = assertNotNull(ShadowDialog.getLatestDialog())
        assertTrue(dialog.isShowing)
        val window = checkNotNull(dialog.window)
        assertEquals(
            expected = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            actual = window.attributes.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            val hideNavigation = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            @Suppress("DEPRECATION")
            assertEquals(
                expected = hideNavigation,
                actual = window.decorView.systemUiVisibility and hideNavigation,
            )
        }
    }
}

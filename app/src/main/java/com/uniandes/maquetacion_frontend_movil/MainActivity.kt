package com.uniandes.maquetacion_frontend_movil

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    private lateinit var screenContainer: FrameLayout
    private lateinit var homeView: HomeAlarmsView
    private var isShowingSecondaryScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)
        screenContainer = findViewById(R.id.screenContainer)
        homeView = HomeAlarmsView(this).apply {
            contentDescription = getString(R.string.home_alarms_accessibility)
            onAlarmClick = { alarmIndex -> showEditAlarm(alarmIndex) }
            onSettingsClick = ::showSettings
        }
        showHome()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isShowingSecondaryScreen) showHome() else finish()
            }
        })
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showHome() {
        isShowingSecondaryScreen = false
        screenContainer.removeAllViews()
        screenContainer.addView(
            homeView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    private fun showEditAlarm(alarmIndex: Int) {
        isShowingSecondaryScreen = true
        val editView = EditAlarmView(this).apply {
            contentDescription = getString(R.string.edit_alarm_accessibility)
            setInitialAlarm(
                homeView.getAlarmTime(alarmIndex),
                homeView.getAlarmPeriod(alarmIndex),
            )
            onBackClick = ::showHome
            onSaveClick = { time, period ->
                homeView.updateAlarmTime(alarmIndex, time, period)
                showHome()
            }
        }
        screenContainer.removeAllViews()
        screenContainer.addView(
            editView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    private fun showSettings() {
        isShowingSecondaryScreen = true
        val settingsView = SettingsView(this).apply {
            contentDescription = getString(R.string.settings_accessibility)
            onBackClick = ::showHome
            onOptionClick = { option ->
                when (option) {
                    SettingsView.SettingsOption.EDIT_PROFILE -> showEditProfile()
                    SettingsView.SettingsOption.CHANGE_PASSWORD -> showChangePassword()
                    else -> Unit
                }
            }
        }
        screenContainer.removeAllViews()
        screenContainer.addView(
            settingsView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    private fun showEditProfile() {
        isShowingSecondaryScreen = true
        val profileView = EditProfileView(this).apply {
            contentDescription = "Editar perfil"
            onBackClick = ::showSettings
            onSaveClick = ::showSettings
        }
        screenContainer.removeAllViews()
        screenContainer.addView(
            profileView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    private fun showChangePassword() {
        isShowingSecondaryScreen = true
        val passwordView = ChangePasswordView(this).apply {
            contentDescription = "Cambiar contraseña"
            onBackClick = ::showSettings
            onSaveClick = ::showSettings
        }
        screenContainer.removeAllViews()
        screenContainer.addView(
            passwordView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
    }
}

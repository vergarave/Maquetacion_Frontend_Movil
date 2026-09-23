package com.uniandes.maquetacion_frontend_movil

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withScale

/** Pixel-accurate rendering of the 390 x 844 "Ajustes" Figma frame. */
class SettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var onBackClick: (() -> Unit)? = null
    var onOptionClick: ((SettingsOption) -> Unit)? = null
    private var profileName = "Nombre Apellido"
    private var profileEmail = "usuario@example.com"

    enum class SettingsOption {
        EDIT_PROFILE,
        CHANGE_PASSWORD,
        NOTIFICATIONS,
        LOG_OUT,
    }

    private data class MenuItem(
        val label: String,
        val top: Float,
        val iconResource: Int,
        val option: SettingsOption,
    )

    private val menuItems = arrayOf(
        MenuItem("Editar perfil", 380f, R.drawable.ic_settings_edit_profile, SettingsOption.EDIT_PROFILE),
        MenuItem("Cambiar contrase\u00F1a", 480f, R.drawable.ic_settings_password, SettingsOption.CHANGE_PASSWORD),
        MenuItem("Notificaciones", 580f, R.drawable.ic_settings_notifications, SettingsOption.NOTIFICATIONS),
        MenuItem("Cerrar sesi\u00F3n", 680f, R.drawable.ic_settings_logout, SettingsOption.LOG_OUT),
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        isDither = true
    }
    private val baseTypeface = ResourcesCompat.getFont(context, R.font.quicksand)
        ?: Typeface.create("sans-serif", Typeface.NORMAL)
    private val medium = weightedTypeface(500)
    private val semiBold = weightedTypeface(600)
    private val bold = weightedTypeface(700)
    private val backIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_alarm_back, context.theme)
    private val avatar = BitmapFactory.decodeResource(resources, R.drawable.settings_avatar)
    private val menuIcons = menuItems.associate { item ->
        item.iconResource to ResourcesCompat.getDrawable(resources, item.iconResource, context.theme)
    }

    private var designScale = 1f

    init {
        isClickable = true
        isFocusable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    fun setProfile(name: String, email: String) {
        profileName = name
        profileEmail = email
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(BACKGROUND)
        designScale = width / DESIGN_WIDTH
        canvas.withScale(designScale, designScale) {
            drawHeader(this)
            drawProfile(this)
            menuItems.forEach { drawMenuItem(this, it) }
        }
    }

    private fun drawHeader(canvas: Canvas) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawCircle(41f, 71f, 21f, paint)
        backIcon?.setBounds(31, 62, 52, 81)
        backIcon?.draw(canvas)
        drawCenteredText(canvas, "Ajustes", 195f, 80f, 24f, PRIMARY, bold)
    }

    private fun drawProfile(canvas: Canvas) {
        avatar?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, RectF(136f, 135f, 254f, 253f), paint)
        }
        drawCenteredText(canvas, profileName, 195f, 300f, 32f, TEXT_PRIMARY, semiBold)
        drawCenteredText(canvas, profileEmail, 195f, 330f, 16f, SECONDARY, semiBold)
    }

    private fun drawMenuItem(canvas: Canvas, item: MenuItem) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(21f, item.top, 371f, item.top + 80f, 30f, 30f, paint)
        menuIcons[item.iconResource]?.let { icon ->
            icon.setBounds(36, item.top.toInt() + 19, 78, item.top.toInt() + 61)
            icon.draw(canvas)
        }
        drawText(canvas, item.label, 90f, item.top + 49f, 20f, SECONDARY, medium)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return true
        val x = event.x / designScale
        val y = event.y / designScale

        if (x in 17f..65f && y in 47f..95f) {
            performClick()
            onBackClick?.invoke()
            return true
        }

        if (x in 21f..371f) {
            menuItems.firstOrNull { y in it.top..(it.top + 80f) }?.let { item ->
                performClick()
                onOptionClick?.invoke(item.option)
                return true
            }
        }
        return true
    }

    private fun drawText(
        canvas: Canvas,
        value: String,
        x: Float,
        baseline: Float,
        size: Float,
        color: Int,
        typeface: Typeface,
    ) {
        setTextPaint(size, typeface, color)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(value, x, baseline, paint)
    }

    private fun drawCenteredText(
        canvas: Canvas,
        value: String,
        centerX: Float,
        baseline: Float,
        size: Float,
        color: Int,
        typeface: Typeface,
    ) {
        setTextPaint(size, typeface, color)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(value, centerX, baseline, paint)
    }

    private fun setTextPaint(size: Float, typeface: Typeface, color: Int) {
        paint.reset()
        paint.isAntiAlias = true
        paint.isSubpixelText = true
        paint.isDither = true
        paint.style = Paint.Style.FILL
        paint.color = color
        paint.textSize = size
        paint.typeface = typeface
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun weightedTypeface(weight: Int): Typeface =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface.create(baseTypeface, weight, false)
        } else {
            Typeface.create(baseTypeface, if (weight >= 600) Typeface.BOLD else Typeface.NORMAL)
        }

    companion object {
        private const val DESIGN_WIDTH = 390f
        private val BACKGROUND = Color.rgb(28, 17, 17)
        private val CARD = Color.rgb(53, 39, 39)
        private val PRIMARY = Color.rgb(255, 177, 153)
        private val TEXT_PRIMARY = Color.rgb(244, 222, 222)
        private val SECONDARY = Color.rgb(198, 197, 212)
    }
}

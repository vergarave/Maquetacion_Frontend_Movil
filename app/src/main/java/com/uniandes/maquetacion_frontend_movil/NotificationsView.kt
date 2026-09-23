package com.uniandes.maquetacion_frontend_movil

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withScale

/** Rendering of the 390 x 844 "Notificaciones" Figma frame. */
class NotificationsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var onBackClick: (() -> Unit)? = null
    var onSaveClick: ((BooleanArray) -> Unit)? = null

    private data class NotificationOption(
        val title: String,
        val subtitle: String,
        val top: Float,
    )

    private val options = arrayOf(
        NotificationOption("Recordatorio para dormir", "Avisa 30 min antes", 290f),
        NotificationOption("Resumen de despertar", "Guardar estadísticas breves", 420f),
        NotificationOption("Omitir modo silencio", "Permitir que la alarma suene", 550f),
    )
    private val enabled = BooleanArray(options.size) { true }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        isDither = true
    }
    private val baseTypeface = ResourcesCompat.getFont(context, R.font.quicksand)
        ?: Typeface.create("sans-serif", Typeface.NORMAL)
    private val medium = weightedTypeface(500)
    private val semiBold = weightedTypeface(600)
    private val bold = weightedTypeface(700)
    private val backIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_alarm_back, context.theme)

    private var designScale = 1f

    init {
        isClickable = true
        isFocusable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    fun setEnabledStates(states: BooleanArray) {
        states.copyInto(enabled, endIndex = minOf(states.size, enabled.size))
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(BACKGROUND)
        designScale = width / DESIGN_WIDTH
        canvas.withScale(designScale, designScale) {
            drawHeader(this)
            drawDescription(this)
            options.forEachIndexed { index, option -> drawOption(this, option, enabled[index]) }
            drawSaveButton(this)
        }
    }

    private fun drawHeader(canvas: Canvas) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawCircle(41f, 71f, 21f, paint)
        backIcon?.setBounds(31, 62, 52, 81)
        backIcon?.draw(canvas)
        drawCenteredText(canvas, "Notificaciones", 195f, 80f, 24f, PRIMARY, bold)
    }

    private fun drawDescription(canvas: Canvas) {
        drawCenteredText(canvas, "Personaliza los avisos para", 195f, 158f, 16f, TEXT_PRIMARY, medium)
        drawCenteredText(canvas, "preparar tu descanso y", 195f, 178f, 16f, TEXT_PRIMARY, medium)
        drawCenteredText(canvas, "acompañar tus mañanas.", 195f, 198f, 16f, TEXT_PRIMARY, medium)
    }

    private fun drawOption(canvas: Canvas, option: NotificationOption, isEnabled: Boolean) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(20f, option.top, 370f, option.top + 100f, 30f, 30f, paint)
        drawText(canvas, option.title, 40f, option.top + 44.5f, 16f, TEXT_PRIMARY, semiBold)
        drawText(canvas, option.subtitle, 40f, option.top + 66.5f, 14f, SECONDARY, medium)
        drawSwitch(canvas, option.top + 34f, isEnabled)
    }

    private fun drawSwitch(canvas: Canvas, top: Float, isEnabled: Boolean) {
        paint.color = if (isEnabled) PRIMARY else DAY_SELECTED
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(294f, top, 346f, top + 32f, 16f, 16f, paint)
        canvas.drawCircle(if (isEnabled) 330f else 310f, top + 16f, 12f, whitePaint())
    }

    private fun drawSaveButton(canvas: Canvas) {
        paint.color = PRIMARY
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(20f, 764f, 370f, 824f, 30f, 30f, paint)
        drawCenteredText(canvas, "Guardar cambios", 195f, 803f, 20f, PERIOD_SELECTED, semiBold)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return true
        val x = event.x / designScale
        val y = event.y / designScale
        when {
            x in 17f..65f && y in 47f..95f -> {
                performClick()
                onBackClick?.invoke()
            }
            x in 280f..360f -> {
                options.forEachIndexed { index, option ->
                    if (y in option.top..(option.top + 100f)) {
                        enabled[index] = !enabled[index]
                        performClick()
                        invalidate()
                        return true
                    }
                }
            }
            x in 20f..370f && y in 764f..824f -> {
                performClick()
                onSaveClick?.invoke(enabled.copyOf())
            }
        }
        return true
    }

    private fun whitePaint(): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }

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
        private val DAY_SELECTED = Color.rgb(86, 78, 9)
        private val PERIOD_SELECTED = Color.rgb(138, 33, 0)
    }
}

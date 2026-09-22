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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withScale

class HomeAlarmsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var onAlarmClick: ((Int) -> Unit)? = null
    var onSettingsClick: (() -> Unit)? = null

    private data class Alarm(
        var time: String,
        val label: String,
        val selectedDays: BooleanArray,
        var period: String = "AM",
        var enabled: Boolean = true,
    )

    private val alarms = arrayOf(
        Alarm("7:30", "Clase de 9", booleanArrayOf(false, true, false, true, false, false, false)),
        Alarm("8:30", "Clase de 11", booleanArrayOf(true, false, true, false, true, false, false)),
        Alarm("6:00", "Entreno", booleanArrayOf(false, false, false, false, false, true, false)),
    )

    private val dayLabels = arrayOf("L", "M", "M", "J", "V", "S", "D")
    private val cardTops = floatArrayOf(350f, 515f, 680f)
    private val dayLefts = floatArrayOf(41f, 86f, 131f, 176f, 221f, 266f, 311f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        isDither = true
    }
    private val baseTypeface = ResourcesCompat.getFont(context, R.font.quicksand)
        ?: Typeface.create("sans-serif", Typeface.NORMAL)
    private val regular = weightedTypeface(400)
    private val medium = weightedTypeface(500)
    private val semiBold = weightedTypeface(600)
    private val bold = weightedTypeface(700)
    private val settingsIcon = AppCompatResources.getDrawable(context, R.drawable.ic_settings_alarm)

    private var designScale = 1f

    init {
        isClickable = true
        isFocusable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(BACKGROUND)

        designScale = width / DESIGN_WIDTH
        canvas.withScale(designScale, designScale) {
            drawHeader(this)
            alarms.forEachIndexed { index, alarm ->
                drawAlarmCard(this, cardTops[index], alarm)
            }
        }
    }

    private fun drawHeader(canvas: Canvas) {
        drawCenteredText(canvas, "Amanecer", 195f, 82f, 32f, PRIMARY, bold)

        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawCircle(350f, 70f, 20f, paint)
        settingsIcon?.setBounds(340, 60, 360, 80)
        settingsIcon?.draw(canvas)

        drawCenteredText(
            canvas,
            "PR\u00D3XIMA ALARMA EN 8H 30M",
            195f,
            140f,
            16f,
            SECONDARY,
            semiBold,
        )
        drawText(canvas, "7:00", 74f, 265f, 128f, NEXT_ALARM, regular)
        drawCenteredText(canvas, "AM", 195f, 318f, 36f, PRIMARY, medium)
    }

    private fun drawAlarmCard(canvas: Canvas, top: Float, alarm: Alarm) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(20f, top, 370f, top + 140f, 30f, 30f, paint)

        drawText(canvas, alarm.time, 41f, top + 49f, 32f, TEXT_PRIMARY, medium)
        setTextPaint(32f, medium, TEXT_PRIMARY)
        val periodX = 41f + paint.measureText(alarm.time) + 8f
        drawText(canvas, alarm.period, periodX, top + 48f, 20f, SECONDARY, medium)
        drawText(canvas, alarm.label, 41f, top + 78f, 16f, SECONDARY, semiBold)

        dayLabels.forEachIndexed { index, label ->
            paint.color = if (alarm.selectedDays[index]) DAY_SELECTED else BACKGROUND
            paint.style = Paint.Style.FILL
            canvas.drawCircle(dayLefts[index] + 17.5f, top + 107.5f, 17.5f, paint)
            drawCenteredText(
                canvas,
                label,
                dayLefts[index] + 17.5f,
                top + 113.5f,
                16f,
                DAY_TEXT,
                semiBold,
            )
        }

        val switchColor = if (alarm.enabled) PRIMARY else DAY_SELECTED
        paint.color = switchColor
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(294f, top + 31f, 346f, top + 63f, 16f, 16f, paint)
        val thumbCenterX = if (alarm.enabled) 330f else 310f
        canvas.drawCircle(thumbCenterX, top + 47f, 12f, whitePaint())
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

    private fun whitePaint(): Paint = paint.apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return true

        val x = event.x / designScale
        val y = event.y / designScale
        alarms.forEachIndexed { alarmIndex, alarm ->
            val top = cardTops[alarmIndex]
            if (x in 286f..354f && y in (top + 23f)..(top + 71f)) {
                alarm.enabled = !alarm.enabled
                performClick()
                invalidate()
                return true
            }
            dayLefts.forEachIndexed { dayIndex, left ->
                if (x in left..(left + 35f) && y in (top + 90f)..(top + 125f)) {
                    alarm.selectedDays[dayIndex] = !alarm.selectedDays[dayIndex]
                    performClick()
                    invalidate()
                    return true
                }
            }
        }

        if (x in 20f..370f) {
            cardTops.forEachIndexed { alarmIndex, top ->
                if (y in top..(top + 140f)) {
                    performClick()
                    onAlarmClick?.invoke(alarmIndex)
                    return true
                }
            }
        }

        if (x in 326f..374f && y in 46f..94f) {
            performClick()
            onSettingsClick?.invoke()
            return true
        }
        return true
    }

    fun getAlarmTime(index: Int): String = alarms.getOrNull(index)?.time ?: "7:30"

    fun getAlarmPeriod(index: Int): String = alarms.getOrNull(index)?.period ?: "AM"

    fun updateAlarmTime(index: Int, time: String, period: String) {
        alarms.getOrNull(index)?.let { alarm ->
            alarm.time = time
            alarm.period = period
            invalidate()
        }
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
        private val NEXT_ALARM = Color.rgb(188, 194, 255)
        private val TEXT_PRIMARY = Color.rgb(244, 222, 222)
        private val SECONDARY = Color.rgb(198, 197, 212)
        private val DAY_TEXT = Color.rgb(186, 173, 58)
        private val DAY_SELECTED = Color.rgb(86, 78, 9)
    }
}

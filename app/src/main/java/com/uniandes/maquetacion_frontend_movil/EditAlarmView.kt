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
import androidx.core.graphics.withRotation
import androidx.core.graphics.withScale

class EditAlarmView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var onBackClick: (() -> Unit)? = null
    var onSaveClick: ((time: String, period: String) -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        isDither = true
    }
    private val baseTypeface = ResourcesCompat.getFont(context, R.font.quicksand)
        ?: Typeface.create("sans-serif", Typeface.NORMAL)
    private val regular = weightedTypeface(400)
    private val semiBold = weightedTypeface(600)
    private val bold = weightedTypeface(700)
    private val chevron = ResourcesCompat.getDrawable(
        resources,
        R.drawable.ic_alarm_chevron_down,
        context.theme,
    )
    private val backIcon = ResourcesCompat.getDrawable(
        resources,
        R.drawable.ic_alarm_back,
        context.theme,
    )

    private val dayLabels = arrayOf("L", "M", "M", "J", "V", "S", "D")
    private val dayLefts = floatArrayOf(43f, 88f, 133f, 178f, 223f, 268f, 313f)
    private val selectedDays = BooleanArray(7)

    private var designScale = 1f
    private var hour = 7
    private var minute = 30
    private var isAm = true
    private var sunriseProgress = 0f
    private var volumeProgress = 0f

    init {
        isClickable = true
        isFocusable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    fun setInitialAlarm(time: String, period: String) {
        val parts = time.split(':')
        hour = parts.getOrNull(0)?.toIntOrNull()?.let { if (it == 0) 12 else it } ?: 7
        minute = parts.getOrNull(1)?.toIntOrNull() ?: 30
        isAm = period.equals("AM", ignoreCase = true)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(BACKGROUND)
        designScale = width / DESIGN_WIDTH
        canvas.withScale(designScale, designScale) {
            drawHeader(this)
            drawClock(this)
            drawPeriodSelector(this)
            drawRepeatCard(this)
            drawSliderCard(this, 457f, "Simulaci\u00F3n de amanecer", "0m", "60m", sunriseProgress)
            drawSliderCard(this, 603f, "Volumen maximo", "0%", "100%", volumeProgress)
            drawSaveButton(this)
        }
    }

    private fun drawHeader(canvas: Canvas) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawCircle(41f, 71f, 21f, paint)
        backIcon?.setBounds(31, 62, 52, 81)
        backIcon?.draw(canvas)
        drawCenteredText(canvas, "Editar Alarma", 195f, 80f, 24f, PRIMARY, bold)
    }

    private fun drawClock(canvas: Canvas) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(20f, 112f, 175f, 262f, 30f, 30f, paint)
        canvas.drawRoundRect(215f, 112f, 370f, 262f, 30f, 30f, paint)

        drawChevron(canvas, 88f, 122f, pointsUp = true)
        drawChevron(canvas, 282f, 122f, pointsUp = true)
        drawChevron(canvas, 88f, 241f, pointsUp = false)
        drawChevron(canvas, 282f, 241f, pointsUp = false)

        drawCenteredText(canvas, hour.toString().padStart(2, '0'), 97.5f, 225f, 96f, CLOCK, regular)
        drawCenteredText(canvas, ":", 195f, 222f, 96f, CLOCK, regular)
        drawCenteredText(canvas, minute.toString().padStart(2, '0'), 292.5f, 225f, 96f, CLOCK, regular)
    }

    private fun drawChevron(canvas: Canvas, left: Float, top: Float, pointsUp: Boolean) {
        chevron?.setBounds(left.toInt(), top.toInt(), (left + 20f).toInt(), (top + 12f).toInt())
        if (pointsUp) {
            canvas.withRotation(180f, left + 10f, top + 6f) { chevron?.draw(this) }
        } else {
            chevron?.draw(canvas)
        }
    }

    private fun drawPeriodSelector(canvas: Canvas) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(123f, 277f, 263f, 317f, 30f, 30f, paint)
        paint.color = PERIOD_SELECTED
        if (isAm) {
            canvas.drawRoundRect(128f, 282f, 193f, 312f, 30f, 30f, paint)
        } else {
            canvas.drawRoundRect(193f, 282f, 258f, 312f, 30f, 30f, paint)
        }
        drawCenteredText(canvas, "AM", 160f, 304f, 16f, if (isAm) TEXT_PRIMARY else SECONDARY, bold)
        drawCenteredText(canvas, "PM", 226f, 304f, 16f, if (isAm) SECONDARY else TEXT_PRIMARY, bold)
    }

    private fun drawRepeatCard(canvas: Canvas) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(20f, 332f, 370f, 437f, 30f, 30f, paint)
        drawText(canvas, "Repetir", 40f, 370f, 16f, TEXT_PRIMARY, semiBold)

        dayLabels.forEachIndexed { index, label ->
            paint.color = if (selectedDays[index]) DAY_SELECTED else BACKGROUND
            paint.style = Paint.Style.FILL
            canvas.drawCircle(dayLefts[index] + 17.5f, 399.5f, 17.5f, paint)
            drawCenteredText(canvas, label, dayLefts[index] + 17.5f, 405.5f, 16f, DAY_TEXT, semiBold)
        }
    }

    private fun drawSliderCard(
        canvas: Canvas,
        top: Float,
        title: String,
        startLabel: String,
        endLabel: String,
        progress: Float,
    ) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(20f, top, 370f, top + 126f, 30f, 30f, paint)
        drawText(canvas, title, 40f, top + 40f, 16f, TEXT_PRIMARY, semiBold)

        val handleCenter = 48f + (294f * progress)
        paint.color = TEXT_PRIMARY
        paint.style = Paint.Style.FILL
        if (progress > 0f) {
            canvas.drawRoundRect(40f, top + 64f, handleCenter - 6f, top + 80f, 2f, 2f, paint)
        }
        canvas.drawRoundRect(handleCenter + 8f, top + 64f, 350f, top + 80f, 2f, 2f, paint)

        paint.color = PRIMARY
        canvas.drawRoundRect(handleCenter - 2f, top + 50f, handleCenter + 2f, top + 94f, 2f, 2f, paint)

        paint.color = PERIOD_SELECTED
        canvas.drawCircle(345f, top + 72f, 2f, paint)
        drawText(canvas, startLabel, 40f, top + 106f, 10f, SECONDARY, semiBold)
        drawRightAlignedText(canvas, endLabel, 350f, top + 106f, 10f, SECONDARY, semiBold)
    }

    private fun drawSaveButton(canvas: Canvas) {
        paint.color = PRIMARY
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(20f, 764f, 370f, 824f, 30f, 30f, paint)
        drawCenteredText(canvas, "Guardar cambios", 195f, 803f, 20f, PERIOD_SELECTED, semiBold)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x / designScale
        val y = event.y / designScale

        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            if (y in 507f..551f) {
                sunriseProgress = sliderProgress(x)
                invalidate()
                return true
            }
            if (y in 653f..697f) {
                volumeProgress = sliderProgress(x)
                invalidate()
                return true
            }
        }

        if (event.action != MotionEvent.ACTION_UP) return true
        when {
            x in 17f..65f && y in 47f..95f -> {
                performClick()
                onBackClick?.invoke()
            }
            x in 20f..175f && y in 112f..145f -> changeHour(1)
            x in 20f..175f && y in 232f..262f -> changeHour(-1)
            x in 215f..370f && y in 112f..145f -> changeMinute(5)
            x in 215f..370f && y in 232f..262f -> changeMinute(-5)
            x in 123f..193f && y in 277f..317f -> {
                isAm = true
                invalidate()
            }
            x in 193f..263f && y in 277f..317f -> {
                isAm = false
                invalidate()
            }
            y in 382f..417f -> {
                dayLefts.forEachIndexed { index, left ->
                    if (x in left..(left + 35f)) {
                        selectedDays[index] = !selectedDays[index]
                        invalidate()
                        return true
                    }
                }
            }
            x in 20f..370f && y in 764f..824f -> {
                performClick()
                onSaveClick?.invoke(
                    "$hour:${minute.toString().padStart(2, '0')}",
                    if (isAm) "AM" else "PM",
                )
            }
        }
        return true
    }

    private fun changeHour(delta: Int) {
        hour = ((hour - 1 + delta + 12) % 12) + 1
        invalidate()
    }

    private fun changeMinute(delta: Int) {
        minute = (minute + delta + 60) % 60
        invalidate()
    }

    private fun sliderProgress(x: Float): Float = ((x - 48f) / 294f).coerceIn(0f, 1f)

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

    private fun drawRightAlignedText(
        canvas: Canvas,
        value: String,
        right: Float,
        baseline: Float,
        size: Float,
        color: Int,
        typeface: Typeface,
    ) {
        setTextPaint(size, typeface, color)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(value, right, baseline, paint)
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
        private val CLOCK = Color.rgb(188, 194, 255)
        private val TEXT_PRIMARY = Color.rgb(244, 222, 222)
        private val SECONDARY = Color.rgb(198, 197, 212)
        private val DAY_TEXT = Color.rgb(186, 173, 58)
        private val DAY_SELECTED = Color.rgb(86, 78, 9)
        private val PERIOD_SELECTED = Color.rgb(138, 33, 0)
    }
}

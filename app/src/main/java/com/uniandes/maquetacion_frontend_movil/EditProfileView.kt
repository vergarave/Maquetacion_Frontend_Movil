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

/** Pixel-accurate rendering of the 390 x 844 "Editar Perfil" Figma frame. */
class EditProfileView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var onBackClick: (() -> Unit)? = null
    var onSaveClick: (() -> Unit)? = null

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
            drawProfile(this)
            drawField(this, "Nombre Completo", "Nombre Usuario", 349f)
            drawField(this, "Correo", "usuario@example.com", 449f)
            drawField(this, "Saludo Personalizado", "usuario", 549f)
            drawSaveButton(this)
        }
    }

    private fun drawHeader(canvas: Canvas) {
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawCircle(41f, 71f, 21f, paint)
        backIcon?.setBounds(31, 62, 52, 81)
        backIcon?.draw(canvas)
        drawCenteredText(canvas, "Editar Perfil", 195f, 80f, 24f, PRIMARY, bold)
    }

    private fun drawProfile(canvas: Canvas) {
        avatar?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, RectF(136f, 135f, 254f, 253f), paint)
        }
    }

    private fun drawField(canvas: Canvas, label: String, value: String, top: Float) {
        drawText(canvas, label, 21f, top + 15f, 12f, TEXT_PRIMARY, medium)
        paint.color = CARD
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(20f, top + 25f, 370f, top + 90f, 30f, 30f, paint)
        drawText(canvas, value, 40f, top + 64f, 16f, SECONDARY, medium)
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
            x in 20f..370f && y in 764f..824f -> {
                performClick()
                onSaveClick?.invoke()
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
        private val PERIOD_SELECTED = Color.rgb(138, 33, 0)
    }
}

package com.example.myapplication

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.graphics.PathParser

/** Векторный символ знака зодиака для ImageView. */
class ZodiacDrawable(sign: Zodiac) : Drawable() {
    private val path = requireNotNull(PathParser.createPathFromPathData(sign.drawing))
    private val background = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(229, 244, 239) }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(26, 105, 82)
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun draw(canvas: Canvas) {
        val size = minOf(bounds.width(), bounds.height()).toFloat()
        canvas.save()
        canvas.translate(bounds.left + (bounds.width() - size) / 2, bounds.top + (bounds.height() - size) / 2)
        canvas.scale(size / 120, size / 120)
        canvas.drawCircle(60f, 60f, 60f, background)
        canvas.translate(10f, 10f)
        canvas.drawPath(path, stroke)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        background.alpha = alpha
        stroke.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        stroke.colorFilter = colorFilter
        invalidateSelf()
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

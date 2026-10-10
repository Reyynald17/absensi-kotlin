package com.example.absensisiswaproto.ui.absen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ArcProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 14f
        color = Color.parseColor("#E0E0E0")
    }

    private val paintFg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 14f
        color = Color.parseColor("#32D800")
    }

    private val rect = RectF()
    private var progress = 0f

    fun setProgress(p: Float) {
        progress = p.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pad = paintBg.strokeWidth / 2f
        val size = minOf(width, height).toFloat() - paintBg.strokeWidth
        val cx = width / 2f
        val cy = height / 2f
        rect.set(cx - size / 2f, cy - size / 2f, cx + size / 2f, cy + size / 2f)

        canvas.drawArc(rect, -90f, 360f, false, paintBg)
        canvas.drawArc(rect, -90f, 360f * progress, false, paintFg)
    }
}

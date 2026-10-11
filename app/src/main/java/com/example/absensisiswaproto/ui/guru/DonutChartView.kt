package com.example.absensisiswaproto.ui.guru

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class DonutChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintHadir = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
        color = Color.parseColor("#1E8827")
    }
    private val paintTelat = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
        color = Color.parseColor("#FFC107")
    }
    private val paintAlpha = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
        color = Color.parseColor("#E53935")
    }
    private val paintEmpty = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
        color = Color.parseColor("#E0E0E0")
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E8827")
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private var hadir = 0
    private var telat = 0
    private var alpha = 0
    private var percentage = 0.0

    private val rect = RectF()

    fun setData(hadir: Int, telat: Int, alpha: Int) {
        this.hadir = hadir
        this.telat = telat
        this.alpha = alpha
        val total = hadir + telat + alpha
        percentage = if (total > 0) hadir.toDouble() / total * 100 else 0.0
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val strokeW = dpToPx(28f)
        val size = minOf(width, height).toFloat() - strokeW
        val cx = width / 2f
        val cy = height / 2f
        rect.set(cx - size / 2f, cy - size / 2f, cx + size / 2f, cy + size / 2f)

        paintHadir.strokeWidth = strokeW
        paintTelat.strokeWidth = strokeW
        paintAlpha.strokeWidth = strokeW
        paintEmpty.strokeWidth = strokeW

        paintText.textSize = dpToPx(40f)

        val total = hadir + telat + alpha
        if (total == 0) {
            canvas.drawArc(rect, -90f, 360f, false, paintEmpty)
            paintText.color = Color.parseColor("#888888")
            canvas.drawText("0%", cx, cy + paintText.textSize / 3, paintText)
            return
        }

        var startAngle = -90f
        val hadirSweep = 360f * hadir / total
        val telatSweep = 360f * telat / total
        val alphaSweep = 360f * alpha / total

        canvas.drawArc(rect, startAngle, hadirSweep, false, paintHadir)
        startAngle += hadirSweep
        canvas.drawArc(rect, startAngle, telatSweep, false, paintTelat)
        startAngle += telatSweep
        canvas.drawArc(rect, startAngle, alphaSweep, false, paintAlpha)

        paintText.color = Color.parseColor("#1E8827")
        val percentText = "${percentage.toInt()}%"
        canvas.drawText(percentText, cx, cy + paintText.textSize / 3, paintText)
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
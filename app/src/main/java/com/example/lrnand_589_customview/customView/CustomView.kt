package com.example.lrnand_589_customview.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.ColorInt

class CustomView(context: Context, private var attrSet: AttributeSet) :
    FrameLayout(context, attrSet) {

    @ColorInt
    private val defaultColor: Int = Color.GREEN // если не указали в атрибутах свой цвет то рисуем этим
    private val maxFigures: Int = 10
    private val figures = mutableListOf<Figure>()
    var colorSet = mutableListOf<Int>()
    var limitExceededCallback: (() -> Unit)? = null

    init {
        setWillNotDraw(false)
        isSaveEnabled = true;
    }

    private fun addFigure(x: Float, y: Float) {
        val paint = Paint()
        paint.color = if (colorSet.isNotEmpty()) colorSet.random() else defaultColor
        paint.flags = Paint.ANTI_ALIAS_FLAG
        figures.add(Figure.withRandomBoundsAndType(x, y, paint))
        invalidate()
    }


    private fun drawAll(canvas: Canvas) {

        figures.forEach {
            it.drawSelf(canvas)
        }

        if (figures.size > maxFigures) {
            figures.clear()
            canvas.drawColor(Color.WHITE)
            limitExceededCallback?.invoke()
        }

        // вывести количество фигур
        val text = figures.size.toString()
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 72f

        canvas.drawText(text, 25f, 100f, paint)


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Float = event.x
        val y: Float = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                addFigure(x, y)
            }
        }
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawAll(canvas)
        }
    }

}
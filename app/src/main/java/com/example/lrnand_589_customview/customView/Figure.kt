package com.example.lrnand_589_customview.customView

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

class Figure(
    private val x: Float,
    private val y: Float,
    private val size: Float,
    private val type: Type,
    private val paint: Paint
) {
    fun drawSelf(canvas: Canvas) {
        when (type) {
            Type.SQUARE ->
                canvas.drawRect(getSquareF(), paint)

            Type.ROUNDED_SQUARE -> {
                drawCross(canvas)
                drawFourCircles(canvas)
            }

            Type.ROUND -> {
                canvas.drawCircle(x, y, size / 2, paint)
            }
        }
    }

    private fun getHalfSize(): Float {
        return size / 2
    }

    private fun getLeft(): Float = x - getHalfSize()

    private fun getTop(): Float = y + getHalfSize()

    private fun getRight(): Float = x + getHalfSize()

    private fun getBottom(): Float = y - getHalfSize()

    private fun getSquareF(): RectF {
        return RectF(getLeft(), getTop(), getRight(), getBottom())
    }

    private fun getSquare(): Rect {
        return Rect(getLeft().toInt(), getTop().toInt(), getRight().toInt(), getBottom().toInt())
    }

    private fun drawCross(canvas: Canvas) {
        val sizePart = size / 4

        val verticalStrip = getSquareF()
        verticalStrip.left = verticalStrip.left + sizePart
        verticalStrip.right = verticalStrip.right - sizePart

        val horizontalStrip = getSquareF()
        horizontalStrip.top = horizontalStrip.top - sizePart
        horizontalStrip.bottom = horizontalStrip.bottom + sizePart

        canvas.drawRect(verticalStrip, paint)
        canvas.drawRect(horizontalStrip, paint)
    }

    private fun drawFourCircles(canvas: Canvas) {
        val sizePart = size / 4

        val cx1 = x - sizePart
        val cx2 = x - sizePart
        val cx3 = x + sizePart
        val cx4 = x + sizePart

        val cy1 = y - sizePart
        val cy2 = y + sizePart
        val cy3 = y - sizePart
        val cy4 = y + sizePart

        canvas.drawCircle(cx1, cy1, sizePart, paint)
        canvas.drawCircle(cx2, cy2, sizePart, paint)
        canvas.drawCircle(cx3, cy3, sizePart, paint)
        canvas.drawCircle(cx4, cy4, sizePart, paint)
    }

    enum class Type {
        ROUND,
        SQUARE,
        ROUNDED_SQUARE
    }

    companion object {
        fun withRandomBoundsAndType(x: Float, y: Float, paint: Paint): Figure {
            val randomSize1 = (20..50).random().toFloat()
            return Figure(x, y, randomSize1, Type.values().random(), paint)
        }
    }
}
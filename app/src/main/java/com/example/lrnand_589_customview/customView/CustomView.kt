package com.example.lrnand_589_customview.customView

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import android.os.Parcel
import android.view.View
import com.google.gson.Gson
import androidx.annotation.AttrRes
import com.example.lrnand_589_customview.R


class CustomView(context: Context, private var attrSet: AttributeSet) :
    FrameLayout(context, attrSet) {

    var colorSet = mutableListOf<Int>()
    var limitExceededCallback: (() -> Unit)? = null
    @ColorInt
    private val defaultColor: Int = Color.GREEN // если не указали в атрибутах свой цвет то рисуем этим
    private val maxFigures: Int = 10
    private var figures = mutableListOf<Figure>()

    init {
        setWillNotDraw(false)
        obtainAttributes()
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

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        if (superState != null) {
            val ss = SavedState(superState)
            val gson = Gson()
            ss.figuresAndColors = gson.toJson(FiguresAndColors(figures, colorSet))
            return ss
        }

        return superState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        val json = state.figuresAndColors
        val gson = Gson()
        val figuresAndColors = gson.fromJson(json, FiguresAndColors::class.java)

        this.colorSet = figuresAndColors.colors
        this.figures = figuresAndColors.figures

        super.onRestoreInstanceState(state.superState)
    }

    private fun obtainAttributes() {
        val typedArray = context.obtainStyledAttributes(
            attrSet,
            R.styleable.CustomView,
            0, 0
        )

        val colors = typedArray.getTextArray(R.styleable.CustomView_colorSetReference)

        colors?.let {
            it.forEach { color ->
                val colorStr = color.toString()
                colorSet.add(Color.parseColor(colorStr))
            }
        }

        typedArray.recycle()
    }

    private fun addFigure(x: Float, y: Float) {
        val paint = Paint()

        paint.color = if (colorSet.isNotEmpty()) colorSet.random() else defaultColor
        paint.flags = Paint.ANTI_ALIAS_FLAG

        figures.add(Figure.withRandomBoundsAndType(x, y, paint))

        invalidate()
    }

    private fun drawFiguresCount(canvas: Canvas) {
        val text = figures.size.toString()
        val paint = Paint()

        paint.color = Color.BLACK
        paint.textSize = 72f

        canvas.drawText(text, 25f, 100f, paint)
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

        drawFiguresCount(canvas)
    }

    data class FiguresAndColors(val figures: MutableList<Figure>, val colors: MutableList<Int>)

    internal class SavedState : View.BaseSavedState {
        lateinit var figuresAndColors: String

        constructor(superState: Parcelable) : super(superState) {}

        private constructor(parcel: Parcel) : super(parcel) {
            val s = parcel.readString()
            s?.let {
                this.figuresAndColors = s
            }
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(this.figuresAndColors)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
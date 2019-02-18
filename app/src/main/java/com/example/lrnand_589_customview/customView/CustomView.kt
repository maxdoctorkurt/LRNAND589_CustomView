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

    @ColorInt
    private val defaultColor: Int = Color.GREEN // если не указали в атрибутах свой цвет то рисуем этим
    private val maxFigures: Int = 10
    private var figures = mutableListOf<Figure>()
    var colorSet = mutableListOf<Int>()
    var limitExceededCallback: (() -> Unit)? = null
    init {
        setWillNotDraw(false)
        isSaveEnabled = true;

        // TODO - посмотреть что там со ссылками на ресурсы
        context.theme.obtainStyledAttributes(
            attrSet,
            R.styleable.CustomView,
            0, 0
        ).apply {

            try {
                val colors = getTextArray(R.styleable.CustomView_colorSetReference)
                colors?.let {
                    val s = it.forEach {
                        val s = it.toString()
                        colorSet.add(Color.parseColor(s))
                    }
                }
            } finally {
                recycle()
            }
        }
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

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        if (superState != null) {
            val ss = SavedState(superState)
            val gson = Gson()
            ss.figuresAndColors = gson.toJson(FiguresAndColors(figures, colorSet))
            return ss
        }
        return superState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
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
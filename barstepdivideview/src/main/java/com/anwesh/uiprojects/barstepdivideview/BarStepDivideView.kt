package com.anwesh.uiprojects.barstepdivideview

/**
 * Created by anweshmishra on 18/01/20.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val nodes : Int = 5
val bars : Int = 3
val scGap : Float = 0.02f / bars
val delay : Long = 20
val sizeFactor : Float = 2f
val foreColor : Int = Color.parseColor("#BDBDBD")
val backColor : Int = Color.parseColor("#BDBDBD")
val strokeFactor : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawStepDivideBar(i : Int, w : Float, scale : Float, hGap : Float, paint : Paint) {
    val gap : Float = w / (bars)
    val barSize : Float = gap / sizeFactor
    val sc : Float = scale.sinify().divideScale(i, bars)
    save()
    translate(gap * i + barSize / 2, 0f)
    drawRect(RectF(0f, -hGap * sc, barSize, 0f), paint)
    restore()
}

fun Canvas.drawStepDividerBars(w : Float, scale : Float, hGap : Float, paint : Paint) {
    for (j in 0..(bars - 1)) {
        drawStepDivideBar(j, w, scale, hGap, paint)
    }
}

fun Canvas.drawBSDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(0f, gap * i + gap / 2)
    drawLine(0f, 0f, gap , 0f, paint)
    drawStepDividerBars(w, scale, gap, paint)
    restore()
}


class BarStepDivideView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

}
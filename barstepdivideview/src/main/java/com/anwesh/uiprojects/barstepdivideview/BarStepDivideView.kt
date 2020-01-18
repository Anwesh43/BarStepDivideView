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
import android.util.Log

val nodes : Int = 5
val bars : Int = 3
val scGap : Float = 0.02f / bars
val delay : Long = 10
val sizeFactor : Float = 2f
val foreColor : Int = Color.parseColor("#311B92")
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
    if (scale > 0) {
        Log.d("coords", "${i}:${sc}")
    }
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
    translate(0f, gap * (i + 1))
    Log.d("y", "${gap * i + gap / 2}")
    drawLine(0f, 0f, w , 0f, paint)
    drawStepDividerBars(w, scale, gap, paint)
    restore()
}


class BarStepDivideView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BSDNode(var i : Int, val state : State = State()) {

        private var next : BSDNode? = null
        private var prev : BSDNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BSDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBSDNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BSDNode {
            var curr : BSDNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }


    data class StepDivideBar(var i : Int) {

        private val root : BSDNode = BSDNode(0)
        private var curr : BSDNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BarStepDivideView) {

        private val animator : Animator = Animator(view)
        private val sdb : StepDivideBar = StepDivideBar(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            sdb.draw(canvas, paint)
            animator.animate {
                sdb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sdb.startUpdating {
                Log.d("started animating", "animator")
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BarStepDivideView {
            val view : BarStepDivideView = BarStepDivideView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
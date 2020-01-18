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

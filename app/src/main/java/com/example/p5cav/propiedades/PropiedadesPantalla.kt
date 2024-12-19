package com.example.p5cav.propiedades

import android.content.Context
import android.view.Display
import android.view.WindowManager

const val IZQUIERDA = 0
const val ARRIBA = 1
const val DERECHA = 2
const val ABAJO = 3

class PropiedadesPantalla(context : Context){


    var anchura = -1
    var altura = -1


    init {
        obtenerParametrosPantalla(context)
    }
    fun obtenerParametrosPantalla(context: Context) {

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        anchura = display.width
        altura = display.height


    }




}
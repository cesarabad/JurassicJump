package com.example.p5cav.entidades

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.p5cav.propiedades.ABAJO
import com.example.p5cav.propiedades.ARRIBA
import com.example.p5cav.propiedades.DERECHA
import com.example.p5cav.propiedades.IZQUIERDA
import com.example.p5cav.vistas.propiedadesPantalla

const val VIVO = true
//const val MUERTO = false
const val MAXIMO_SALTOS = 2
const val VERTICAL_BIAS_DINOSAURIO_DEFAULT = 0.5f
const val ALTURA_SALTO_DINOSAURIO = 0.23f
const val DURACION_SALTO = 500L
const val DURACION_CAIDA = 400L
const val DURACION_FRONTFLIP = 600L

class Dinosaurio (var dinosaurioGif : ImageView) {



    private var horizontalBiasDinosaurio = 0.15f
    private var verticalBiasDinosaurio = VERTICAL_BIAS_DINOSAURIO_DEFAULT
    var posicionesDinosaurio = IntArray(4)
    var estaVivo = VIVO
    private var animacionSalto : ValueAnimator? = null
    private var contadorSaltosRealizados = 0

    fun actualizarPosicionDinosaurio() {

        // width/3 para reducir el hitbox, y que no sea el tamaño de la imagen
        posicionesDinosaurio[IZQUIERDA] = (horizontalBiasDinosaurio*propiedadesPantalla.anchura - dinosaurioGif.layoutParams.width/3.5).toInt()
        posicionesDinosaurio[ABAJO] = (verticalBiasDinosaurio*propiedadesPantalla.altura + dinosaurioGif.layoutParams.height/3.5).toInt()
        posicionesDinosaurio[DERECHA] = (horizontalBiasDinosaurio*propiedadesPantalla.anchura + dinosaurioGif.layoutParams.width/3).toInt()
        posicionesDinosaurio[ARRIBA] = (verticalBiasDinosaurio*propiedadesPantalla.altura - dinosaurioGif.layoutParams.height/3).toInt()
    }

    fun saltar(mainLayout : ConstraintLayout) {

        val haceFrontFlip = contadorSaltosRealizados in 1..<MAXIMO_SALTOS
        val puedeSaltar = contadorSaltosRealizados < MAXIMO_SALTOS

        if (haceFrontFlip) {
            if (animacionSalto != null) animacionSalto!!.cancel()

            val frontFlip = ObjectAnimator.ofFloat(dinosaurioGif, "rotation", 0f, -360f)
            frontFlip.duration = DURACION_FRONTFLIP
            frontFlip.start()
        }

        if (puedeSaltar) {

            val alturaFinalSalto = verticalBiasDinosaurio - if (haceFrontFlip) ALTURA_SALTO_DINOSAURIO/1.3f else ALTURA_SALTO_DINOSAURIO
            animacionSalto = ValueAnimator.ofFloat(verticalBiasDinosaurio, alturaFinalSalto)
            animacionSalto!!.duration = DURACION_SALTO
            animacionSalto!!.addUpdateListener {
                    animacionEjecutando ->
                actualizarPosicionDuranteAnimacion(animacionEjecutando,  mainLayout)

                // Final de la animacion (me falla con doOnEnd)
                if (animacionEjecutando.animatedValue as Float == alturaFinalSalto) {
                    animacionSalto = null
                    caer(mainLayout)
                }
            }
            animacionSalto!!.start()
            contadorSaltosRealizados++

        }
    }

    private fun caer(mainLayout : ConstraintLayout){

        val animacion = ValueAnimator.ofFloat(verticalBiasDinosaurio, VERTICAL_BIAS_DINOSAURIO_DEFAULT)
        animacion.duration = DURACION_CAIDA
        animacion.addUpdateListener {
                animacionEjecutando ->

            actualizarPosicionDuranteAnimacion(animacionEjecutando,  mainLayout)

            // Si mientras esta cayendo se hace el segundo salto
            if (animacionSalto != null) {
                animacionEjecutando.cancel()
            }

            // Final de la animacion (me falla con doOnEnd)
            if (animacionEjecutando.animatedValue as Float == VERTICAL_BIAS_DINOSAURIO_DEFAULT) {
                contadorSaltosRealizados = 0
            }
        }
       animacion.start()



    }

    private fun actualizarPosicionDuranteAnimacion(animacion : ValueAnimator, mainLayout : ConstraintLayout) {

        if (animacion.isRunning) {
            verticalBiasDinosaurio = animacion.animatedValue as Float

            val constraintSet = ConstraintSet()
            constraintSet.clone(mainLayout)
            constraintSet.setVerticalBias(dinosaurioGif.id, verticalBiasDinosaurio)
            constraintSet.applyTo(mainLayout)
            actualizarPosicionDinosaurio()

            if (!estaVivo) {
                animacion.cancel()
            }
        }

    }


}


package com.example.p5cav.entidades

import android.animation.ValueAnimator
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.p5cav.propiedades.ABAJO
import com.example.p5cav.propiedades.ARRIBA
import com.example.p5cav.propiedades.DERECHA
import com.example.p5cav.propiedades.IZQUIERDA
import com.example.p5cav.vistas.GamePlay
import com.example.p5cav.R
import com.example.p5cav.vistas.fondoPartida
import com.example.p5cav.vistas.navegacion.Resultado
import com.example.p5cav.vistas.navegacion.usuarioLogeado
import com.example.p5cav.vistas.obstaculos
import com.example.p5cav.vistas.pajaros
import com.example.p5cav.vistas.propiedadesPantalla
import com.example.p5cav.vistas.puntuacionPartida
import kotlin.random.Random

class Obstaculo (private var obstaculo: ImageView) {

    private var horizontalBiasObstaculo = 1.0f
    private var verticalBiasObstaculo = obtenerVerticalBias()
    private var posicionesObstaculo = IntArray(4)



    private fun actualizarPosicionObstaculo() {

        posicionesObstaculo[IZQUIERDA] = (horizontalBiasObstaculo* fondoPartida.width - obstaculo.layoutParams.width/2 - (fondoPartida.width-propiedadesPantalla.anchura)/2).toInt()
        posicionesObstaculo[ABAJO] = (verticalBiasObstaculo*propiedadesPantalla.altura + obstaculo.layoutParams.height/1.5).toInt()
        posicionesObstaculo[DERECHA] = (horizontalBiasObstaculo* fondoPartida.width + obstaculo.layoutParams.width/3 - (fondoPartida.width-propiedadesPantalla.anchura)/2).toInt()
        posicionesObstaculo[ARRIBA] = (verticalBiasObstaculo*propiedadesPantalla.altura - obstaculo.layoutParams.height/3).toInt()
    }


     fun lanzarObstaculo( dinosaurio: Dinosaurio, gamePlay: GamePlay, puntuacionOutput: TextView) {

        val vaConPajaros = comprobarPajaros(gamePlay.mainLayout, dinosaurio)

        val animacion = ValueAnimator.ofFloat(1.0f, 0.0f)

        animacion.duration = (3000f/ (1 + puntuacionPartida.toFloat()/100000)).toLong()
        animacion.addUpdateListener {
                animacionEjecutandose ->
            cambiarPosicionObstaculoDuranteAnimacion(gamePlay.mainLayout, animacionEjecutandose.animatedValue as Float)
            puntuacionPartida += 10
            puntuacionOutput.text = puntuacionPartida.toString()

            // Comprobar si el dinosaurio esta tocando el obstaculo o pajaros
             dinosaurio.estaVivo = comprobarDinosaurioASalvo(dinosaurio) && if(vaConPajaros) pajaros.comprobarDinosaurioASalvo(dinosaurio) else true

            if (!dinosaurio.estaVivo) {
                terminarPartida(animacionEjecutandose,gamePlay,dinosaurio)
            }

        }


        animacion.start()

        animacion.doOnEnd {
            if (dinosaurio.estaVivo) {
                obtenerObstaculoAleatorio(obstaculos).lanzarObstaculo(dinosaurio,gamePlay,puntuacionOutput)
            }
        }

    }

    private fun terminarPartida(animacionEjecutandose: ValueAnimator, gamePlay: GamePlay, dinosaurio: Dinosaurio) {
        animacionEjecutandose.cancel()

        Glide.with(gamePlay).clear(dinosaurio.dinosaurioGif)
        dinosaurio.dinosaurioGif.setImageDrawable(ContextCompat.getDrawable(gamePlay,R.drawable.dinosaurio))

        // FIN DE PARTIDA, MOSTRAR RESULTADOS

        val pantallaResultado = Intent(gamePlay, Resultado::class.java)

        pantallaResultado.putExtra("resultado", puntuacionPartida)

        gamePlay.startActivity(pantallaResultado)
        puntuacionPartida = 0
        gamePlay.finish()
    }

    fun cambiarPosicionObstaculoDuranteAnimacion(gamePlayLayout : ConstraintLayout, horizontalBias : Float) {
        horizontalBiasObstaculo = horizontalBias
        val constraintSet = ConstraintSet()
        constraintSet.clone(gamePlayLayout)
        constraintSet.setHorizontalBias(obstaculo.id, horizontalBias)
        constraintSet.applyTo(gamePlayLayout)
        actualizarPosicionObstaculo()
    }

    private fun comprobarPajaros(gamePlayLayout: ConstraintLayout, dinosaurio : Dinosaurio) : Boolean{

        // Impedir doble salto cuando no es necesario
        val conPajaros = obstaculo.layoutParams.height.toFloat()/propiedadesPantalla.altura.toFloat() <= 0.18f // Obstaculo bajo
                    && obstaculo.layoutParams.width.toFloat()/propiedadesPantalla.anchura.toFloat() <= 0.25f // Obstaculo estrecho

        if (conPajaros) {
            pajaros.lanzarPajaros(gamePlayLayout, dinosaurio)
        } else {
            pajaros.cambiarPosicionObstaculoDuranteAnimacion(gamePlayLayout, 1.3f)
        }
        return conPajaros
    }

    private fun lanzarPajaros(gamePlayLayout: ConstraintLayout, dinosaurio: Dinosaurio) {

        obstaculo.visibility = ImageView.VISIBLE
        val animacion = ValueAnimator.ofFloat(1.3f, 0f)
        animacion.duration = ((3000f/ (1 + puntuacionPartida.toFloat()/100000))).toLong()
        animacion.addUpdateListener {
            animacionEjecutandose ->
            cambiarPosicionObstaculoDuranteAnimacion(gamePlayLayout,animacionEjecutandose.animatedValue as Float)

            if (!dinosaurio.estaVivo) {
                animacionEjecutandose.cancel()
            }
        }


        animacion.start()

        animacion.doOnEnd { obstaculo.visibility = ImageView.GONE }
    }

    private fun comprobarDinosaurioASalvo(dinosaurio: Dinosaurio) : Boolean {

        val noTocaX = (dinosaurio.posicionesDinosaurio[DERECHA] < posicionesObstaculo[IZQUIERDA] // Se encuentra a la izquierda del obstaculo
                || dinosaurio.posicionesDinosaurio[IZQUIERDA] > posicionesObstaculo[DERECHA]) // Se encuentra a la derecha del objeto


        return noTocaX
                || dinosaurio.posicionesDinosaurio[ABAJO] < posicionesObstaculo [ARRIBA] // Esta arriba
                || (dinosaurio.posicionesDinosaurio[ARRIBA] > posicionesObstaculo [ABAJO] && dinosaurio.posicionesDinosaurio[ABAJO] > posicionesObstaculo [ARRIBA]) // Pasa por debajo
    }

    private fun obtenerVerticalBias() : Float{

        val idObstaculo = obstaculo.id

        return when (idObstaculo) {
            R.id.rocaPng -> {
                0.52f

            }
            R.id.cactusPng -> {
                0.48f

            }
            R.id.pajarosPng -> {
                0.1f

            }
            else -> 0.5f
        }



    }

    companion object {
        fun obtenerObstaculoAleatorio(obstaculos: MutableList<ImageView>) : Obstaculo {

            val obstaculo = Obstaculo(obstaculos[Random.nextInt(0,obstaculos.size)])
            return obstaculo
        }
    }


}
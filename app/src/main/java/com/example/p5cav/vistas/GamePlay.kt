package com.example.p5cav.vistas

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.p5cav.propiedades.PropiedadesPantalla
import com.example.p5cav.R
import com.example.p5cav.entidades.Dinosaurio
import com.example.p5cav.entidades.Obstaculo
import com.example.p5cav.entidades.VIVO
import kotlin.random.Random

var puntuacionPartida = 0L
lateinit var obstaculos : MutableList<ImageView>
lateinit var propiedadesPantalla : PropiedadesPantalla

@SuppressLint("StaticFieldLeak")
lateinit var pajaros : Obstaculo
@SuppressLint("StaticFieldLeak")
lateinit var fondoPartida : ImageView

class GamePlay : AppCompatActivity() {


    lateinit var mainLayout : ConstraintLayout
    private lateinit var dinosaurio : Dinosaurio
    private lateinit var saltarBtn : Button
    private lateinit var puntuacionPartidaOutput : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gameplay)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        obstaculos = mutableListOf()
        anadirObstaculos(obstaculos)

        propiedadesPantalla = PropiedadesPantalla(this)
        mainLayout = findViewById(R.id.main)
        dinosaurio =  Dinosaurio(findViewById(R.id.dinosaurioGif))
        fondoPartida = findViewById(R.id.fondoPartida)
        puntuacionPartidaOutput = findViewById(R.id.puntuacionOutput)
        saltarBtn = findViewById(R.id.saltarBtn)
        pajaros = Obstaculo(findViewById(R.id.pajarosPng))


        empezarPartida()
    }

    private fun animarBolaGiratoria (bolaPng : ImageView) {
        bolaPng.visibility = if (Random.nextInt(1,6) == 3) ImageView.VISIBLE else ImageView.INVISIBLE

        val vuelta = ObjectAnimator.ofFloat(bolaPng, "rotation", 0f, -1800f)
        val desplazamiento = ValueAnimator.ofFloat(1.0f , 0.0f)
        desplazamiento.addUpdateListener {
            animacionEjecutandose ->
            val constraintSet = ConstraintSet()
            constraintSet.clone(mainLayout)
            constraintSet.setHorizontalBias(bolaPng.id, animacionEjecutandose.animatedValue as Float)
            constraintSet.applyTo(mainLayout)
        }

        val animaciones = AnimatorSet()
        animaciones.duration = Random.nextLong(4500, 6000)
        animaciones.playTogether(vuelta, desplazamiento)
        animaciones.start()
        animaciones.doOnEnd { animarBolaGiratoria(bolaPng) }
    }


    private fun empezarPartida() {

        // Da movimiento a los gif
        dinosaurio.estaVivo = VIVO
        pajaros.cambiarPosicionObstaculoDuranteAnimacion(mainLayout, 1.3f)
        Glide.with(this).load(R.drawable.dinosaurio).into(findViewById(R.id.dinosaurioGif))
        Glide.with(this).load(R.drawable.background).into(findViewById(R.id.fondoPartida))

        dinosaurio.actualizarPosicionDinosaurio()

        animarBolaGiratoria(findViewById(R.id.bolaPng))

        saltarBtn.setOnClickListener {
            dinosaurio.saltar(mainLayout)

        }

        Obstaculo.obtenerObstaculoAleatorio(obstaculos).
            lanzarObstaculo(dinosaurio, this, puntuacionPartidaOutput)



    }


    private fun anadirObstaculos(obstaculos: MutableList<ImageView>) {

        obstaculos.add(findViewById(R.id.rocaPng))
        obstaculos.add(findViewById(R.id.cactusPng))
        obstaculos.add(findViewById(R.id.arbolPng))
        obstaculos.add(findViewById(R.id.esqueletoPng))
    }


}
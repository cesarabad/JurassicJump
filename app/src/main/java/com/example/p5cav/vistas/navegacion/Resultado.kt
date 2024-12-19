package com.example.p5cav.vistas.navegacion

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.p5cav.R
import com.example.p5cav.vistas.navegacion.Leaderboard.Companion.generarLeaderBoard

class Resultado : AppCompatActivity() {

    private lateinit var resultadoTxt : TextView
    private lateinit var userOutput : TextView
    private lateinit var bestScoreOutput : TextView
    private lateinit var leaderboard: LinearLayout
    private lateinit var botonVolver : Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resultado)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resultadoTxt = findViewById(R.id.resultadoOutput)
        leaderboard = findViewById(R.id.leaderBoardResultado)
        botonVolver = findViewById(R.id.volverMenuBtn)
        userOutput = findViewById(R.id.userOutput)
        bestScoreOutput = findViewById(R.id.bestOutput)

        val extras = intent.extras
        if (extras != null) {
            val resultado = extras.getLong("resultado")

            if (resultado > usuarioLogeado.bestScore) {
                usuarioLogeado.bestScore = resultado
            }

            resultadoTxt.text = resultado.toString() + " " + getString(R.string.puntos)
            userOutput.text = usuarioLogeado.username
            bestScoreOutput.text = usuarioLogeado.bestScore.toString() + " " + getString(R.string.tuMejor)
        }

        leaderboard.addView(generarLeaderBoard(this))
        botonVolver.setOnClickListener {
            finish()
        }
    }
}
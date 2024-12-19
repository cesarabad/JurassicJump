package com.example.p5cav.vistas.navegacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.p5cav.R
import com.example.p5cav.vistas.GamePlay


class MenuJuego : AppCompatActivity() {


    private lateinit var logoApp : ImageView
    private lateinit var cambiarUserBtn : Button
    private lateinit var leaderBoardBtn : Button
    private lateinit var userLogedOutput : TextView
    private lateinit var iniciarPartidaBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_juego)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        logoApp = findViewById(R.id.gameLogoGameplay)
        cambiarUserBtn = findViewById(R.id.cambiarUsuarioBtn)
        leaderBoardBtn = findViewById(R.id.leaderBoardBtn)
        userLogedOutput = findViewById(R.id.userLogeadoOutput)
        iniciarPartidaBtn = findViewById(R.id.empezarPartidaBtn)

        userLogedOutput.text = usuarioLogeado.username

        iniciarPartidaBtn.setOnClickListener {
            val gamePlay = Intent(this, GamePlay::class.java)
            startActivity(gamePlay)
        }

        cambiarUserBtn.setOnClickListener {
            finish()
        }

        leaderBoardBtn.setOnClickListener {
            val leaderboard = Intent(this, Leaderboard::class.java)
            startActivity(leaderboard)
        }
    }
}
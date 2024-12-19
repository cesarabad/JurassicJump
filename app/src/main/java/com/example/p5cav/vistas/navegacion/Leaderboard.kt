package com.example.p5cav.vistas.navegacion

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.core.widget.NestedScrollView
import com.example.p5cav.R




class Leaderboard : AppCompatActivity() {

    private lateinit var leaderboardOutput : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        leaderboardOutput = findViewById(R.id.leaderBoardOutput)
        leaderboardOutput.addView(generarLeaderBoard(this))
    }

    companion object {
        fun generarLeaderBoard(context: Context): LinearLayout {
            // Crear el contenedor principal (LinearLayout) que incluirá el encabezado fijo
            val containerLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Fondo con el color especificado
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(android.graphics.Color.parseColor("#36A930")) // Color con transparencia
                    setStroke(4, android.graphics.Color.BLACK) // Borde negro
                    cornerRadius = 8f // Bordes redondeados
                }
            }

            // Crear el encabezado con fondo oscuro y borde claro
            val filaHeader = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(android.graphics.Color.DKGRAY) // Fondo oscuro
                setPadding(8, 8, 8, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)  // Espacio debajo del encabezado
                }
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(android.graphics.Color.DKGRAY)
                    setStroke(4, android.graphics.Color.DKGRAY)  // Borde gris oscuro
                    cornerRadius = 3f  // Bordes redondeados
                }
            }

            // Función auxiliar para crear un TextView con propiedades comunes
            fun crearTextView(context: Context, texto: String, isHeader: Boolean = false, alignEnd: Boolean = false): TextView {
                return TextView(context).apply {
                    text = texto
                    textSize = 25f
                    setPadding(16, 16, 16, 16)

                    setTextColor(android.graphics.Color.WHITE)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        weight = 1f  // Esto empuja el TextView hacia la derecha

                    }
                    if (isHeader) {
                        setTypeface(null, android.graphics.Typeface.BOLD)  // Negrita para el encabezado
                    } else {
                        setTypeface(null, android.graphics.Typeface.BOLD_ITALIC)
                    }

                }


            }


            // Añadir "USUARIO" y "PUNTUACION" al encabezado
            filaHeader.apply {
                addView(crearTextView(context, "USUARIO", isHeader = true))
                addView(crearTextView(context, "PUNTUACION", isHeader = true, alignEnd = true)) // Alineación derecha
            }
            containerLayout.addView(filaHeader)

            // Crear el NestedScrollView para el contenido desplazable
            val nestedScrollView = NestedScrollView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // Crear un LinearLayout dentro del NestedScrollView para mostrar los usuarios
            val linearLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            // Crear filas por cada usuario
            usuariosLogeados.sortedByDescending { it.bestScore }.forEach { usuario ->
                val filaLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    background = android.graphics.drawable.GradientDrawable().apply {
                        setStroke(4, android.graphics.Color.DKGRAY)  // Borde gris oscuro
                    }
                }

                // Crear avatar y TextViews para username y bestScore
                val avatar = ImageView(context).apply {
                    setImageBitmap(usuario.avatar)
                    layoutParams = LinearLayout.LayoutParams(100, 100).apply {

                        marginEnd = 16 }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setPadding(10)
                }

                // Añadir avatar y textViews a la fila
                filaLayout.apply {
                    addView(avatar)
                    addView(crearTextView(context, usuario.username))
                    addView(crearTextView(context, usuario.bestScore.toString(), alignEnd = true)) // Alineación derecha para la puntuación
                }

                linearLayout.addView(filaLayout)
            }

            // Añadir el LinearLayout (con las filas) al NestedScrollView
            nestedScrollView.addView(linearLayout)

            // Añadir el NestedScrollView al contenedor principal
            containerLayout.addView(nestedScrollView)

            // Retornar el LinearLayout contenedor
            return containerLayout
        }
    }





}
package com.example.p5cav.vistas.navegacion

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.drawToBitmap
import androidx.core.widget.doAfterTextChanged
import com.example.p5cav.R
import com.example.p5cav.usuarios.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

lateinit var usuarioLogeado : User
val usuariosLogeados : MutableSet<User> = mutableSetOf()

class LoginUser : AppCompatActivity() {

    private lateinit var jugarBtn : Button
    private lateinit var usernameInput : TextView
    private lateinit var hacerFoto : ActivityResultLauncher<Intent>
    private lateinit var avatarImage : ImageView
    private lateinit var avatarDefault : Bitmap

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        jugarBtn = findViewById(R.id.jugarBtn)
        usernameInput = findViewById<EditText>(R.id.usernameInput)
        avatarImage = findViewById(R.id.userAvatar)
        avatarDefault = avatarImage.drawable.toBitmap()



        insertarUsuariosMuestra()
        hacerFoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageBitmap = result.data?.extras?.get("data") as Bitmap
                    avatarImage.setImageBitmap(imageBitmap)

                }
        }

        avatarImage.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            hacerFoto.launch(intent)
        }

        // Mi solucion:
//        usernameInput.doAfterTextChanged {
//            val replicaUsuario =  User(usernameInput.text.toString(), avatarImage.drawable.toBitmap())
//            if (usuariosLogeados.contains(replicaUsuario)) {
//
//                val indiceUser = usuariosLogeados.indexOf(replicaUsuario)
//                avatarImage.setImageBitmap(usuariosLogeados.elementAt(indiceUser).avatar)
//            } else {
//                avatarImage.setImageBitmap(avatarDefault)
//            }
//        }
        // Solucion de ChatGPT porque me fallaba en rendimiento

        // En vez de buscar en la lista un objeto User, busca utilizando el string.
        // Utiliza un debounce para que el codigo se ejecute cuando el usuario lleve un rato sin escribir
        // Hace la busqueda en otro hizo (por si la lista de usuarios es grande, para que no se bloquee la app)
        var debounceJob: Job? = null
        usernameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Cancela cualquier trabajo anterior de debounce
                debounceJob?.cancel()
                // Inicia un nuevo debounce con un retraso de 300 ms
                debounceJob = CoroutineScope(Main).launch {
                    delay(300) // Tiempo de debounce, ajustable
                    s?.let { text ->
                        // Lanza la búsqueda del usuario en el hilo IO
                        val userFound = withContext(IO) {
                            usuariosLogeados.find { it.username == text.toString() }
                        }

                        // Actualiza la interfaz en el hilo principal
                        if (userFound != null) {
                            avatarImage.setImageBitmap(userFound.avatar)
                        } else {
                            avatarImage.setImageBitmap(avatarDefault)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        jugarBtn.setOnClickListener {

            val username = usernameInput.text.toString()
            usuarioLogeado = User(if (username == "") getString(R.string.usuarioHint) else username, avatarImage.drawToBitmap())

            if (!usuariosLogeados.contains(usuarioLogeado)) {
                usuariosLogeados.add(usuarioLogeado)

            } else {
                val indexUsuario = usuariosLogeados.indexOf(usuarioLogeado)
                usuarioLogeado = usuariosLogeados.elementAt(indexUsuario)
            }

            val menu = Intent(this, MenuJuego::class.java)
            startActivity(menu)
        }
    }



    private fun insertarUsuariosMuestra() {
        usuariosLogeados.add(User("Pablo", avatarDefault, 18920))
        usuariosLogeados.add(User("Arthur", avatarDefault, 23860))
        usuariosLogeados.add(User("Ponme", avatarDefault, 39400))
        usuariosLogeados.add(User("Cesar", avatarDefault, 9600))
        usuariosLogeados.add(User("Enric", avatarDefault, 16430))
        usuariosLogeados.add(User("Hector", avatarDefault, 41000))
        usuariosLogeados.add(User("Christian", avatarDefault, 24530))
        usuariosLogeados.add(User("Vasile", avatarDefault, 18040))
        usuariosLogeados.add(User("Diego", avatarDefault, 13060))
        usuariosLogeados.add(User("10", avatarDefault, 36390))
        usuariosLogeados.add(User("Dani", avatarDefault, 1800))
        usuariosLogeados.add(User("Un", avatarDefault, 37050))
    }
}
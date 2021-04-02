package ipvc.estg.smartcities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import ipvc.estg.smartcities.api.EndPoints
import ipvc.estg.smartcities.api.ServiceBuilder
import ipvc.estg.smartcities.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.Map

class
Login : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // inicialização do SP e verificação dos dados
        val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.LoginData), Context.MODE_PRIVATE)

        val emailSP = sharedPreferences.getString("email", "")
        val passwordSP = sharedPreferences.getString("password", "")

        if (emailSP != "" && passwordSP != "") {
            correctLogin()
        }

        email = findViewById(R.id.email_et)
        password = findViewById(R.id.password_et)
        loginButton = findViewById(R.id.login_bt)

        //call the method that verify if editText values are null
        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

    }


    // MENU DE OPÇOES
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu!!.findItem(R.id.notesMenu).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notesMenu -> {
                val intent = Intent(this, Notes::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //TEXT WATCHER
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //verify if login/password are not null
            loginButton.isEnabled = !(email.text.toString() == "" || password.text.toString() == "")
        }
    }

    fun correctLogin() {
        val intent = Intent(this, Maps::class.java)
        startActivity(intent)
        finish()
    }

    fun loginButton(view: View) {
        // faz o request dos dados de login
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getUserLogin(email.text.toString(), password.text.toString())

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.LoginData), Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putInt("id", response.body()!!.id)
                        putString("name", response.body()!!.name)
                        putString("email", email.text.toString())
                        putString("password", password.text.toString())
                        commit()
                    }
                    correctLogin()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@Login, getString(R.string.email_password_incorrect), Toast.LENGTH_SHORT).show()
            }
        })

    }
}
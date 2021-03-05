package ipvc.estg.smartcities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import ipvc.estg.smartcities.api.Endpoints
import ipvc.estg.smartcities.api.ServiceBuilder

class Login : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private  lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        //val request = ServiceBuilder.buildService(Endpoints::class.java)
        //val call = request.getUsers("rycardo.dias@hotmail.com", "1")


        email = findViewById(R.id.email_et)
        password = findViewById(R.id.password_et)
        loginButton = findViewById(R.id.login_bt)

        //call the method that verify if editText values are null
        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notes -> {
                val intent = Intent(this, Notes::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //verify if login/password are not null
            loginButton.isEnabled = !(email.text.toString()=="" || password.text.toString()=="")
        }
    }

    fun loginButton(view: View) {
        val intent = Intent(this, Map::class.java)
        startActivity(intent)
        Toast.makeText(this, "Email: " + email.text + " Password: " + password.text, Toast.LENGTH_SHORT).show()
    }
}
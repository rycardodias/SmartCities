package ipvc.estg.smartcities

import android.app.Activity
import android.content.Intent
import android.os.Build.ID
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*

class AddNotes : AppCompatActivity() {
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        title = findViewById(R.id.et_title)
        description = findViewById(R.id.et_description)
        title.addTextChangedListener(textWatcher)
        description.addTextChangedListener(textWatcher)

        button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(title.text) || TextUtils.isEmpty(description.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val title = title.text.toString()
                val description = description.text.toString()
                replyIntent.putExtra(ID,  intent.getIntExtra("ID", 0))
                replyIntent.putExtra(TITLE,  title)
                replyIntent.putExtra(DESCRIPTION, description)
                replyIntent.putExtra(DATE, Date().toString())
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }

        //altera o texto
        title.setText(intent.getStringExtra("TITLE"))
        description.setText(intent.getStringExtra("DESCRIPTION"))
        if (intent.getIntExtra("ID", 0) != 0) {
            button.setText("Edit Note")
        }
    }

    companion object {
        const val ID = "com.example.android.id"
        const val TITLE = "com.example.android.title"
        const val DESCRIPTION = "com.example.android.country"
        const val DATE = "com.example.android.date"
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //verify if login/password are not null
            button.isEnabled = !(title.text.toString()=="" || description.text.toString()=="")
        }
    }
}
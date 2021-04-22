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
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import java.util.*

class AddMarker : AppCompatActivity() {
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var button: Button
    private lateinit var image: EditText
    private lateinit var carTrafficProblem: CheckBox
    private lateinit var solved: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_marker)

        title = findViewById(R.id.et_title)
        description = findViewById(R.id.et_description)
//        image =
        carTrafficProblem = findViewById(R.id.cb_trafficProblem)

        title.addTextChangedListener(textWatcher)
        description.addTextChangedListener(textWatcher)

        var trafficProblem: Int = 0
        carTrafficProblem.setOnClickListener(View.OnClickListener {
            if (carTrafficProblem.isChecked) {
                trafficProblem = 1
                Toast.makeText(this, trafficProblem.toString(), Toast.LENGTH_SHORT).show()
            } else {
                trafficProblem = 0
                Toast.makeText(this, trafficProblem.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        button = findViewById(R.id.button_save)
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
//                replyIntent.putExtra(IMAGE,  title)
                replyIntent.putExtra(CARTRAFFICPROBLEM, trafficProblem)
                replyIntent.putExtra(SOLVED, 0) //alterar

                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }

        //altera o texto
        title.setText(intent.getStringExtra("TITLE"))
        description.setText(intent.getStringExtra("DESCRIPTION"))
        if (intent.getIntExtra("ID", 0) != 0) {
            button.text = getString(R.string.edit_marker)
        }
    }

    companion object {
        const val ID = "com.example.android.id"
        const val TITLE = "com.example.android.title"
        const val DESCRIPTION = "com.example.android.country"
        const val DATE = "com.example.android.date"
        const val IMAGE = "com.example.android.image"
        const val CARTRAFFICPROBLEM = "com.example.android.cartrafficproblem"
        const val SOLVED = "com.example.android.solved"
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            button.isEnabled = !(title.text.toString()=="" || description.text.toString()=="")
        }
    }
}
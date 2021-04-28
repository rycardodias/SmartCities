package ipvc.estg.smartcities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import java.util.*


class AddMarker : AppCompatActivity() {
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var button: Button
    private lateinit var image: ImageView
    private lateinit var carTrafficProblem: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_marker)

        title = findViewById(R.id.et_title)
        description = findViewById(R.id.et_description)
        image = findViewById(R.id.iv_imagem)
        carTrafficProblem = findViewById(R.id.cb_trafficProblem)
        title.addTextChangedListener(textWatcher)
        description.addTextChangedListener(textWatcher)

        var imageURL = ""

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

        image.setOnClickListener {

        }

        button = findViewById(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(title.text) || TextUtils.isEmpty(description.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val title = title.text.toString()
                val description = description.text.toString()

                replyIntent.putExtra(ID, intent.getIntExtra("ID", 0))

                // verifica se os campos foram alterados
                if (title== intent.getStringExtra("TITLE") &&
                    description == intent.getStringExtra("DESCRIPTION") &&
                    imageURL == intent.getStringExtra("IMAGE") &&
                    trafficProblem == intent.getIntExtra("CARTRAFFICPROBLEM", 0)) {
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                } else {
                    replyIntent.putExtra(TITLE, title)
                    replyIntent.putExtra(DESCRIPTION, description)
                    replyIntent.putExtra(IMAGE,  imageURL)
                    replyIntent.putExtra(CARTRAFFICPROBLEM, trafficProblem)
                    setResult(Activity.RESULT_OK, replyIntent)
                }
            }
            finish()
        }

        /**
         * mudança dos parametros no update
         */
        title.setText(intent.getStringExtra("TITLE"))
        description.setText(intent.getStringExtra("DESCRIPTION"))

        if (intent.getIntExtra("CARTRAFFICPROBLEM", 0) == 0) {
            carTrafficProblem.setChecked(false);
        } else {
            carTrafficProblem.setChecked(true);
        }

        //adiciona imagem
        imageURL = intent.getStringExtra("IMAGE").toString()
        if (imageURL!= "") {
            Picasso.get().load(imageURL).into(image)
        }

        if (intent.getIntExtra("ID", 0) != 0) {
            button.text = getString(R.string.edit_marker)
        }

    }

    companion object {
        const val ID = "com.example.android.id"
        const val TITLE = "com.example.android.title"
        const val DESCRIPTION = "com.example.android.country"
        const val IMAGE = "com.example.android.image"
        const val CARTRAFFICPROBLEM = "com.example.android.cartrafficproblem"
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            button.isEnabled = !(title.text.toString()=="" || description.text.toString()=="")
        }
    }
}
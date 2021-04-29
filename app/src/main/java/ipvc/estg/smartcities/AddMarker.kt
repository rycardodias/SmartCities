package ipvc.estg.smartcities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import ipvc.estg.smartcities.api.EndPoints
import ipvc.estg.smartcities.api.ServiceBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*


class AddMarker : AppCompatActivity() {
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var button: Button
    private lateinit var image: ImageView
    private lateinit var carTrafficProblem: CheckBox

    //variaveis imagem
    private val pickImage = 100
    private var imageUri: Uri? = null

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

        image.setImageResource(R.drawable.ic_launcher_background)

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
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
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
                if (title == intent.getStringExtra("TITLE") &&
                    description == intent.getStringExtra("DESCRIPTION") &&
                    imageURL == intent.getStringExtra("IMAGE") &&
                    trafficProblem == intent.getIntExtra("CARTRAFFICPROBLEM", 0)) {
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                } else {
//                    uploadFile(imageUri!!)
                    replyIntent.putExtra(TITLE, title)
                    replyIntent.putExtra(DESCRIPTION, description)
                    replyIntent.putExtra(IMAGE, imageURL)
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
            carTrafficProblem.isChecked = false
        } else {
            carTrafficProblem.isChecked = true
        }
        //adiciona imagem
        imageURL = intent.getStringExtra("IMAGE").toString()

            Picasso.get().load(imageURL)
                .placeholder(R.drawable.ic_baseline_history_toggle_off_200)
                .error(R.drawable.ic_baseline_search_200)
                .resize(800, 600)
                .into(image)

        if (intent.getIntExtra("ID", 0) != 0) {
            button.text = getString(R.string.edit_marker)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            Picasso.get().load(imageUri).resize(800, 600).into(image)
            Log.d("###IMAGEM", imageUri.toString())
            uploadFile(imageUri!!)

        }
    }

    private fun uploadFile(fileUri: Uri) {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val file = File(fileUri.toString())
        // create RequestBody instance from file
        val requestFile: RequestBody = RequestBody.create(MediaType.parse(contentResolver.getType(fileUri)), Uri.decode(file.path))
        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)

        // add another part within the multipart request
        val descriptionString = "TEST"
        val description = RequestBody.create(MultipartBody.FORM, descriptionString)

        val call: Call<ResponseBody> = request.upload(description, body)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>) {
                val resposta = response.body()
                Log.v("Upload", "success" + resposta.toString())
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("Upload error:", t.message!!)
            }
        })
    }

    companion object {
        const val ID = "com.example.android.id"
        const val TITLE = "com.example.android.title"
        const val DESCRIPTION = "com.example.android.country"
        const val IMAGE = "com.example.android.image"
        const val CARTRAFFICPROBLEM = "com.example.android.cartrafficproblem"
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            button.isEnabled = !(title.text.toString() == "" || description.text.toString() == "")
        }
    }
}
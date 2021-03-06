package ipvc.estg.smartcities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.smartcities.adapter.LineAdapter
import ipvc.estg.smartcities.dataclasses.Place

class Notes : AppCompatActivity() {
    private lateinit var myList: ArrayList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        myList = ArrayList()

        for (i in 0 until 100) {
            myList.add(Place("Title $i", "Description $i"))
        }

        recyclerview.adapter = LineAdapter(myList)
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    fun deleteButton(view: View) {

    }

    fun editButton(view: View) {

    }

}
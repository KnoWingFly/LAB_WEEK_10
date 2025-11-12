package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date
import kotlin.getValue
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        prepareDatabase()
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeValueFromDatabase()

        prepareViewModel()

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    override fun onStart() {
        super.onStart()
        val total = db.totalDao().getTotal(ID)
        if (total.isNotEmpty()) {
            Toast.makeText(this, total.first().total.date, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { total ->
            updateText(total)
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java, "total-database"
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration(false)
            .build()
    }

    private fun initializeValueFromDatabase(){
        val total = db.totalDao().getTotal(ID)

        if(total.isEmpty()){
            db.totalDao().insert(Total(id = 1, total = TotalObject(0, Date().toString())))
        } else{
            viewModel.setTotal(total.first().total.value)
        }
    }

    override fun onPause(){
        super.onPause()
        db.totalDao().update(Total(ID, TotalObject(viewModel.total.value!!, Date().toString())))
    }

    companion object{
        const val ID: Long = 1
    }

}
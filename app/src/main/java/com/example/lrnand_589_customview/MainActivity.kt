package com.example.lrnand_589_customview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        customView.colorSet = mutableListOf(Color.BLUE, Color.MAGENTA, Color.CYAN, Color.RED, Color.GREEN)

        customView.limitExceededCallback = {
            Toast.makeText(this, "Game over!", Toast.LENGTH_LONG).show()
        }
    }
}
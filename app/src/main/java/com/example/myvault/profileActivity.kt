package com.example.myvault

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class profileActivity : AppCompatActivity() {
    private lateinit var name:TextView
    private lateinit var email:TextView
    private lateinit var UserAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var pie:PieChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title ="USER PROFILE"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_profile)
        name=findViewById(R.id.NameTitle)
        email=findViewById(R.id.EmailTitle)
        pie=findViewById(R.id.pieChart)

        pie.isDrawHoleEnabled=true
        pie.setHoleColor(Color.TRANSPARENT)
        pie.centerText="500 MB"
        pie.setCenterTextSize(25f)
        pie.holeRadius=75f
        pie.setCenterTextColor(Color.WHITE)
        pie.setUsePercentValues(true)
        val l=pie.legend
        l.isEnabled=false
        pie.description.isEnabled=false
        val values:ArrayList<PieEntry> = ArrayList()
        values.add(PieEntry(1.0f,"MEMORY"))
        val colors:ArrayList<Int> = ArrayList()
        val data= PieData(PieDataSet(values,"memory"))
        for (color:Int in ColorTemplate.LIBERTY_COLORS)
        {
            colors.add(color)
        }

        val dataSet:PieDataSet = PieDataSet(values,"MEMORY ANALYSIS")
        dataSet.colors=colors
        data.setDrawValues(false)
        data.setValueFormatter(PercentFormatter(pie))
        data.setValueTextSize(12f)
        pie.setDrawEntryLabels(false)
        data.setValueTextColor(Color.BLACK)
        pie.data=data
        data.setDrawValues(false)
        pie.invalidate()
        pie.animateY(1400, Easing.EaseInOutQuad)
        dbRef= FirebaseDatabase.getInstance().reference
        UserAuth= FirebaseAuth.getInstance()
        val UserUid = UserAuth.currentUser?.uid
        dbRef.child("User").child(UserUid!!).get()
            .addOnSuccessListener { snap ->
                name.text=snap.child("name").value.toString()
                email.text=snap.child("email").value.toString()
            }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
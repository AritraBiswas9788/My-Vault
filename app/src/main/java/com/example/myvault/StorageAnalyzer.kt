package com.example.myvault

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.core.graphics.toColor
import androidx.core.view.marginRight
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.roundToInt

class StorageAnalyzer : AppCompatActivity() {
    private lateinit var pieChart: PieChart
    private lateinit var UserAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var imText:TextView
    private lateinit var pdfText:TextView
    private lateinit var memText:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage_analyzer)
        supportActionBar?.title ="STORAGE ANALYZER"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pieChart=findViewById(R.id.pieChart)
        imText=findViewById(R.id.imageMemory)
        pdfText=findViewById(R.id.pdfMemory)
        memText=findViewById(R.id.totalMemory)
        dbRef= FirebaseDatabase.getInstance().reference
        UserAuth= FirebaseAuth.getInstance()
        var data:StorageData
        val userUid = UserAuth.currentUser?.uid

        pieChart.isDrawHoleEnabled=true
        //pieChart.setUsePercentValues(true)
        //pieChart.setEntryLabelColor(Color.BLACK)
        //pieChart.setEntryLabelTextSize(12f)
        pieChart.setDrawEntryLabels(false)
        pieChart.centerText="MEMORY ANALYSIS"
        pieChart.setCenterTextSize(16f)

        pieChart.description.isEnabled=false
        pieChart.setExtraOffsets(5f,5f,20f,5f)

        val l: Legend =pieChart.legend
        l.verticalAlignment=Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment=Legend.LegendHorizontalAlignment.RIGHT
        l.orientation=Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled=true
        l.textSize=12f
        l.xOffset=0f
        l.yOffset=0f

        dbRef.child("User").child(userUid!!).child("Storage").get()
            .addOnSuccessListener { snap ->
                data = StorageData(
                    snap.child("imageMemory").value.toString(),
                    snap.child("pdfMemory").value.toString(),
                    snap.child("totalMemory").value.toString()
                )
                val imFrac:Float = (data.imageMemory!!.toFloat()/(500.0*1024.0f*1024.0f)).toFloat()
                val pdfFrac:Float = (data.pdfMemory!!.toFloat()/(500.0*1024.0f*1024.0f)).toFloat()
                val memLeftFrac:Float= 1.0f -(imFrac + pdfFrac)
                var num =data.imageMemory!!.toFloat()/(1024.0*1024.0)
                var value:String
                if (num>1.0f)
                    value=num.roundToInt().toString()
                else
                {
                    num *= 100.0
                    value=(num.roundToInt()/(100.0)).toString()

                }
                value="Image Memory: $value mb"
                imText.text=value
                num =data.pdfMemory!!.toFloat()/(1024.0*1024.0)
                if (num>1.0f)
                    value=num.roundToInt().toString()
                else
                {
                    num *= 100.0
                    value=(num.roundToInt()/(100.0)).toString()

                }
                value="PDF Memory: $value MB"
                pdfText.text=value
                num =data.totalMemory!!.toFloat()/(1024.0*1024.0)
                if (num>1.0f)
                    value=num.roundToInt().toString()
                else
                {
                    num *= 100.0
                    value=(num.roundToInt()/(100.0)).toString()

                }
                value="Total Memory Left: $value mb"

                memText.text=value
               // imText.clearAnimation()
                //pdfText.clearAnimation()
                                //memText.clearAnimation()
                imText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this,R.anim.slide_in_right))
                pdfText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this,R.anim.slide_in_left))
                memText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this,R.anim.slide_in_right))

                val values:ArrayList<PieEntry> = ArrayList()
                values.add(PieEntry(imFrac,"IMAGE MEMORY"))
                values.add(PieEntry(pdfFrac,"PDF MEMORY"))
                values.add(PieEntry(memLeftFrac,"MEMORY LEFT"))

                val colors:ArrayList<Int> = ArrayList()
                //for(color:Int in ColorTemplate.MATERIAL_COLORS)
                //{
                   // colors.add(color)

                //
                // }
                for (color:Int in ColorTemplate.MATERIAL_COLORS)
                {
                    colors.add(color)
                }

                val dataSet:PieDataSet = PieDataSet(values,"MEMORY ANALYSIS")
                dataSet.colors=colors

                val data:PieData= PieData(dataSet)
                data.setDrawValues(false)
                data.setValueFormatter(PercentFormatter(pieChart))
                data.setValueTextSize(12f)
                data.setValueTextColor(Color.BLACK)
                pieChart.data=data
                pieChart.invalidate()
                pieChart.animateY(1400,Easing.EaseInOutQuad)
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
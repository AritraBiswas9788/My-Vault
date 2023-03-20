package com.example.myvault

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PdfFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PdfFragment : Fragment() {
    //Rename and change types of parameters
    //private var param1: String? = null
    //private var param2: String? = null
    private lateinit var pdfRecView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var UserAuth: FirebaseAuth
    private lateinit var pdfAdapter: PDFAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //arguments?.let {
          //  param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        //}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pdfRecView=view.findViewById(R.id.PdfRecView)
        UserAuth=FirebaseAuth.getInstance()
        dbRef= FirebaseDatabase.getInstance().reference
        pdfRecView.layoutManager=LinearLayoutManager(context)
        var fileList:ArrayList<UploadFile> = ArrayList()
        val userUid = UserAuth.currentUser?.uid
        dbRef.child("UploadFiles").child(userUid!!).child("PDFs").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fileList.clear()
                for(postSnapshot in snapshot.children)
                {
                    fileList.add(UploadFile(postSnapshot.child("filename").value.toString(),postSnapshot.child("fileUri").value.toString()))
                }
                pdfAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        pdfAdapter=PDFAdapter(requireContext(),fileList)
        pdfRecView.adapter=pdfAdapter
        pdfRecView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy > 0){
                    controlBar("hide")
                } else{
                    controlBar("show")
                }
                super.onScrolled(recyclerView, dx, dy)

            }
        })
    }
    private fun controlBar(hideStatus:String)
    {if(hideStatus.equals("hide"))
        (activity as MainActivity).hideBar()
    else
        (activity as MainActivity).showBar()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PdfFragment.
         */
        //Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PdfFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
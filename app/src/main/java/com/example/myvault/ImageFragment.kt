package com.example.myvault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageFragment() : Fragment() {
    // Rename and change types of parameters
    //private var param1: String? = null
    //private var param2: String? = null
    private lateinit var imageRecView: RecyclerView
    private lateinit var dbRef:DatabaseReference
    private lateinit var UserAuth: FirebaseAuth
    private lateinit var imageAdapter: ImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
       // }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_image, container, false)
    }
    private fun controlBar(hideStatus:String)
    {if(hideStatus.equals("hide"))
        (activity as MainActivity).hideBar()
    else
        (activity as MainActivity).showBar()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageRecView=view.findViewById(R.id.ImageRecView)
        UserAuth=FirebaseAuth.getInstance()
        dbRef=FirebaseDatabase.getInstance().reference
        imageRecView.layoutManager=GridLayoutManager(context,2)
        var fileList:ArrayList<UploadFile> = ArrayList()
        val userUid = UserAuth.currentUser?.uid
        dbRef.child("UploadFiles").child(userUid!!).child("Images").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                fileList.clear()
                for(postSnapshot in snapshot.children)
                {
                    fileList.add(UploadFile(postSnapshot.child("filename").value.toString(),postSnapshot.child("fileUri").value.toString()))
                }
                imageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        imageAdapter= ImageAdapter(requireContext(),fileList)
        imageRecView.adapter=imageAdapter
        imageRecView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
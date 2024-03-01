package com.example.guvenlipati

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.adapter.PetsAdapter
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private  lateinit var databaseReferencePets: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        databaseReferencePets =
            FirebaseDatabase.getInstance().getReference("pets")

        val profilePhoto = view.findViewById<CircleImageView>(R.id.circleImageProfilePhoto)
        val userNameEdit = view.findViewById<EditText>(R.id.editTextUserName)
        val userSurname = view.findViewById<EditText>(R.id.editTextUserSurname)
        val provinceCombo = view.findViewById<AutoCompleteTextView>(R.id.provinceCombo)
        val townCombo = view.findViewById<AutoCompleteTextView>(R.id.townCombo)
        val petRecyclerView = view.findViewById<RecyclerView>(R.id.petRecycleView)

        petRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)


        val petList = ArrayList<Pet>()


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user?.userId == firebaseUser.uid) {
                    if (user.userPhoto.isEmpty()) {
                        profilePhoto.setImageResource(R.drawable.men_image)
                    } else {
                        val imageUri = Uri.parse(user.userPhoto)
                        Glide.with(this@ProfileFragment).load(imageUri)
                            .placeholder(R.drawable.men_image)
                            .into(profilePhoto)
                    }
                    userNameEdit.setText(user.userName)
                    userSurname.setText(user.userSurname)
                    provinceCombo.setText(user.userProvince)
                    townCombo.setText(user.userTown)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        databaseReferencePets.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                petList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children)
                {
                    val pet = dataSnapShot.getValue(Pet::class.java)

                    if (pet?.userId==firebaseUser.uid){
                        pet.let {
                            petList.add(it)
                        }
                    }
                }

                val petAdapter = PetsAdapter(requireContext(),petList)
                petRecyclerView.adapter = petAdapter


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}
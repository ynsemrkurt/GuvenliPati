package com.example.guvenlipati

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guvenlipati.adapter.SelectPetsAdapter
import com.example.guvenlipati.models.Pet
import com.example.guvenlipati.models.User
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdvertCreateFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReferencePets: DatabaseReference
    private var petSelectID: String = ""
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advert_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectDateButton = view.findViewById<Button>(R.id.selectDateButton)
        val petRecyclerView = view.findViewById<RecyclerView>(R.id.petRecycleView)
        val editTextStartDate = view.findViewById<EditText>(R.id.editTextStartDate)
        val editTextEndDate = view.findViewById<EditText>(R.id.editTextEndDate)
        val jobStay = view.findViewById<Chip>(R.id.job1)
        val jobFeed = view.findViewById<Chip>(R.id.job2)
        val jobWalk = view.findViewById<Chip>(R.id.job3)
        val jobAbout = view.findViewById<EditText>(R.id.editTextJobAbout)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReferencePets = FirebaseDatabase.getInstance().getReference("pets")


        petRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val selectPetList = ArrayList<Pet>()

        val databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        databaseReferenceUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(User::class.java)
                    user = userData ?: User() // Kullanıcı verisi alınırsa user özelliğini başlat

                    // Kullanıcı verisi alındıktan sonra, pets verilerini almak için isteği gerçekleştirin
                    databaseReferencePets.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            selectPetList.clear()
                            for (dataSnapShot: DataSnapshot in snapshot.children) {
                                val pet = dataSnapShot.getValue(Pet::class.java)
                                if (pet?.userId == firebaseUser.uid) {
                                    pet.let {
                                        selectPetList.add(it)
                                    }
                                }
                            }

                            val petAdapter =
                                SelectPetsAdapter(requireContext(), selectPetList) { selectedPetId ->
                                    petSelectID = selectedPetId
                                }
                            petRecyclerView.adapter = petAdapter
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumunda yapılacak işlemler
            }
        })

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Tarih Aralığını Seç")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            editTextStartDate.setText(formatDate(startDate))
            editTextEndDate.setText(formatDate(endDate))
        }

        selectDateButton.setOnClickListener {
            dateRangePicker.show(childFragmentManager, "date_range_picker")
        }


        var checkedJobType = ""

        jobStay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedJobType = jobStay.text.toString()
            }
        }

        jobFeed.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedJobType = jobFeed.text.toString()
            }
        }

        jobWalk.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedJobType = jobWalk.text.toString()
            }
        }

        view.findViewById<Button>(R.id.JobOptionButton).setOnClickListener {

            if (auth.currentUser != null) {

                if (jobAbout.text.toString().isEmpty()) {
                    showToast("Lütfen boş alan bırakmayınız!")
                    return@setOnClickListener
                }
                if (editTextStartDate.text.toString().isEmpty()|| editTextEndDate.text.toString().isEmpty()) {
                    showToast("Lütfen tarih seçiniz!")
                    return@setOnClickListener
                }
                if (checkedJobType.isEmpty()) {
                    showToast("Lütfen hizmet türü seçiniz!")
                    return@setOnClickListener
                }
                if (petSelectID.isEmpty()){
                    showToast("Lütfen dostunuzu seçiniz!")
                    return@setOnClickListener
                }

                val hashMap: HashMap<String, Any> = HashMap()
                val jobId = firebaseUser.uid + petSelectID
                hashMap["jobId"] = jobId
                hashMap["jobType"] = checkedJobType
                hashMap["jobAbout"] = jobAbout.text.toString()
                hashMap["jobProvince"] = user.userProvince
                hashMap["jobStatus"] = true
                hashMap["userID"] = firebaseUser.uid
                hashMap["petID"] = petSelectID
                hashMap["jobTown"] = user.userTown
                hashMap["jobStartDate"] = editTextStartDate.text.toString()
                hashMap["jobEndDate"] = editTextEndDate.text.toString()


                val reference = FirebaseDatabase.getInstance().getReference("jobs").child(jobId)
                reference.setValue(hashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        (activity as HomeActivity).goHomeFragment()
                        showToast("İş Kaydı Başarılı!")
                    } else {
                        showToast("Hatalı işlem!")
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showMaterialDialog()
        }
    }
    private fun showMaterialDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Emin Misiniz?")
            .setMessage("Eğer geri dönerseniz iş kaydınız silinecektir.")
            .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.background_dialog))
            .setPositiveButton("Geri Dön") { _, _ ->
                showToast("Kaydınız iptal edildi.")
                requireActivity().finish()
            }
            .setNegativeButton("İptal") { _, _ ->
                showToast("İptal Edildi")
            }
            .show()
    }
    private fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

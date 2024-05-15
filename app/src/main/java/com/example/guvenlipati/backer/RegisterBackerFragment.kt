package com.example.guvenlipati.backer

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guvenlipati.R
import com.example.guvenlipati.databinding.FragmentRegisterBackerBinding
import com.example.guvenlipati.home.HomeActivity
import com.example.guvenlipati.models.Backer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.ByteArrayInputStream
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilderFactory

class RegisterBackerFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference
    private var verificationStatus: Boolean = false
    private lateinit var binding: FragmentRegisterBackerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var dogJob = false
        var catJob = false
        var birdJob = false

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("identifies")
                .child(firebaseUser.uid)
        databaseReference2 =
            FirebaseDatabase.getInstance().getReference("users")
                .child(firebaseUser.uid)

        binding.dogs.setOnCheckedChangeListener { _, isChecked ->
            dogJob = isChecked
        }

        binding.cats.setOnCheckedChangeListener { _, isChecked ->
            catJob = isChecked
        }

        binding.birds.setOnCheckedChangeListener { _, isChecked ->
            birdJob = isChecked
        }


        binding.ConfirmBackerButton.setOnClickListener {
            val backerBirthYear = binding.editTextAge.text.toString().toDoubleOrNull()

            if (auth.currentUser != null) {
                if (binding.editTextBackerName.text.trim()
                        .isEmpty() || binding.editTextBackerSurname.text.trim()
                        .isEmpty() || binding.editTextAdress.text.trim()
                        .isEmpty() || binding.editTextExperience.text.trim()
                        .isEmpty() || binding.editTextBackerAbout.text.trim()
                        .isEmpty() || binding.editTextPetNumber.text.trim().isEmpty()
                ) {
                    showToast("Lütfen boş alan bırakmayınız!")
                    return@setOnClickListener
                }
                if (!isTCKNCorrect(binding.editTextID.text.toString())) {
                    showToast("TC kimlik numaranızı doğru giriniz!")
                    return@setOnClickListener
                }
                val currentYear = LocalDate.now().year
                val expInt = binding.editTextExperience.text.toString().toDouble()


                if (backerBirthYear != null) {
                    val backerAge = (currentYear - backerBirthYear)

                    if (backerBirthYear < currentYear - 80 || backerBirthYear > currentYear - 18 || binding.editTextAge.text.toString()
                            .isEmpty() || expInt >= backerAge
                    ) {
                        showToast("Doğum yılınızı ve Deneyim Sürenizi doğru giriniz!")
                        return@setOnClickListener
                    }
                }

                if (!binding.dogs.isChecked && !binding.cats.isChecked && !binding.birds.isChecked) {
                    showToast("Lütfen En Az Bir Hayvan Seçiniz!")
                    return@setOnClickListener
                }

                if (!binding.checkBox.isChecked || !binding.checkBox2.isChecked || !binding.checkBox3.isChecked) {
                    showToast("Sözleşmeleri kabul etmeniz gerekmektedir!")
                    return@setOnClickListener
                }

                //KPSPublic API
                val soapRequestTask = SoapRequestTask()
                val result = soapRequestTask.execute(
                    binding.editTextBackerName.text.toString(),
                    binding.editTextBackerSurname.text.toString(),
                    binding.editTextID.text.toString(),
                    binding.editTextAge.text.toString()
                ).get()

                if (!result) {
                    showToast("Lütfen Tc, Ad ve Soyad gibi bilgilerini doğru giriniz!")
                    return@setOnClickListener
                }

                val databaseReferenceTCKN =
                    FirebaseDatabase.getInstance().getReference("identifies")
                databaseReferenceTCKN.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.progressCard.visibility = View.VISIBLE
                        binding.buttonPaw2.visibility = View.INVISIBLE
                        binding.ConfirmBackerButton.visibility = View.INVISIBLE
                        var tcStatus = false
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val backer = dataSnapshot.getValue(Backer::class.java)
                            if (backer?.TC == binding.editTextID.text.toString()) {
                                tcStatus = true
                                break
                            }
                        }
                        if (tcStatus) {
                            showToast("Aynı TC kimlik numarası kullanılamaz!")
                            binding.progressCard.visibility = View.INVISIBLE
                            binding.buttonPaw2.visibility = View.VISIBLE
                            binding.ConfirmBackerButton.visibility = View.VISIBLE
                            return
                        } else {

                            val hashMap= mutableMapOf(
                                "userID" to auth.currentUser!!.uid,
                                "legalName" to binding.editTextBackerName.text.toString(),
                                "legalSurname" to binding.editTextBackerSurname.text.toString(),
                                "TC" to binding.editTextID.text.toString(),
                                "backerBirthYear" to binding.editTextAge.text.toString(),
                                "adress" to binding.editTextAdress.text.toString(),
                                "experience" to binding.editTextExperience.text.toString(),
                                "petNumber" to binding.editTextPetNumber.text.toString(),
                                "about" to binding.editTextBackerAbout.text.toString(),
                                "dogBacker" to dogJob,
                                "catBacker" to catJob,
                                "birdBacker" to birdJob
                            )

                            databaseReference2.child("userBacker").setValue(true)

                            databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showBottomSheet()
                                } else {
                                    showToast("Hatalı işlem!")
                                }
                                binding.progressCard.visibility = View.INVISIBLE
                                binding.buttonPaw2.visibility = View.VISIBLE
                                binding.ConfirmBackerButton.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast("Hatalı işlem!")
                    }
                })
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isTCKNCorrect(id: String?): Boolean {
        if (id == null) return false

        if (id.length != 11) return false

        val chars = id.toCharArray()
        val a = IntArray(11)

        for (i in 0 until 11) {
            a[i] = chars[i] - '0'
        }

        if (a[0] == 0) return false
        if (a[10] % 2 == 1) return false

        if (((a[0] + a[2] + a[4] + a[6] + a[8]) * 7 - (a[1] + a[3] + a[5] + a[7])) % 10 != a[9]) return false

        if ((a[0] + a[1] + a[2] + a[3] + a[4] + a[5] + a[6] + a[7] + a[8] + a[9]) % 10 != a[10]) return false

        return true
    }


    //KPSPublic API
    inner class SoapRequestTask : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String?): Boolean {
            val client = OkHttpClient()

            val requestBody = getSoapRequestBody(params[0], params[1], params[2], params[3])
            val request = Request.Builder()
                .url("https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx")
                .post(requestBody)
                .addHeader("Content-Type", "application/soap+xml; charset=utf-8")
                .addHeader(
                    "Cookie",
                    "TS01326bb0=0179b2ce456757ef584bd54018d03ef6bde1c4dd044111d4a52c7617577d052b47928daeb9c9509323b952a0b2cfb2ab4746016de3"
                )
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body.string()

                return parseSoapResponse(responseBody)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        override fun onPostExecute(result: Boolean) {
            verificationStatus = result
        }

        private fun getSoapRequestBody(
            name: String?,
            surname: String?,
            identityNumber: String?,
            birthYear: String?
        ): okhttp3.RequestBody {
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\r\n  <soap12:Body>\r\n    <TCKimlikNoDogrula xmlns=\"http://tckimlik.nvi.gov.tr/WS\">\r\n      <TCKimlikNo>$identityNumber</TCKimlikNo>\r\n      <Ad>$name</Ad>\r\n      <Soyad>$surname</Soyad>\r\n      <DogumYili>$birthYear</DogumYili>\r\n    </TCKimlikNoDogrula>\r\n  </soap12:Body>\r\n</soap12:Envelope>".toRequestBody(
                "application/soap+xml; charset=utf-8".toMediaType()
            )
        }

        private fun parseSoapResponse(responseBody: String?): Boolean {
            try {
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val input = ByteArrayInputStream(responseBody?.toByteArray())

                val document: Document = builder.parse(input)
                val element: Element = document.documentElement

                val nodeList: NodeList = element.getElementsByTagName("TCKimlikNoDogrulaResult")

                if (nodeList.length > 0) {
                    val resultValue = nodeList.item(0).textContent.trim()
                    return resultValue.toBoolean()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_backer, null)
        view.findViewById<Button>(R.id.backToMain).setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }
}
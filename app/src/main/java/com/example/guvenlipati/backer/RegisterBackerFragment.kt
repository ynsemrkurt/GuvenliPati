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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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


        val editTextBackerName = binding.editTextBackerName
        val editTextBackerSurname = binding.editTextBackerSurname
        val editTextID = binding.editTextID
        val editTextAge = binding.editTextAge
        val editTextAdress = binding.editTextAdress
        val editTextExperience = binding.editTextExperience
        val editTextPetNumber = binding.editTextPetNumber
        val editTextBackerAbout = binding.editTextBackerAbout
        val checkBox = binding.checkBox
        val checkBox2 = binding.checkBox2
        val checkBox3 = binding.checkBox3
        val confirmBackerButton = binding.ConfirmBackerButton
        val progressCard = binding.progressCard
        val buttonPaws = binding.buttonPaw2

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("identifies")
                .child(firebaseUser.uid)
        databaseReference2 =
            FirebaseDatabase.getInstance().getReference("users")
                .child(firebaseUser.uid)


        confirmBackerButton.setOnClickListener {
            val backerBirthYear = editTextAge.text.toString().toDoubleOrNull()

            if (auth.currentUser != null) {

                if (editTextBackerName.text.trim().isEmpty() || editTextBackerSurname.text.trim()
                        .isEmpty() || editTextAdress.text.trim()
                        .isEmpty() || editTextExperience.text.trim()
                        .isEmpty() || editTextBackerAbout.text.trim()
                        .isEmpty() || editTextPetNumber.text.trim().isEmpty()
                ) {
                    showToast("Lütfen boş alan bırakmayınız!")
                    return@setOnClickListener
                }
                if (!isTCKNCorrect(editTextID.text.toString())) {
                    showToast("TC kimlik numaranızı doğru giriniz!")
                    return@setOnClickListener
                }
                val currentYear = LocalDate.now().year
                val expInt = editTextExperience.text.toString().toDouble()

                if (backerBirthYear != null) {
                    val backerAge = (currentYear - backerBirthYear)

                    if (backerBirthYear < currentYear - 80 || backerBirthYear > currentYear - 18 || editTextAge.text.toString()
                            .isEmpty() || expInt >= backerAge
                    ) {
                        showToast("Doğum yılınızı ve Deneyim Sürenizi doğru giriniz!")
                        return@setOnClickListener
                    }
                }
                if (!checkBox.isChecked || !checkBox2.isChecked || !checkBox3.isChecked) {
                    showToast("Sözleşmeleri kabul etmeniz gerekmektedir!")
                    return@setOnClickListener
                }

                //KPSPublic API
                val soapRequestTask = SoapRequestTask()
                val result = soapRequestTask.execute(
                    editTextBackerName.text.toString(),
                    editTextBackerSurname.text.toString(),
                    editTextID.text.toString(),
                    editTextAge.text.toString()
                ).get()

                if (!result) {
                    showToast("Lütfen Tc, Ad ve Soyad gibi bilgilerini doğru giriniz!")
                    return@setOnClickListener
                }




                progressCard.visibility = View.VISIBLE
                buttonPaws.visibility = View.INVISIBLE
                confirmBackerButton.visibility = View.INVISIBLE

                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["userID"] = auth.currentUser!!.uid
                hashMap["legalName"] = editTextBackerName.text.toString()
                hashMap["legalSurname"] = editTextBackerSurname.text.toString()
                hashMap["TC"] = editTextID.text.toString()
                hashMap["backerBirthYear"] = editTextAge.text.toString()
                hashMap["adress"] = editTextAdress.text.toString()
                hashMap["experience"] = editTextExperience.text.toString()
                hashMap["petNumber"] = editTextPetNumber.text.toString()
                hashMap["about"] = editTextBackerAbout.text.toString()
                hashMap["dogBacker"] = false
                hashMap["catBacker"] = false
                hashMap["birdBacker"] = false
                hashMap["userAvailability"] = 0
                hashMap["homeJob"] = false
                hashMap["feedingJob"] = false
                hashMap["walkingJob"] = false
                hashMap["homeMoney"] = 0
                hashMap["feedingMoney"] = 0
                hashMap["walkingMoney"] = 0
                //Müsaitlik durumu 1->Hafta İçi 2->Hafta Sonu 3->Tüm Günler

                databaseReference2.child("userBacker").setValue(true)

                databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showBottomSheet()
                    } else {
                        showToast("Hatalı işlem!")
                    }
                    progressCard.visibility = View.INVISIBLE
                    buttonPaws.visibility = View.VISIBLE
                    confirmBackerButton.visibility = View.VISIBLE
                }
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
            if (result) {
                verificationStatus = true
            } else {
                verificationStatus = false
            }
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
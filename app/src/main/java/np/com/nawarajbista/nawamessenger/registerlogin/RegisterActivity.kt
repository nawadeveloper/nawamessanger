package np.com.nawarajbista.nawamessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import np.com.nawarajbista.nawamessenger.R
import np.com.nawarajbista.nawamessenger.messages.LatestMessagesActivity
import np.com.nawarajbista.nawamessenger.models.User
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sign_in_register_layout.setOnClickListener {
            Log.d("allready_have_account", "go to login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        button_register_register_layout.setOnClickListener {
            performRegistration()
        }

        //adding image to register
        image_register_layout.setOnClickListener {
            Log.d("mainActivity", "select image")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    //handling screen rotation
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        val emailInput = email_register_layout.text
        val userInput = user_name_register_layout.text
        val passwordInput = password_register_layout.text

        outState?.putCharSequence("saveEmail", emailInput)
        outState?.putCharSequence("saveUser", userInput)
        outState?.putCharSequence("savePassword", passwordInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val emailInput = savedInstanceState?.getCharSequence("saveEmail")
        val userInput = savedInstanceState?.getCharSequence("saveUser")
        val passwordInput = savedInstanceState?.getCharSequence("savePassword")

        email_register_layout.setText(emailInput)
        user_name_register_layout.setText(userInput)
        password_register_layout.setText(passwordInput)
    }

    //handling  firebase and adding image
    private var selectedPhotoURI: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //photo is selected here
            Log.d("mainActivity", "photo data is selected")

            selectedPhotoURI = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoURI)

            circular_image_view_register_layout.setImageBitmap(bitmap)
            image_register_layout.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            image_register_layout.setBackgroundDrawable(bitmapDrawable)


        }
    }

    private fun performRegistration() {
        val email = email_register_layout.text.toString()
        val password = password_register_layout.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("mainActivity","$email and $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                //if not success
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("mainActivity", "Successfully created user")
                uploadImageToFirebaseStorage()

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("mainActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
            }

    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoURI == null) return
        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$fileName")
        ref.putFile(selectedPhotoURI!!)
            .addOnSuccessListener {
                Log.d("mainActivity", "image uploaded successfully: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("mainActivity", "file location: $it")
                    saveUserToFirebaseDatabase(it.toString())

                }
            }
            .addOnFailureListener {
                Log.d("mainActivity", "file upload failed")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        val user = User(
            uid,
            user_name_register_layout.text.toString(),
            profileImageUrl
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("mainActivity","saved to firebase database")
            }
            .addOnFailureListener {
                Log.d("mainActivity", "$it")
            }
    }
}

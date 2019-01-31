package np.com.nawarajbista.nawamessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        button_register_register_layout.setOnClickListener {
            performRegistration()
        }


        sign_in_register_layout.setOnClickListener {
            Log.d("all_ready_have_an_account", "go to login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegistration() {
        val email = email_register_layout.text.toString()
        val password = password_register_layout.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

//        Log.d("register","$email and $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                //if not success
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("main", "Successfully created user")

            }
            .addOnFailureListener {
                Log.d("main", "Failed to create user: ${it.message}")
                Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
            }

    }
}

package np.com.nawarajbista.nawamessenger.registerlogin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import np.com.nawarajbista.nawamessenger.R
import np.com.nawarajbista.nawamessenger.messages.LatestMessagesActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        sign_in_button_sign_in_layout.setOnClickListener {
            performSignIn()
        }

        back_to_register_login_layout.setOnClickListener {
            finish()
        }
    }

    private fun performSignIn() {
        val email = email_sign_in_layout.text.toString()
        val password = password_sign_in_layout.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "You must provide email and password to login", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                //if not success
                if(!it.isSuccessful) return@addOnCompleteListener

                //if success
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Toast.makeText(this,"Failed to sign in ${it.message}", Toast.LENGTH_SHORT).show()
            }


    }
}
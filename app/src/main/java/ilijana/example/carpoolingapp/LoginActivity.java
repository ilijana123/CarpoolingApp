package ilijana.example.carpoolingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView goToSignup;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        goToSignup = findViewById(R.id.goToSignup);

        loginButton.setOnClickListener(view -> {
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            } else if (databaseHelper.checkEmailPassword(email, password)) {

                Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                        "SELECT id, name, surname, userType FROM users WHERE email = ?",
                        new String[]{email});

                if (cursor.moveToFirst()) {
                    int userId = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String surname = cursor.getString(2);
                    String userType = cursor.getString(3);

                    Log.d("LoginActivity", "Retrieved userId: " + userId);
                    Log.d("LoginActivity", "User Type: " + userType);

                    Intent intent;
                    if ("Driver".equals(userType)) {
                        intent = new Intent(this, DriverActivity.class);
                    } else {
                        intent = new Intent(this, PassengerActivity.class);
                    }

                    intent.putExtra("userId", userId);
                    intent.putExtra("name", name);
                    intent.putExtra("surname", surname);
                    startActivity(intent);
                    cursor.close();
                }
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        goToSignup.setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }
}

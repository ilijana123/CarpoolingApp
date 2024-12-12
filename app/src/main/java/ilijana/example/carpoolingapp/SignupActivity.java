package ilijana.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    EditText signupEmail, signupPassword, signupConfirmPassword, signupName, signupSurname;
    RadioGroup userTypeGroup;
    Button signupButton;
    TextView goToLogin;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseHelper = new DatabaseHelper(this);

        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupConfirmPassword = findViewById(R.id.signupConfirmPassword);
        signupName = findViewById(R.id.signupName);
        signupSurname = findViewById(R.id.signupSurname);
        userTypeGroup = findViewById(R.id.userTypeGroup);
        signupButton = findViewById(R.id.signupButton);
        goToLogin = findViewById(R.id.goToLogin);

        signupButton.setOnClickListener(view -> {
            String name = signupName.getText().toString().trim();
            String surname = signupSurname.getText().toString().trim();
            String email = signupEmail.getText().toString().trim();
            String password = signupPassword.getText().toString();
            String confirmPassword = signupConfirmPassword.getText().toString();
            int selectedUserTypeId = userTypeGroup.getCheckedRadioButtonId();
            String userType = selectedUserTypeId == R.id.passengerRadio ? "Passenger" : "Driver";

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedUserTypeId == -1) {
                Toast.makeText(SignupActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (databaseHelper.checkEmail(email)) {
                Toast.makeText(SignupActivity.this, "User already exists! Please login.", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = databaseHelper.insertData(email, password, userType, name, surname);
                if (inserted) {
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        goToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

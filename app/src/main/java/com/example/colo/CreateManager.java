package com.example.colo;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CreateManager extends AppCompatActivity
{

    EditText Name, Email, Username, Password, VerifyPassword, EmployeeID;
    TextView DateText;
    Button DatePicker, CreateAccountBTN;
    DatePickerDialog.OnDateSetListener dateSetListener;
    RadioGroup RadioGroupGender, RadioGroupRole;
    RadioButton RadioButtonGender, RadioButtonRole;

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String EMPLOYEE = "Employee";
    private static final String TAG = "CreateAccount";
    private UserHelperClass userHelperClass;
    private String CompanyName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_manager);

        Name = (EditText) findViewById(R.id.etNameEntry);
        Email = (EditText) findViewById(R.id.etEmailEntry);
        Username = (EditText) findViewById(R.id.etUserNameEntry);
        Password = (EditText) findViewById(R.id.etPasswordEntry);
        VerifyPassword = (EditText) findViewById(R.id.etPasswordConfirmation);
        EmployeeID = (EditText) findViewById(R.id.etEmployeeID);
        DatePicker = (Button) findViewById(R.id.btnSelectDate);
        DateText = (TextView) findViewById(R.id.tvDateText);
        CreateAccountBTN = (Button) findViewById(R.id.btnCreateAccount);

        RadioGroupGender = (RadioGroup) findViewById(R.id.RadioGroupGender);
        RadioButtonGender = (RadioButton) findViewById(R.id.rbNoAnswer);
        CompanyName = ((GlobalCompanyName) this.getApplication()).getGlobalCompanyName();


        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(EMPLOYEE);
        mAuth = FirebaseAuth.getInstance();


        //Date of Birth Button
        DatePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(CreateManager.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        //Date of Birth Text View
        dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                DateText.setText(date);
            }
        };

        //save data in Firebase on button click
        CreateAccountBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Manager is creating this employee/manager
                // Company attribute
                String companyName = CompanyName;
                String name = Name.getText().toString();
                String email = Email.getText().toString();
                String userName = Username.getText().toString();
                String password = Password.getText().toString();
                String employeeID = EmployeeID.getText().toString();
                String dateText = DateText.getText().toString();
                String gender = RadioButtonGender.getText().toString();
                String role = "Manager";

                if (validateName() & validateEmail() & validateUserName() & validatePassword() & validateVerificationPassword() & validateID() & validateDate() & validateGender() & validateRole())
                {
                    userHelperClass = new UserHelperClass(companyName, name, email, userName, password, employeeID, dateText, gender, role, null, null,true);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(getApplicationContext(), "Account successfully created", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "createUserWithEmail:success");
//                                      FirebaseDatabase.getInstance().getReference("Employees "+uidpath);
                                        FirebaseDatabase.getInstance().getReference("Companies").child(companyName).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(userHelperClass);
                                    } else
                                    {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(CreateManager.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    finish();
                }
            }
        });
    }


    public void checkButtonGender(View v)
    {
        int radioId = RadioGroupGender.getCheckedRadioButtonId();
        RadioButtonGender = findViewById(radioId);
    }

    public void checkButtonRole(View v)
    {
        int radioId = RadioGroupRole.getCheckedRadioButtonId();
        RadioButtonRole = findViewById(radioId);
    }


    private boolean validateName()
    {
        String val = Name.getText().toString();

        if (val.isEmpty())
        {
            Name.setError("Field can not be empty");
            return false;
        } else
        {
            Name.setError(null);
            return true;
        }
    }

    private boolean validateEmail()
    {
        String val = Email.getText().toString();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty())
        {
            Email.setError("Field can not be empty");
            return false;

        } else if (!val.matches(checkEmail))
        {
            Email.setError("Invalid Email");
            return false;
        } else
        {

            Email.setError(null);
            return true;
        }
    }

    private boolean validateUserName()
    {
        String val = Username.getText().toString();
        String checkSpaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty())
        {
            Username.setError("Field can not be empty");
            return false;
        } else if (val.length() > 20)
        {
            Username.setError("Username is too big");
            return false;
        } else if (!val.matches(checkSpaces))
        {
            Username.setError("No white spaces are allowed");
            return false;
        } else
        {

            Username.setError(null);
            return true;
        }
    }

    private boolean validatePassword()
    {
        String val = Password.getText().toString();

        if (val.isEmpty())
        {
            Password.setError("Field can not be empty");
            return false;

        } else if (Password.length() < 6)
        {
            Password.setError("Password needs to be at least 6 characters long");
            return false;
        } else
        {

            Password.setError(null);
            return true;
        }
    }

    private boolean validateVerificationPassword()
    {
        String val = VerifyPassword.getText().toString();

        if (val.isEmpty())
        {
            VerifyPassword.setError("Field can not be empty");
            return false;

        } else if (!(Password.getText().toString().equals(VerifyPassword.getText().toString())))
        {
            VerifyPassword.setError("The passwords do not match");
            return false;
        } else
        {
            VerifyPassword.setError(null);
            return true;
        }
    }

    private boolean validateID()
    {
        String val = EmployeeID.getText().toString();

        if (val.isEmpty())
        {
            EmployeeID.setError("Field can not be empty");
            return false;
        } else
        {
            EmployeeID.setError(null);
            return true;
        }
    }

    private boolean validateDate()
    {
        String val = DateText.getText().toString();

        if (val.equals("Date"))
        {
            DateText.setError("Please enter your birthday");
            return false;
        } else
        {
            DateText.setError(null);
            return true;
        }
    }

    private boolean validateGender()
    {
        if (RadioGroupGender.getCheckedRadioButtonId() == -1)
        {
            RadioButtonGender.setError("Please select the gender");
            return false;
        } else
        {
            RadioButtonGender.setError(null);
            return true;
        }
    }


    private boolean validateRole()
    {
        if (RadioGroupRole.getCheckedRadioButtonId() == -1)
        {
            RadioButtonRole.setError("Please select the role");
            return false;
        } else
        {
            RadioButtonRole.setError(null);
            return true;
        }
    }


}
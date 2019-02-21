package views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.android.dooyd.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import webservices.WebService;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    String fromPage = "";
    private TextInputEditText editTextName;
    private TextInputEditText editTextMobile;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Pattern pattern;
    private ProgressBar regProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initViews();

        pattern = Pattern.compile("((?=.*\\d)(?=.*[A-Z])(?=.*\\W).{8,8})");

    }

    private void initViews() {

        if (getIntent() != null && getIntent().hasExtra("FROM_PAGE"))
            fromPage = getIntent().getStringExtra("FROM_PAGE");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextName = findViewById(R.id.regNameView);
        editTextMobile = findViewById(R.id.regMobileView);
        editTextEmail = findViewById(R.id.regEmailView);
        editTextPassword = findViewById(R.id.regPasswordView);
        regProgressBar = findViewById(R.id.regProgressBar);

        MaterialButton buttonRegister = findViewById(R.id.registerButton);
        AppCompatTextView loginNow = findViewById(R.id.login_now_view);

        //SET CLICK EVENTS
        buttonRegister.setOnClickListener(this);
        loginNow.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void register() {

        JSONObject rawJson = new JSONObject();

        if (!TextUtils.isEmpty(editTextName.getText()) && editTextName.getText() != null) {

            if (!TextUtils.isEmpty(editTextMobile.getText()) && editTextMobile.getText() != null) {

                if (!TextUtils.isEmpty(editTextEmail.getText()) && editTextEmail.getText() != null) {

                    if (isValidEmail(editTextEmail.getText())) {

                        if (!TextUtils.isEmpty(editTextPassword.getText()) && editTextPassword.getText() != null) {

                            if (pattern.matcher(editTextPassword.getText()).find()) {

                                regProgressBar.setVisibility(View.VISIBLE);

                                try {
                                    rawJson.put("name", editTextName.getText().toString());
                                    rawJson.put("mobile", editTextMobile.getText().toString());
                                    rawJson.put("email", editTextEmail.getText().toString());
                                    rawJson.put("password", editTextPassword.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rawJson.toString());

                                WebService.createApiService().addCustomerProfile(body).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                                        Log.d("REG RESPONSE", "" + response);
                                        if (response.body() != null && response.isSuccessful()) {
                                            if (response.body().matches("Registered successfully & Activation Mail sent to your mail")) {
                                                showToast(response.body());
                                                editTextName.getText().clear();
                                                editTextMobile.getText().clear();
                                                editTextEmail.getText().clear();
                                                editTextPassword.getText().clear();
                                                if (!fromPage.equals("PRODUCT"))
                                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                if (response.body().matches("Mobile/Email alredy exists")) {
                                                    showToast(response.body());
                                                    editTextName.requestFocus();
                                                } else {
                                                    showToast("Error occurred try again later.");
                                                }
                                            }

                                        } else {
                                            showToast("Error occurred try again later.");
                                        }

                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                        showToast("" + t.getMessage());
                                    }
                                });
                            } else {
                                showToast("Invalid Password, Try with atleast one special character,one alpha numeric values,one uppercase letter & minimum 8 characters");
                            }

                        } else {
                            showToast("Password not be empty");
                        }
                    } else {
                        showToast("Invalid Email Id");
                    }

                } else {
                    showToast("Email not be empty");
                }
            } else {
                showToast("Mobile number not be empty");
            }
        } else {
            showToast("Name not be empty.");
        }


    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        regProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.registerButton: {
                register();
                break;
            }

            case R.id.login_now_view: {
                if (!fromPage.equals("PRODUCT"))
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
                break;
            }
        }

    }

    private boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}

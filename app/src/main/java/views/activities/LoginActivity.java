package views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.android.dooyd.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import datamodel.Constants;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import webservices.WebService;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    String fromPage = "";
    private TextInputEditText editTextUserName;
    private TextInputEditText editTextPassword;
    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

    }

    private void initViews() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null && getIntent().hasExtra("FROM_PAGE"))
            fromPage = getIntent().getStringExtra("FROM_PAGE");

        editTextUserName = findViewById(R.id.loginUserNameView);
        editTextPassword = findViewById(R.id.loginPasswordView);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        loginProgressBar.setVisibility(View.INVISIBLE);
        MaterialButton loginButton = findViewById(R.id.loginButton);
        AppCompatTextView registerNow = findViewById(R.id.registerNow);
        AppCompatTextView forgotPassword = findViewById(R.id.forgot_password);

        //SET CLICK EVENTS
        loginButton.setOnClickListener(this);
        registerNow.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton: {
                loginProgressBar.setVisibility(View.VISIBLE);
                login();
                break;
            }

            case R.id.registerNow: {
                if (!fromPage.equals("PRODUCT"))
                    finish();
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.putExtra("FROM_PAGE", fromPage);
                startActivity(intent);
                break;
            }

            case R.id.forgot_password: {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("FROM_PAGE", fromPage);
                startActivity(intent);
                break;
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void login() {
        JSONObject rawJson = new JSONObject();
        if (!TextUtils.isEmpty(editTextUserName.getText()) && editTextUserName.getText() != null) {
            String userName = editTextUserName.getText().toString();
            if (!TextUtils.isEmpty(editTextPassword.getText()) && editTextPassword.getText() != null) {
                String password = editTextPassword.getText().toString();
                try {
                    rawJson.put("username", userName);
                    rawJson.put("password", password);
                    rawJson.put("userType", "1");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rawJson.toString());
                WebService.createApiService().validateCustomerLogin(body).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Log.d("REG RESPONSE", "" + response);
                        loginProgressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            //SUCCESSFUL LOGIN
                            try {
                                JSONObject responseJson = new JSONObject(response.body());
                                String userToken = responseJson.getString("token");
                                SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                                // WRITING VALUE
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(Constants.SHARED_KEY_USER_TOKEN, userToken);
                                editor.putString(Constants.SHARED_KEY_LOGGED_VALUE, "1");
                                editor.apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            showToast("Login Successfully");
                            if (fromPage.equals("PRODUCT")) {

                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                            } else
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            try {
                                String message = response.errorBody().string();
                                if(!message.isEmpty())
                                {
                                    showToast(message);
                                }
                                else {
                                    showToast("Please try again");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        loginProgressBar.setVisibility(View.GONE);
                        showToast("" + t.getMessage());
                    }
                });

            } else {
                loginProgressBar.setVisibility(View.GONE);
                showToast("Password not be empty");
            }
        } else {
            loginProgressBar.setVisibility(View.GONE);
            showToast("Mobile/Email not be empty");
        }
    }
}

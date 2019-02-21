package views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import com.android.dooyd.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import datamodel.Constants;
import datamodel.ProfileItem;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import views.activities.ForgotPasswordActivity;
import views.activities.LoginActivity;
import views.activities.MainActivity;
import views.activities.RegistrationActivity;
import webservices.WebService;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextInputEditText editTextUserName;
    private TextInputEditText editTextPassword;
    private ProgressBar loginProgressBar;
    MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = null;

        if (getActivity() != null) {
            ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(true);
            }
            actionBar.setTitle("Profile");


        }

        rootView = inflater.inflate(R.layout.layout_go_login, container, false);
        setLoginView(rootView);

        return rootView;
    }

    private void setLoginView(View rootView) {
        editTextUserName = rootView.findViewById(R.id.loginUserNameView);
        editTextPassword = rootView.findViewById(R.id.loginPasswordView);
        loginProgressBar = rootView.findViewById(R.id.loginProgressBar);
        loginProgressBar.setVisibility(View.INVISIBLE);
        MaterialButton loginButton = rootView.findViewById(R.id.loginButton);
        AppCompatTextView registerNow = rootView.findViewById(R.id.registerNow);
        AppCompatTextView forgotPassword = rootView.findViewById(R.id.forgot_password);

        //SET CLICK EVENTS
        loginButton.setOnClickListener(this);
        registerNow.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  MainActivity)
            mainActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                Intent intent = new Intent(mainActivity, RegistrationActivity.class);
                intent.putExtra("FROM_PAGE", "");
                startActivity(intent);
                break;
            }

            case R.id.forgot_password: {
                Intent intent = new Intent(mainActivity, ForgotPasswordActivity.class);
                intent.putExtra("FROM_PAGE", "");
                startActivity(intent);
                break;
            }
        }
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
                                SharedPreferences settings = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                                // WRITING VALUE
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(Constants.SHARED_KEY_USER_TOKEN, userToken);
                                editor.putString(Constants.SHARED_KEY_LOGGED_VALUE, "1");
                                editor.apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            showToast("Login Successfully");

                            mainActivity.refreshAdapter();

                        }  else {
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

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}

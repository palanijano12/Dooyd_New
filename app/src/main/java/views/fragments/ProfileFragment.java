package views.fragments;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import com.android.dooyd.R;
import com.google.android.material.button.MaterialButton;
import datamodel.Constants;
import datamodel.ProfileItem;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import views.activities.MainActivity;
import webservices.WebService;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private AppCompatEditText profileNameView;
    private AppCompatEditText profileMobileView;
    private AppCompatEditText profileEmailView;
//    private AppCompatEditText profilePasswordView;
    MaterialButton logoutButton;
    SharedPreferences settings;
    private ProgressBar progressProfile;

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

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(rootView);
        getProfileData();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews(View v) {
        profileNameView = v.findViewById(R.id.profileNameView);
        profileMobileView = v.findViewById(R.id.profileMobileView);
        profileEmailView = v.findViewById(R.id.profileEmailView);
//        profilePasswordView = v.findViewById(R.id.profilePasswordView);
        progressProfile = v.findViewById(R.id.progressProfile);
        logoutButton = v.findViewById(R.id.logoutButton);
        MaterialButton updateButton = v.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        settings = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);

    }

    private void updateProfileData() {

        JSONObject rawJson = new JSONObject();

        if (!TextUtils.isEmpty(profileNameView.getText()) && profileNameView.getText() != null) {

            if (!TextUtils.isEmpty(profileMobileView.getText()) && profileMobileView.getText() != null) {

                if (!TextUtils.isEmpty(profileEmailView.getText()) && profileEmailView.getText() != null) {


                    try {
                        rawJson.put("name", profileNameView.getText().toString());
                        rawJson.put("mobile", profileMobileView.getText().toString());
                        rawJson.put("email", profileEmailView.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rawJson.toString());


                    WebService.createApiService().updateCustomerProfile("Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, ""), body).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                            Log.d("RESPONSE", "onResponse: " + response);

                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    showToast(response.body());
                                }
                                progressProfile.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            showToast("" + t.getMessage());
                        }
                    });


                }
            }
        }


    }

    private void getProfileData() {

        WebService.createApiService().getCustomerProfile("Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, "")).enqueue(new Callback<ProfileItem>() {
            @Override
            public void onResponse(@NonNull Call<ProfileItem> call, @NonNull Response<ProfileItem> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        profileNameView.setText(response.body().getCustomerName());
                        profileMobileView.setText(response.body().getCustomerMobile());
                        profileEmailView.setText(response.body().getCustomerEmail());
//                        profilePasswordView.setText("");
                        if (!TextUtils.isEmpty(profileNameView.getText()) && profileNameView.getText() != null) {
                            profileNameView.setSelection(profileNameView.getText().length());
                        }

                    } else {
                        showToast("Error retrieving profile data.");
                    }

                    progressProfile.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileItem> call, @NonNull Throwable t) {
                showToast("" + t.getMessage());
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateButton: {
                progressProfile.setVisibility(View.VISIBLE);
                updateProfileData();
                break;
            }
            case R.id.logoutButton: {
                showExitDialog();
                break;
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                settings.edit().clear().apply();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

}

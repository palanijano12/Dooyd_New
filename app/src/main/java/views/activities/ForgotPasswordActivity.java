package views.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.android.dooyd.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    String fromPage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (getIntent() != null && getIntent().hasExtra("FROM_PAGE"))
            fromPage = getIntent().getStringExtra("FROM_PAGE");
    }
}

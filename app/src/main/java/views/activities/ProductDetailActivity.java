package views.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dooyd.R;
import datamodel.Constants;
import views.adapter.ProductRecyclerAdapter;
import views.fragments.ProductDescriptionFragment;
import views.fragments.ProductFragment;
import views.listener.ProductRecyclerListener;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        initViews();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null) {
            Bundle arguments = new Bundle();
            arguments.putInt(Constants.KEY_POSITION, getIntent().getIntExtra(Constants.KEY_POSITION, 0));
            arguments.putString(Constants.KEY_PRODUCT_ID,  getIntent().getStringExtra(Constants.KEY_PRODUCT_ID));

            Fragment fragment = new ProductDescriptionFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().replace(R.id.product_frame_container, fragment).commit();
        }



    }

    private void initViews() {
        // mainRecyclerView = findViewById(R.id.mainRecyclerView);
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
    protected void onDestroy() {
        super.onDestroy();

    }

//
//    @NonNull
//    @Override
//    public Lifecycle getLifecycle() {
//        return mLifecycleRegistry;
//    }
}

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
import views.fragments.ProductFragment;
import views.listener.ProductRecyclerListener;

public class ProductActivity extends AppCompatActivity implements LifecycleOwner, LifecycleObserver {

    public static int INTENT_LOGIN = 110;
    private RecyclerView mainRecyclerView;
    private ProductRecyclerAdapter mainRecyclerAdapter;
    private LifecycleRegistry mLifecycleRegistry;
    private ProductRecyclerListener productRecyclerListener;


    private int itemPosition;
    private final String[] apiUrls =
            {
                    "Product/GetProcurementProducts/1",
                    "Product/GetDesignEngineeringProducts//1",
                    "Product/GetConstructionProducts/1",
                    "Product/GetRentalProducts/1"
            };

    private String categoryName;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        initViews();
        getLifecycle().addObserver(this);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null) {
            categoryName = getIntent().getStringExtra(Constants.KEY_CATEGORY_NAME);
            position = getIntent().getIntExtra(Constants.KEY_POSITION, 0);
        }

        Bundle arguments = new Bundle();
        arguments.putInt(Constants.KEY_POSITION, position);
        arguments.putString(Constants.KEY_CATEGORY_NAME, categoryName);

        Fragment fragment = new ProductFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction().replace(R.id.product_frame_container, fragment).addToBackStack(null).commit();


//        if (getIntent() != null) {
//            setTitle(getIntent().getStringExtra("CATEGORY_NAME"));
//            itemPosition = getIntent().getIntExtra("POSITION", 0);
//        }
//
//        mainRecyclerView.setHasFixedSize(true);
//        mainRecyclerView.setLayoutManager(new LinearLayoutManager(ProductActivity.this));


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
    protected void onStart() {
        super.onStart();
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);

//        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
//
//        mainViewModel.getMainItems(apiUrls[itemPosition]).observe(this, new Observer<List<MainItem>>() {
//            @Override
//            public void onChanged(List<MainItem> mainItems) {
//                mainRecyclerAdapter = new ProductRecyclerAdapter(ProductActivity.this, mainItems, ProductActivity.this);
//                mainRecyclerAdapter.setHasStableIds(true);
//                mainRecyclerView.setAdapter(mainRecyclerAdapter);
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Fragment fragment;

        if (getSupportFragmentManager().getFragments().size() > 0) {

            fragment = getSupportFragmentManager().getFragments().get(0);
            if (fragment instanceof ProductFragment) {
                super.onBackPressed();
                //getSupportFragmentManager().getFragments().remove(fragment);
                finish();
            } else {
                super.onBackPressed();
            }

        } else {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

//
//    @NonNull
//    @Override
//    public Lifecycle getLifecycle() {
//        return mLifecycleRegistry;
//    }
}

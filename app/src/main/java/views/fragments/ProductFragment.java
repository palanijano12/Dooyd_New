package views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.*;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dooyd.R;
import com.google.android.material.button.MaterialButton;
import datamodel.Constants;
import datamodel.MainItem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import viewmodel.MainViewModel;
import views.activities.LoginActivity;
import views.activities.MainActivity;
import views.activities.ProductActivity;
import views.activities.ProductDetailActivity;
import views.adapter.ProductRecyclerAdapter;
import views.listener.ProductRecyclerListener;
import webservices.WebService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ProductFragment extends Fragment implements LifecycleOwner, ProductRecyclerListener, View.OnClickListener {

    private final String[] apiUrls =
            {
                    "Product/GetProcurementProducts/1",
                    "Product/GetDesignEngineeringProducts//1",
                    "Product/GetConstructionProducts/1",
                    "Product/GetRentalProducts/1"
            };
    List<MainItem> mainItemsMain = new ArrayList<>();
    ProductActivity productActivity;
    SharedPreferences settings;
    MaterialButton viewCartButton;
    private RecyclerView mainRecyclerView;
    private ProductRecyclerAdapter mainRecyclerAdapter;
    private LifecycleRegistry mLifecycleRegistry;
    private ProgressBar productProgress;
    private int itemPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(rootView);

        if (getArguments() != null) {
            if (getActivity() != null) {

                ActionBar actionBar = ((ProductActivity) getActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayShowTitleEnabled(true);
                }


                getActivity().setTitle("" + getArguments().getString(Constants.KEY_CATEGORY_NAME, ""));
                itemPosition = getArguments().getInt(Constants.KEY_POSITION, 0);
            }

        }


        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        mLifecycleRegistry = new LifecycleRegistry(this);

        mainRecyclerAdapter = new ProductRecyclerAdapter(getActivity().getApplicationContext(), mainItemsMain, ProductFragment.this, itemPosition);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ProductActivity)
            productActivity = (ProductActivity) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        mainViewModel.getMainItems(apiUrls[itemPosition]).observe(this, new Observer<List<MainItem>>() {
            @Override
            public void onChanged(List<MainItem> mainItems) {

                if (getActivity() != null) {

                    for(int i =0 ;i < mainItems.size(); i++)
                    {
                        Log.e("mainItems" ,"mainItems " + mainItems.get(i).getItemImageUrl());
                    }

                    productProgress.setVisibility(View.GONE);
                    mainItemsMain.clear();
                    mainItemsMain.addAll(mainItems);

                    if (mainRecyclerAdapter != null)
                        mainRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    private void initViews(View v) {
        mainRecyclerView = v.findViewById(R.id.mainRecyclerView_);
        productProgress = v.findViewById(R.id.productLoader);
        viewCartButton = v.findViewById(R.id.viewCartButton);
        viewCartButton.setOnClickListener(this);

        settings = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        settings.edit().putString(Constants.SHARED_KEY_ADD_CART, "").apply();


    }

    @Override
    public void onResume() {
        super.onResume();
        showViewCartBtn();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);

    }

    @Override
    public void onItemClick(int position, String productId, int viewId) {
        if (getActivity() != null) {
            switch (viewId) {
                case R.id.productCard: {
//                    Bundle arguments = new Bundle();
//                    arguments.putInt(Constants.KEY_POSITION, itemPosition);
//                    arguments.putString(Constants.KEY_PRODUCT_ID, productId);
//
//                    Log.d("productId", "productId " + productId + " " + itemPosition);
//
//                    Fragment fragment = new ProductDescriptionFragment();
//                    fragment.setArguments(arguments);
//
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.product_frame_container, fragment).addToBackStack(null).commit();

                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                    intent.putExtra(Constants.KEY_PRODUCT_ID, productId);
                    intent.putExtra(Constants.KEY_POSITION,itemPosition);
                    startActivity(intent);

                    break;
                }

                case R.id.item_add_cart: {
                    addItemToCart(productId);
                    break;
                }
            }
        }

    }


    private void addItemToCart(String productId) {
        // READ VALUE
        String loggedValue = settings.getString(Constants.SHARED_KEY_LOGGED_VALUE, "");
        if (loggedValue.equals("1")) {
            callAddToCartAPI(productId);
        } else {
            settings.edit().putString(Constants.SHARED_KEY_ADD_CART, productId).apply();
            Intent intent = new Intent(productActivity, LoginActivity.class);
            intent.putExtra("FROM_PAGE", "PRODUCT");
            startActivityForResult(intent, ProductActivity.INTENT_LOGIN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "onActivityResult");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ProductActivity.INTENT_LOGIN) {
                if (!settings.getString(Constants.SHARED_KEY_ADD_CART, "").isEmpty()) {
                    callAddToCartAPI(settings.getString(Constants.SHARED_KEY_ADD_CART, ""));
                    settings.edit().putString(Constants.SHARED_KEY_ADD_CART, "").apply();
                }
            }
        }
    }

    private void callAddToCartAPI(String productId) {
        ///
        productProgress.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = WebService.createApiService().addCart(productId, "Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                productProgress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    viewCartButton.setVisibility(View.VISIBLE);
                    try {
                        showToast(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    settings.edit().clear().apply();

                    Intent intent = new Intent(productActivity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    productActivity.startActivity(intent);
                    productActivity.finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                productProgress.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.viewCartButton: {

                Bundle arguments = new Bundle();
                arguments.putBoolean("HEADER", false);

                Fragment fragment = new CartFragment();
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.product_frame_container, fragment).addToBackStack(null).commit();
                break;
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(productActivity, message, Toast.LENGTH_SHORT).show();
    }

    private void showViewCartBtn() {
        Call<List<MainItem>> call = WebService.createApiService().getCart("Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, ""));
        call.enqueue(new Callback<List<MainItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<MainItem>> call, @NonNull Response<List<MainItem>> response) {
                Log.e("onResponse", "onResponse " + response.toString());
                if (response.isSuccessful()) {

                    if (response.body().size() > 0) {
                        viewCartButton.setVisibility(View.VISIBLE);
                    } else {
                        viewCartButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MainItem>> call, @NonNull Throwable t) {
                productProgress.setVisibility(View.GONE);
            }
        });
    }
}

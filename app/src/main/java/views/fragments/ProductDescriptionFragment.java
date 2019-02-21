package views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.android.dooyd.R;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import datamodel.Constants;
import datamodel.DescItem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import views.activities.LoginActivity;
import views.activities.MainActivity;
import views.activities.ProductActivity;
import views.activities.ProductDetailActivity;
import webservices.WebService;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class ProductDescriptionFragment extends Fragment implements LifecycleOwner, View.OnClickListener {

    private LifecycleRegistry mLifecycleRegistry;
    private String productId;
    int itemposition = 0;

    private AppCompatTextView descProductTitle;
    private AppCompatTextView descProductPrice;
    private AppCompatTextView descProductCutPrice;
    private AppCompatTextView descProductAvail;
    private AppCompatTextView descProductCategory;
    private AppCompatTextView descProductDesc;

    private AppCompatImageView descProductImageView;

    private ProgressBar descProgressBar;
    DescItem descItem = new DescItem();
    SharedPreferences settings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        String apiUrl = "Product/GetProductDetail/";

        View rootView = inflater.inflate(R.layout.fragment_product_description, container, false);

        if (getActivity() != null) {

            getActivity().setTitle("");

            ActionBar actionBar = ((ProductDetailActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(true);
            }
            if (getArguments() != null) {
                productId = getArguments().getString(Constants.KEY_PRODUCT_ID, "");
                itemposition = getArguments().getInt(Constants.KEY_POSITION, 0);
            }

        }

        initViews(rootView);

        mLifecycleRegistry = new LifecycleRegistry(this);

        loadProductDescription(apiUrl + productId);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);


    }

    private void loadProductDescription(String url) {

        Call<DescItem> call = WebService.createApiService().getProductDescription(url);

        call.enqueue(new Callback<DescItem>() {
            @Override
            public void onResponse(@NonNull Call<DescItem> call, @NonNull Response<DescItem> response) {
                Log.e("onResponse", response.toString());
                if (response.body() != null) {
                    if (getActivity() != null) {
                        getActivity().setTitle(response.body().getName());
                    }
                    descItem = response.body();
                    descProductTitle.setText(response.body().getName());
                    descProductPrice.append("" + response.body().getPrice());
                    descProductCutPrice.append("" + response.body().getCutPrice());
                    if(itemposition == 1)
                    {
                        descProductAvail.setText(Constants.DEMO);
                        descProductAvail.setTextColor(Color.parseColor("#12CE19"));
                    }
                    else {
                        if (response.body().getIsAvailable()) {
                            descProductAvail.setText(Constants.IN_STOCK);
                            descProductAvail.setTextColor(Color.parseColor("#12CE19"));
                        } else {
                            descProductAvail.setText(Constants.OUT_OF_STOCK);
                            descProductAvail.setTextColor(Color.RED);
                        }
                    }
                    descProductCategory.setText(response.body().getCategory());
                    descProductDesc.setText(response.body().getShortDescription());
                    if (getActivity() != null) {
                        Glide.with(getActivity().getApplicationContext()).load(response.body().getProductImage().get(0).getFileUrl()).into(descProductImageView);

                    }

                    descProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<DescItem> call, @NonNull Throwable t) {
                Log.e("onResponse", t.toString());
                descProgressBar.setVisibility(View.GONE);
            }
        });
    }


    private void initViews(View v) {
        settings = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        descProductImageView = v.findViewById(R.id.descProductImageView);
        descProductTitle = v.findViewById(R.id.descProductTitleView);
        descProductPrice = v.findViewById(R.id.descProductPriceView);
        descProductCutPrice = v.findViewById(R.id.descProductCutPriceView);
        descProductAvail = v.findViewById(R.id.descProductAvailView);
        descProductCategory = v.findViewById(R.id.descProductCategoryView);
        descProductDesc = v.findViewById(R.id.descProductCategoryDescView);
        descProgressBar = v.findViewById(R.id.productDescLoader);

        MaterialButton btnAddToCart = v.findViewById(R.id.descAddCartButton);
        //MaterialButton btnAddWhislist = v.findViewById(R.id.descAddWhislistButton);
        MaterialButton btnChatVendor = v.findViewById(R.id.descChatVendorButton);
        btnAddToCart.setOnClickListener(this);
        // btnAddWhislist.setOnClickListener(this);
        btnChatVendor.setOnClickListener(this);

        if(itemposition == 1)
        {
            btnAddToCart.setVisibility(View.GONE);
            btnChatVendor.setVisibility(View.GONE);
        }
        else
        {
            btnAddToCart.setVisibility(View.VISIBLE);
            btnChatVendor.setVisibility(View.VISIBLE);
        }

        descProductCutPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);

    }

    @Override
    public void onClick(View v) {
        addItemToCart(productId);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void addItemToCart(String productId) {
        // READ VALUE
        String loggedValue = settings.getString(Constants.SHARED_KEY_LOGGED_VALUE, "");
        if (loggedValue.equals("1")) {
            callAddToCartAPI(productId);
        } else {
            settings.edit().putString(Constants.SHARED_KEY_ADD_CART, productId).apply();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
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
        descProgressBar.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = WebService.createApiService().addCart(productId, "Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                descProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    try {
                        showToast(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    settings.edit().clear().apply();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                descProgressBar.setVisibility(View.GONE);
            }
        });
    }

}
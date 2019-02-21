package views.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
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
import views.activities.ProductActivity;
import views.activities.ProductDetailActivity;
import views.adapter.CartAdapter;
import views.listener.CommonRecyclerListener;
import webservices.WebService;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CartFragment extends Fragment implements CommonRecyclerListener, View.OnClickListener {

    List<MainItem> mainItemsMain = new ArrayList<>();
    SharedPreferences settings;
    MaterialButton doneButton;
    private RecyclerView mainRecyclerView;
    private CartAdapter mainRecyclerAdapter;
    private ProgressBar productProgress;
    TextView tv_total;
    TextView tv_header;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        initViews(rootView);

        if (getActivity() != null && getActivity() instanceof ProductActivity) {

            ActionBar actionBar = ((ProductActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(true);
            }

            getActivity().setTitle("Cart");
        }

        if(getArguments() != null)
        {
            if(getArguments().getBoolean("HEADER", false))
                tv_header.setVisibility(View.VISIBLE);
        }


        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        mainRecyclerAdapter = new CartAdapter(getActivity().getApplicationContext(), mainItemsMain, CartFragment.this);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);

        showViewCartBtn();

        return rootView;
    }

    private void initViews(View v) {
        tv_header = v.findViewById(R.id.tv_header);
        mainRecyclerView = v.findViewById(R.id.mainRecyclerView_);
        productProgress = v.findViewById(R.id.productLoader);
        tv_total = v.findViewById(R.id.tv_total);
        doneButton = v.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(this);

        settings = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        settings.edit().putString(Constants.SHARED_KEY_ADD_CART, "").apply();


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void showViewCartBtn() {
        productProgress.setVisibility(View.VISIBLE);
        Call<List<MainItem>> call = WebService.createApiService().getCart("Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, ""));
        call.enqueue(new Callback<List<MainItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<MainItem>> call, @NonNull Response<List<MainItem>> response) {
                productProgress.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    mainItemsMain.clear();
                    assert response.body() != null;
                    mainItemsMain.addAll(response.body());

                    if (mainItemsMain.size() > 0)
                        doneButton.setVisibility(View.VISIBLE);

                    if (mainRecyclerAdapter != null)
                        mainRecyclerAdapter.notifyDataSetChanged();

                    calculateTotal();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MainItem>> call, @NonNull Throwable t) {
                productProgress.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onItemClick(int viewId, int position) {
        switch (viewId) {

            case R.id.productCard: {
//                Bundle arguments = new Bundle();
//                arguments.putInt(Constants.KEY_POSITION, 0);
//                arguments.putString(Constants.KEY_PRODUCT_ID, mainItemsMain.get(position).getProductid());
//
//                Fragment fragment = new ProductDescriptionFragment();
//                fragment.setArguments(arguments);
//
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.product_frame_container, fragment).addToBackStack(null).commit();

                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT_ID, mainItemsMain.get(position).getProductid());
                intent.putExtra(Constants.KEY_POSITION,0);
                startActivity(intent);

                break;
            }

            case R.id.tv_remove: {
                callDelete(mainItemsMain.get(position).getItemId(), position);
                break;
            }

            case R.id.tv_add: {
                callIncrease(mainItemsMain.get(position).getItemId(), position);
                break;
            }

            case R.id.tv_minus: {
                callDecrease(mainItemsMain.get(position).getItemId(), position);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void callDelete(String productId, final int position) {
        productProgress.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = WebService.createApiService().deleteCart(productId, "Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                productProgress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    mainItemsMain.remove(position);

                    if (mainItemsMain.size() <= 0)
                        doneButton.setVisibility(View.GONE);

                    if (mainRecyclerAdapter != null)
                        mainRecyclerAdapter.notifyDataSetChanged();

                    calculateTotal();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                productProgress.setVisibility(View.GONE);
            }
        });
    }

    private void callIncrease(String productId, final int position) {
        productProgress.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = WebService.createApiService().increaseCart(productId, "Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                productProgress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    int quan = mainItemsMain.get(position).getQuantity();

                    if (quan < mainItemsMain.get(position).getProductquantity())
                        mainItemsMain.get(position).setQuantity(quan + 1);

                    if (mainRecyclerAdapter != null)
                        mainRecyclerAdapter.notifyDataSetChanged();

                    calculateTotal();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                productProgress.setVisibility(View.GONE);
            }
        });
    }

    private void callDecrease(String productId, final int position) {
        productProgress.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = WebService.createApiService().decreaseCart(productId, "Bearer " + settings.getString(Constants.SHARED_KEY_USER_TOKEN, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                productProgress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    int quan = mainItemsMain.get(position).getQuantity();

                    if (quan > 0)
                        mainItemsMain.get(position).setQuantity(quan - 1);

                    if (mainRecyclerAdapter != null)
                        mainRecyclerAdapter.notifyDataSetChanged();

                    calculateTotal();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                productProgress.setVisibility(View.GONE);
            }
        });
    }

    private void calculateTotal() {
        float total = 0;
        for (int i = 0; i < mainItemsMain.size(); i++) {
            total = total + (mainItemsMain.get(i).getQuantity() * mainItemsMain.get(i).getItemPrice());
        }

        try {
            tv_total.setText(getString(R.string.rupee_value) + " " + String.valueOf(total));
        }
        catch (Exception e)
        {
            tv_total.setText(getString(R.string.rupee_value) + " " + String.format("%.2f", total));
        }

    }
}

package webservices;

import datamodel.DescItem;
import datamodel.MainItem;
import datamodel.ProfileItem;
import datamodel.SlideItem;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    @POST
    Call<List<MainItem>> getItems(@Url String url);

    @GET("Slider/GetCustomerSlider")
    Call<List<SlideItem>> getSlideImages();

    @POST("Customer/InsertCustomerProfile")
    Call<String> addCustomerProfile(@Body RequestBody rawData);

    @POST("Login/ValidateLogin")
    Call<String> validateCustomerLogin(@Body RequestBody rawData);

    @GET("Customer/GetCustomerProfile")
    Call<ProfileItem> getCustomerProfile(@Header("Authorization") String value);

    @POST("Customer/UpdateCustomerProfile")
    Call<String> updateCustomerProfile(@Header("Authorization") String value,@Body RequestBody rawData);

    @GET
    Call<DescItem> getProductDescription(@Url String url);

    @GET("Cart/GetCart")
    Call<List<MainItem>> getCart(@Header("Authorization") String value);

    @POST("Cart/InsertCart/{productId}")
    Call<ResponseBody> addCart(@Path("productId") String productId, @Header("Authorization") String value);

    @POST("Cart/IncreaseCartQuantity/{productId}")
    Call<ResponseBody> increaseCart(@Path("productId") String productId, @Header("Authorization") String value);

    @POST("Cart/DecreaseQuantity/{productId}")
    Call<ResponseBody> decreaseCart(@Path("productId") String productId, @Header("Authorization") String value);

    @POST("Cart/DeleteCart/{productId}")
    Call<ResponseBody> deleteCart(@Path("productId") String productId, @Header("Authorization") String value);
}

package viewmodel;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import datamodel.MainItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import webservices.WebService;

import java.util.List;

public class MainViewModel extends ViewModel {

    private MutableLiveData<List<MainItem>> mainLiveItems;

    public LiveData<List<MainItem>> getMainItems(String url) {

        if (mainLiveItems == null) {
            mainLiveItems = new MutableLiveData<>();
            loadMainItems(url);
        }

        return mainLiveItems;
    }

    private void loadMainItems(String url) {

        Call<List<MainItem>> call = WebService.createApiService().getItems(url);


        call.enqueue(new Callback<List<MainItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<MainItem>> call, @NonNull Response<List<MainItem>> response) {
                mainLiveItems.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<MainItem>> call, @NonNull Throwable t) {
                Log.e("OnFailure", "onFailure: " + call + t.getMessage());
            }
        });
    }
}

package views.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import datamodel.Constants;
import views.activities.MainActivity;
import views.fragments.*;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    MainActivity mainActivity;
    SharedPreferences settings;
    private String[] tabTitles;

    public MainPagerAdapter(MainActivity mainActivity, @NonNull FragmentManager fm, String[] tabTitles) {
        super(fm);
        this.tabTitles = tabTitles;
        this.mainActivity = mainActivity;
        settings = mainActivity.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return HomeFragment.newInstance();
            }
            case 1: {
                Bundle arguments = new Bundle();
                arguments.putBoolean("HEADER", true);
                Fragment fragment = new CartFragment();
                fragment.setArguments(arguments);
                if (settings.getString(Constants.SHARED_KEY_USER_TOKEN, "").isEmpty())
                    return new LoginFragment();
                else
                    return fragment;
            }
            case 2: {

                if (settings.getString(Constants.SHARED_KEY_USER_TOKEN, "").isEmpty())
                    return new LoginFragment();
                else
                    return new OrderFragment();
            }
            case 3: {
                Bundle arguments = new Bundle();
                arguments.putBoolean("HEADER", true);
                Fragment fragment = new ProfileFragment();
                fragment.setArguments(arguments);
                if (settings.getString(Constants.SHARED_KEY_USER_TOKEN, "").isEmpty())
                    return new LoginFragment();
                else
                    return fragment;
            }
            default: {
                return new CartFragment();
            }
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 4;
    }
}

package views.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.android.dooyd.R;
import com.google.android.material.tabs.TabLayout;
import datamodel.Constants;
import datamodel.SlideItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import views.activities.MainActivity;
import views.activities.ProductActivity;
import views.adapter.HomeRecyclerAdapter;
import views.adapter.SliderPagerAdapter;
import views.listener.HomeRecyclerListener;
import webservices.WebService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements HomeRecyclerListener {

    private RecyclerView homeRecyclerView;
    private HomeRecyclerAdapter homeRecyclerAdapter;

    private SliderPagerAdapter sliderPagerAdapter;
    private ViewPager slideViewPager;
    private TabLayout indicator;
    private AppCompatImageView slideViewHolder;
    ProgressBar productLoader;

    private int oldDragPosition = 0;
    private Animator pagerAnimation;

    private ObjectAnimator slideAnimation;

    private int currentPage = 0;
    private Timer timer;
    final private long DELAY = 500;
    final private long PERIOD = 3000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(rootView);
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.app_name));
        }

//        SharedPreferences settings = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
//        // WRITING VALUE
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(Constants.SHARED_KEY_USER_TOKEN, "");
//        editor.putString(Constants.SHARED_KEY_LOGGED_VALUE, "");
//        editor.apply();

        List<String> categoryList = new ArrayList<>();
        categoryList.add("PROCUREMENTS");
        categoryList.add("DESIGN \n&\n ENGINEERING");
        categoryList.add("CONSTRUCTION");
        categoryList.add("RENTAL EQUIPMENTS");

        getSlideImages();

        homeRecyclerAdapter = new HomeRecyclerAdapter(getActivity(), categoryList, this);
        homeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        homeRecyclerView.setHasFixedSize(true);

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeRecyclerView.setAdapter(homeRecyclerAdapter);
    }

    private void initViews(View v) {

        productLoader = v.findViewById(R.id.productLoader);
        slideViewPager = v.findViewById(R.id.slideViewPager);
        slideViewHolder = v.findViewById(R.id.slideViewHolder);
        homeRecyclerView = v.findViewById(R.id.homeRecyclerView);
        indicator = v.findViewById(R.id.indicator);
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onItemClick(int position, String categoryName) {
        if (getActivity() != null) {

//            Bundle arguments = new Bundle();
//            arguments.putInt("POSITION", position);
//            arguments.putString("CATEGORY_NAME", categoryName);
//
//            Fragment fragment = new ProductFragment();
//            fragment.setArguments(arguments);

            if (getActivity() != null) {
                getActivity().startActivity(new Intent(getActivity(), ProductActivity.class).putExtra(Constants.KEY_POSITION, position).putExtra(Constants.KEY_CATEGORY_NAME, categoryName));
            }

            // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        }

    }

    private void getSlideImages() {
        productLoader.setVisibility(View.VISIBLE);
        Call<List<SlideItem>> call = WebService.createApiService().getSlideImages();
        call.enqueue(new Callback<List<SlideItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<SlideItem>> call, @NonNull Response<List<SlideItem>> response) {
                productLoader.setVisibility(View.GONE);
                sliderPagerAdapter = new SliderPagerAdapter(getActivity(), response.body());
                slideViewPager.setAdapter(sliderPagerAdapter);
                indicator.setupWithViewPager(slideViewPager, true);
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
//                slideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                    @Override
//                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                    }
//
//                    @Override
//                    public void onPageSelected(int position) {
//
//                    }
//
//                    @Override
//                    public void onPageScrollStateChanged(int state) {
//
//                        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
//                            slideViewPager.setX(0);
//                            slideAnimation.cancel();
//                        }
//
//                    }
//                });
//
//                if (getActivity() != null && response.body() != null) {
//                    Glide.with(getActivity()).load(response.body().get(0).getItemImageUrl()).into(slideViewHolder);
//
//                }
//
//                slideViewPager.setX(slideViewPager.getMeasuredWidth());
//                slideViewPager.setCurrentItem(1);
//
//                slideAnimation = ObjectAnimator.ofFloat(slideViewPager, "X", slideViewPager.getMeasuredWidth(), 0);
//                slideAnimation.setDuration(1200);
//                slideAnimation.setStartDelay(200);
//                slideAnimation.setRepeatMode(ValueAnimator.RESTART);
//                slideAnimation.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        slideViewPager.setCurrentItem(slideItemValue);
//                        slideItemValue++;
//                        if (slideItemValue > 2) {
//                            slideItemValue = 0;
//                        }
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//
//                        slideViewPager.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                slideAnimation.start();
//                            }
//                        }, 300);
//
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//                        //  slideViewPager.setCurrentItem(1);
//
//
//                    }
//                });
//                slideAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        slideViewPager.setX(animation.getAnimatedFraction());
//                    }
//                });
//                slideAnimation.start();


               /* if (slideViewPager.getAdapter() != null) {
                    slideViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animatePagerTransition();
                        }
                    }, Long.parseLong(Constants.SLIDER_ANIMATION_DURATION));
                }*/

               /* final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == sliderPagerAdapter.getCount()-1) {
                            currentPage = 0;
                        }
                        slideViewPager.setCurrentItem(currentPage);
                        currentPage++;
                    }
                };

                timer = new Timer(); // This will create a new Thread
                timer.schedule(new TimerTask() { // task to be scheduled
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, DELAY, PERIOD);*/


            }

            @Override
            public void onFailure(@NonNull Call<List<SlideItem>> call, @NonNull Throwable t) {
                productLoader.setVisibility(View.GONE);
            }
        });
    }


    private void animatePagerTransition() {
        if (pagerAnimation != null) {
            pagerAnimation.cancel();
        }
        pagerAnimation = getPagerTransitionAnimation();
        if (slideViewPager.beginFakeDrag()) {
            pagerAnimation.start();
        }
    }


    private Animator getPagerTransitionAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(0, (slideViewPager.getMeasuredWidth() - 1));
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                slideViewPager.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                slideViewPager.endFakeDrag();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                slideViewPager.endFakeDrag();
                oldDragPosition = 0;
                slideViewPager.beginFakeDrag();
            }
        });

        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dragPosition = (Integer) animation.getAnimatedValue();
                int dragOffset = dragPosition - oldDragPosition;
                oldDragPosition = dragPosition;
                slideViewPager.fakeDragBy(-dragOffset);
            }
        });

        animator.setDuration(Long.parseLong(Constants.SLIDER_ANIMATION_DURATION));
        animator.setRepeatCount(2);

        return animator;
    }


    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            if (getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (slideViewPager.getCurrentItem() < sliderPagerAdapter.getCount() - 1) {
                            slideViewPager.setCurrentItem(slideViewPager.getCurrentItem() + 1);
                        } else {
                            slideViewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        }
    }


}

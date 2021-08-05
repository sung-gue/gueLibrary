package com.breakout.sample.component.navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.breakout.sample.R;
import com.breakout.sample.databinding.NavigationA1Binding;
import com.breakout.sample.databinding.NavigationA2Binding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * https://developer.android.com/guide/navigation/navigation-getting-started
 */
@SuppressLint("NonConstantResourceId")
public class NavigationActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    static String TITLE = "title";

    private NavController _navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomNavigationView navView;

        int layoutId = getIntent().getIntExtra("layoutId", R.layout.navigation_a1);
        int initNum = getIntent().getIntExtra("initNum", 3);
        switch (layoutId) {
            default:
            /* initNum : 1~5 가능
                <fragment
                    android:id="@+id/navHostFragment"
                    android:name="androidx.navigation.fragment.NavHostFragment"
             */
            case R.layout.navigation_a1: {
                NavigationA1Binding binding = DataBindingUtil.setContentView(this, layoutId);
                navView = binding.navView;
                break;
            }
            /*  initNum : Navigation.findNavController() 사용한 1,2번 실패
                    https://developer.android.com/guide/navigation/navigation-getting-started#navigate
                <androidx.fragment.app.FragmentContainerView
                    android:id="@+id/navHostFragment"
                    android:name="androidx.navigation.fragment.NavHostFragment"
             */
            case R.layout.navigation_a2: {
                NavigationA2Binding binding = DataBindingUtil.setContentView(this, layoutId);
                navView = binding.navView;
                break;
            }
        }
        switch (initNum) {
            default:
            case 1: {
                _navController = Navigation.findNavController(this, R.id.navHostFragment);
                break;
            }
            case 2: {
                View navHostFragment = findViewById(R.id.navHostFragment);
                _navController = Navigation.findNavController(navHostFragment);
                break;
            }
            case 3: {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
                _navController = NavHostFragment.findNavController(fragment);
                break;
            }
            case 4: {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
                _navController = navHostFragment.getNavController();
                break;
            }
            case 5:
                navControllerInit5();
                break;
        }
        navControllerSetting(_navController, navView);
    }

    private void navControllerInit3() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        _navController = NavHostFragment.findNavController(fragment);
    }

    private void navControllerInit3_1() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        _navController = navHostFragment.getNavController();
    }

    private void navControllerInit4() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        Fragment fragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        _navController = navHostFragment.getNavController();
    }

    private void navControllerInit5() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        NavHostFragment navHostFragment = (NavHostFragment) fragments.get(0);
        _navController = navHostFragment.getNavController();
    }

    private void actionBarInit() {
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_guide, R.id.nav_photo)
                .build();
        NavigationUI.setupActionBarWithNavController(this, _navController, appBarConfiguration);
    }

    private void navControllerSetting(NavController navController, BottomNavigationView navView) {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.d(TAG, "onDestinationChanged: " + destination.getLabel());
            }
        });
        navView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemReselected: " + item.getTitle());
                switch (item.getItemId()) {
                    case R.id.nav_home:
                    case R.id.nav_guide:
                    case R.id.nav_photo:
                    default:
                }
            }
        });
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String title = (String) item.getTitle();
                Log.d(TAG, "onNavigationItemSelected: " + title);
                Bundle args = new Bundle();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                    case R.id.nav_guide:
                    case R.id.nav_photo:
                    default:
                }
                args.putString(TITLE, title);
                _navController.navigate(item.getItemId(), args);
                return true;
            }
        });
    }

}
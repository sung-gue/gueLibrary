package com.breakout.sample.component.navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.breakout.sample.R;
import com.breakout.sample.databinding.NavigationFBinding;


public class NavigationFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private NavigationFBinding _binding;

    public NavigationFragment() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //View rootView = inflater.inflate(R.layout.f_guide, container, false);
        _binding = DataBindingUtil.inflate(inflater, R.layout.navigation_f, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        //controlBackPressed();
    }

    protected void initUI() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String title = getArguments().getString(NavigationActivity.TITLE, "home");
            _binding.tvTitle.setText(title);
        }
    }

    /*
        활성화하면 NavController에서 fragment back stack 이동 불가
     */
    private void controlBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.w(TAG, "onBackPressed");
            }
        });
    }

}
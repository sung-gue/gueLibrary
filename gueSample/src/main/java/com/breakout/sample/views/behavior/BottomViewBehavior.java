package com.breakout.sample.views.behavior;


import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.breakout.sample.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/*
    TODO: 2019-12-13 Need more behavior research
 */

/**
 * Custom behavior.
 *
 * @author sung-gue
 * @version 1.0 (2016.02.16)
 */
public class BottomViewBehavior extends CoordinatorLayout.Behavior<View> {
    private String TAG = getClass().getSimpleName();

    public BottomViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return dependency instanceof AppBarLayout;
        //return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
//        Log.d(TAG, String.format("onDependentViewChanged | %s, %s, %s, %s", child.getHeight(), dependency.getHeight(), dependency.getTop(), dependency.getTranslationY()));
        float appbarHeight = parent.getContext().getResources().getDimension(R.dimen.appbar_height);
        float childHeight = child.getHeight();
        if (child.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            childHeight += ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin;
        }
        if (child instanceof FloatingActionButton) {
            if (child.getVisibility() == View.VISIBLE && -dependency.getTop() >= appbarHeight) {
                ((FloatingActionButton) child).hide();
            } else if (child.getVisibility() == View.GONE && -dependency.getTop() < appbarHeight) {
                ((FloatingActionButton) child).show();
            }
        } else if (child.getVisibility() == View.VISIBLE) {
            float offset = -childHeight * (dependency.getTop() / appbarHeight);
            child.setTranslationY(offset);
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
//        Log.d(TAG, String.format("onStartNestedScroll | %s, %s", axes, type));
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
//        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    private int dyDirectionSum;

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
//        Log.d(TAG, String.format("onNestedPreScroll | %s, %s, %s", dx, dy, type));

        /*if (dy > 0 && dyDirectionSum < 0 || dy < 0 && dyDirectionSum > 0) {
            child.animate().cancel();
            dyDirectionSum = 0;
        }
        dyDirectionSum += dy;
        if (dyDirectionSum > child.getHeight()) {
            hideView(child);
        } else if (dyDirectionSum < -child.getHeight()) {
            showView(child);
        }*/
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
//        Log.d(TAG, String.format("onNestedScroll | %s, %s, %s, %s, %s", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type));

        /*if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.setVisibility(View.GONE);
            //hideView(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.setVisibility(View.VISIBLE);
            //showView(child);
        }*/

        /*if (dyConsumed > 0) {
            child.setVisibility(View.GONE);
//            hideView(child);
        } else if (dyConsumed < 0) {
            child.setVisibility(View.VISIBLE);
//            showView(child);
        }*/
    }


    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final long ANIMATION_DURATION = 200;
    private boolean isShowing;
    private boolean isHiding;

    private void hideView(final View view) {
        if (isHiding || view.getVisibility() != View.VISIBLE) {
            return;
        }

        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(ANIMATION_DURATION);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isHiding = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isHiding = false;
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // 취소되면 다시 보여줌
                isHiding = false;
                showView(view);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // no-op
            }
        });

        animator.start();
    }

    private void showView(final View view) {
        if (isShowing || view.getVisibility() == View.VISIBLE) {
            return;
        }
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(ANIMATION_DURATION);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isShowing = true;
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // 취소되면 다시 숨김
                isShowing = false;
                hideView(view);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // no-op
            }
        });

        animator.start();
    }


}
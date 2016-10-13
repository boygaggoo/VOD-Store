package com.cookingshow;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class BaseMainFragment extends Fragment{

    public static final String ARGUMENT_DATA_KEY = "menuItem";
    protected String pageName = null;

    public boolean isFirstMenu() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            return ((MainActivity)activity).isTheFirstMenu();
        }
        return false;
    }

    public boolean isLastMenu() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            return ((MainActivity)activity).isTheLastMenu();
        }
        return false;
    }

    protected void switchMainNavi(boolean isToLeft) {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity)activity).switchNavigation(isToLeft);
        }
    }

    protected void onAnimStart(boolean enter) {

    }

    protected void onAnimEnd(boolean enter) {

    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        if (nextAnim == 0) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        if (anim != null) {
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    onAnimEnd(enter);
                    setAnimEnd();
                }

                public void onAnimationRepeat(Animation animation) {

                }

                public void onAnimationStart(Animation animation) {
                    setAnimStart();
                    onAnimStart(enter);
                }
            });
        }
        return anim;
    }

    private void setAnimStart() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).animationStart();
        }
    }

    private void setAnimEnd() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).animationEnd();
        }
    }

    public void onDisplayForFragment() {

    }

    public void onHiddenForFragment() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        if (isHidden) {
            onHiddenForFragment();
        } else {
            onDisplayForFragment();
        }
    }

    public void onNaviFocusDown() {

    }

    protected boolean isHidden = false;

    protected OnFragmentActionListener mFragmentActionListener = null;

    public void setOnFragmentActionListener(OnFragmentActionListener listener) {
        this.mFragmentActionListener = listener;
    }

    public interface OnFragmentActionListener {
        public void onProgressChangeAction(int progress);
        public void onProgressMaxAction(int max);
        public void onFocusUpAction();
    }

    @Override
    public void onDetach() {
        super.onDetach();


    }

    public void onFlipperNextPage() {

    }

    public void onFlipperBeforePage() {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }



}

package com.cookingshow.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.cookingshow.R;

public class ItemCursorView extends View {
	private View mFocusView;
	private View mUnFocusView;
	private int[] mFocusLocation = new int[2];
	private int[] mLocation = new int[2];
	private Drawable mDrawableWhite;
	private Drawable mDrawableShadow;
	private float mScaleUp = 1.0f;
	private float mScaleDown = 1.1f;
	private Paint mPaint = new Paint();
	private Rect mRect = new Rect();
	private boolean mMirror = false;
    private long SCALE_UP_DURATION = 200;
    private long SCALE_DOWN_DURATION = 150;
    private long SCALE_UPDATE_DURATION = 2000;



	ObjectAnimator animScaleUp = ObjectAnimator.ofFloat(this, "ScaleUp",
			new float[] { 1.0F, 1.1F }).setDuration(SCALE_UP_DURATION);
	ObjectAnimator animScaleDown = ObjectAnimator.ofFloat(this, "ScaleDown",
			new float[] { 1.1F, 1.0F }).setDuration(SCALE_DOWN_DURATION);
	
	ObjectAnimator animShime = ObjectAnimator.ofFloat(this, "ScaleUp",
	        new float[] { 1.1F, 1.15F, 1.1F }).setDuration(150);

    ObjectAnimator animUpdate = ObjectAnimator.ofFloat(this, "Update",
            new float[] { 1.0F, 1.0F }).setDuration(SCALE_UPDATE_DURATION);
	        
	public ItemCursorView(Context context) {
		super(context);
		init(context);
	}
	
	public ItemCursorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	void init(Context context){
		mDrawableWhite = getResources().getDrawable(R.drawable.item_highlight);
		mDrawableShadow = getResources().getDrawable(R.drawable.ad_bg_focus);
		mPaint.setColor(0xff000000);
		animScaleUp.setInterpolator(new DecelerateInterpolator());
		animScaleDown.setInterpolator(new DecelerateInterpolator());
		animShime.setInterpolator(new AccelerateInterpolator());
	}

	@Override
    protected void onDraw(Canvas canvas) {
		drawCursorView(canvas,mFocusView,mScaleUp, true);
		//if(animScaleDown.isRunning())
        {
			//drawCursorView(canvas,mUnFocusView,mScaleDown, false);
		}
    }
	
	public void drawCursorView(Canvas canvas, View view, float scale, boolean focus){
    	if(view!=null){
	    	canvas.save();

			if (null == mLocation) {
				mLocation = new int[2];
			}
			if (null == mFocusLocation) {
				mFocusLocation = new int[2];
			}
            View animalView = view;
            if (view instanceof AppItemView ) {
                animalView = ((AppItemView)view).appIcon;
            } /*else if (view instanceof DownloadItemView) {
                animalView = ((DownloadItemView)view).appIcon;
            }*/
			getLocationInWindow(mLocation);
            animalView.getLocationInWindow(mFocusLocation);
			
			int width = animalView.getWidth();
			int height = animalView.getHeight();
//            Log.d("xxxx",  "focus location " + mFocusLocation[0] + "->" + mFocusLocation[1]);
            int left = (int) (mFocusLocation[0] - mLocation[0] - width * (scale - 1) / 2);
            int top = (int) (mFocusLocation[1] - mLocation[1] - height * (scale - 1) / 2);
            /*if (view instanceof AppItemView ) {
                canvas.translate(left + 36, top + 36);
            } else {
            }*/
            canvas.translate(left, top);
            canvas.scale(scale, scale);

            /*if (view instanceof AppItemView ) {
                width = ((AppItemView)view).appIcon.getWidth();
                height = ((AppItemView)view).appIcon.getHeight();
//                ((AppItemView)view).appIcon.getLocationInWindow(mFocusLocation);
            }*/
	    	
	    	if(focus){
                Rect padding = new Rect();
                mDrawableShadow.getPadding(padding);
                mDrawableShadow.setBounds(-padding.left, -padding.top, width + padding.right, height + padding.bottom);
                mDrawableShadow.setAlpha((int) (255 * (scale - 1) * 10));
                mDrawableShadow.draw(canvas);
//                mDrawableWhite.getPadding(padding);
//                mDrawableWhite.setBounds(-padding.left - 1, -padding.top - 1, width + padding.right + 1, height + padding.bottom + 1);
//                mDrawableWhite.setAlpha((int) (255 * (scale - 1) * 10));
//                mDrawableWhite.draw(canvas);

	    	}
            if (view instanceof AppItemView/* || view instanceof DownloadItemView*/) {
//                canvas.translate(-36, -36);
//                View view1 = ((AppItemView)view).iconLayout;
//                view1.draw(canvas);
            } else {
                animalView.draw(canvas);
            }

            /*if (view instanceof AppItemView && ((AppItemView) view).isNewApp) {
                Drawable drawable = getResources().getDrawable(R.drawable.icon_new);
                Rect padding1 = new Rect();
                drawable.getPadding(padding1);
                drawable.setBounds(100, -18, 98 + 100, 48 - 18);
                drawable.setAlpha((int) (255 * (scale - 1) * 10));
                drawable.draw(canvas);
            }*/
            canvas.restore();
    	}
	}
	
 
    public void setFocusView(View view){
    	if(mFocusView != view){
	    	mFocusView = view;	
	    	//mMirror = mirror;
	    	mScaleUp = 1.0f;
	    	//animScaleUp.setStartDelay(50);
//            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	    	animScaleUp.start();
            animUpdate.start();
			/*if(view instanceof MirrorItemView ){
				((MirrorItemView)view).setReflection(false);
				view.invalidate();
			}*/
    	}
    }
    
    public void showIndicator(){
        animShime.start(); 
    }
    
    public void setUnFocusView(View view){
    	mFocusView = null;
    	if(mUnFocusView != view){
    		mUnFocusView = view;
    		animScaleDown.start();
    	}
    	invalidate();
    }

    public void setUnFocusWithSameFocusView(View view) {
        if (mFocusView != null && mFocusView == view) {
            mFocusView = null;
            mUnFocusView = view;
            animScaleDown.start();
            invalidate();
        }
    }

    public void clearFocusView() {
        mFocusView = null;
        invalidate();
    }
    
    /**
     * 该方法不能被混淆
     * @param scale
     */
    public void setScaleUp(float scale){
    	mScaleUp = scale;
    	invalidate();
    }

    
    public void setScaleDown(float scale){
    	mScaleDown = scale;
    	invalidate();
    }

    public void setUpdate(float scale){
        invalidate();
    }

}

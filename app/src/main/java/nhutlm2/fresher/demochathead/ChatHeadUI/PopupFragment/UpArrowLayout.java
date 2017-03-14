package nhutlm2.fresher.demochathead.ChatHeadUI.PopupFragment;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import nhutlm2.fresher.demochathead.R;

/**
 * Created by cpu1-216-local on 07/03/2017.
 */

public class UpArrowLayout extends ViewGroup {
    private final Point pointTo = new Point(0, 0);
    private ImageView arrowView;
    private int arrowDrawable = R.drawable.chat_top_arrow;

    public UpArrowLayout(Context context) {
        super(context);
        init();
    }

    public UpArrowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UpArrowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (arrowView != null) {
            removeView(arrowView);
        }
        arrowView = createArrowView();
        addView(arrowView);
    }

    protected ImageView createArrowView() {
        Drawable drawable = getResources().getDrawable(arrowDrawable);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawable);
        return imageView;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        arrowView.measure(measureSpec, measureSpec);
        updatePointer();
    }



    public void pointTo(final int viewX, final int viewY) {
        pointTo.x = viewX;
        pointTo.y = viewY;
        if (getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
            updatePointer();
        }
        invalidate();

    }


    private void updatePointer() {
        int x = (int) (pointTo.x - arrowView.getMeasuredWidth() / 2);
        int y = pointTo.y;
        if (x != arrowView.getTranslationX()) {
            arrowView.setTranslationX(x);
        }
        if (y != arrowView.getTranslationY()) {
            arrowView.setTranslationY(y);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        arrowView.layout(left, top, left + arrowView.getMeasuredWidth(), top + arrowView.getMeasuredHeight());
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == arrowView) continue;
            child.layout(left, top + arrowView.getMeasuredHeight() + pointTo.y, right, bottom);
        }
    }
    protected LayoutParams generateDefaultLayoutParams() {
        return new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        addView(arrowView);
    }
}

package nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadContainer;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import nhutlm2.fresher.demochathead.ChatHeadArrangement.MinimizedArrangement;
import nhutlm2.fresher.demochathead.ChatHeadManager.ChatHeadManager;

/**
 * Created by luvikaser on 07/03/2017.
 */

public class HostFrameLayout extends FrameLayout{
    private final ChatHeadManager manager;

    public HostFrameLayout(Context context, ChatHeadManager manager) {
        super(context);
        this.manager = manager;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        manager.onMeasure(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        manager.onSizeChanged(w,h,oldw,oldh);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = super.dispatchKeyEvent(event);
        if(!handled) {
            if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                minimize();
                return true;
            }
        }
        return handled;
    }

    public void minimize() {
        if (!(manager.getActiveArrangement() instanceof MinimizedArrangement)) {
            manager.setArrangement(MinimizedArrangement.class, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}

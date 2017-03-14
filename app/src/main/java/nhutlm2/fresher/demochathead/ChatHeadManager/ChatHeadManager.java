package nhutlm2.fresher.demochathead.ChatHeadManager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.rebound.SpringConfigRegistry;
import com.facebook.rebound.SpringSystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHead;
import nhutlm2.fresher.demochathead.ChatHeadArrangement.ChatHeadArrangement;
import nhutlm2.fresher.demochathead.ChatHeadArrangement.MaximizedArrangement;
import nhutlm2.fresher.demochathead.ChatHeadArrangement.MinimizedArrangement;
import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadCloseButton;
import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadContainer.ChatHeadContainer;
import nhutlm2.fresher.demochathead.ChatHeadUI.PopupFragment.ChatHeadViewAdapter;
import nhutlm2.fresher.demochathead.R;
import nhutlm2.fresher.demochathead.Utils.ChatHeadConfig;
import nhutlm2.fresher.demochathead.Utils.ChatHeadDefaultConfig;
import nhutlm2.fresher.demochathead.ChatHeadUI.PopupFragment.UpArrowLayout;
import nhutlm2.fresher.demochathead.Utils.ChatHeadOverlayView;
import nhutlm2.fresher.demochathead.Utils.SpringConfigsHolder;

/**
 * Created by cpu1-216-local on 07/03/2017.
 */

public class ChatHeadManager<User extends Serializable> implements ChatHeadManagerListener<User>{
    private static final int OVERLAY_TRANSITION_DURATION = 200;
    private final Map<Class<? extends ChatHeadArrangement>, ChatHeadArrangement> arrangements = new HashMap<>(3);
    private final Context context;
    private final ChatHeadContainer chatHeadContainer;
    private List<ChatHead<User>> chatHeads;
    private int maxWidth;
    private int maxHeight;
    private ChatHeadCloseButton closeButton;
    private ChatHeadArrangement activeArrangement;
    private ChatHeadViewAdapter<User> viewAdapter;
    private ChatHeadOverlayView overlayView;
    private boolean overlayVisible;
    private ImageView closeButtonShadow;
    private SpringSystem springSystem;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private ChatHeadConfig config;
    private Bundle activeArrangementBundle;
    private ArrangementChangeRequest requestedArrangement;
    private DisplayMetrics displayMetrics;
    private UpArrowLayout arrowLayout;

    public ChatHeadManager(Context context, ChatHeadContainer chatHeadContainer) {
        this.context = context;
        this.chatHeadContainer = chatHeadContainer;
        this.displayMetrics = chatHeadContainer.getDisplayMetrics();
        init(context, new ChatHeadDefaultConfig(context));
    }

    public ChatHeadContainer getChatHeadContainer() {
        return chatHeadContainer;
    }

    @Override
    public DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    @Override
    public List<ChatHead<User>> getChatHeads() {
        return chatHeads;
    }

    @Override
    public void setViewAdapter(ChatHeadViewAdapter chatHeadViewAdapter) {
        this.viewAdapter = chatHeadViewAdapter;
    }

    @Override
    public ChatHeadCloseButton getCloseButton() {
        return closeButton;
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }

    @Override
    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    public Context getContext() {
        return context;
    }


    @Override
    public ChatHeadArrangement getActiveArrangement() {
        if (activeArrangement != null) {
            return activeArrangement;
        }
        return null;
    }

    @Override
    public void selectChatHead(ChatHead chatHead) {
        if (activeArrangement != null)
            activeArrangement.selectChatHead(chatHead);
    }



    @Override
    public void onMeasure(int height, int width) {
        boolean needsLayout = false;
        if (height != maxHeight && width != maxWidth) {
            needsLayout = true; // both changed, must be screen rotation.
        }
        maxHeight = height;
        maxWidth = width;

        int closeButtonCenterX = (int) ((float) width * 0.5f);
        int closeButtonCenterY = (int) ((float) height * 0.9f);

        closeButton.onParentHeightRefreshed();
        closeButton.setCenter(closeButtonCenterX, closeButtonCenterY);

        if (maxHeight > 0 && maxWidth > 0) {
            if (requestedArrangement != null) {
                setArrangementImpl(requestedArrangement);
                requestedArrangement = null;
            } else {
                if (needsLayout) {
                    // this means height changed and we need to redraw.
                    setArrangementImpl(new ArrangementChangeRequest(activeArrangement.getClass(), null, false));

                }
            }
        }

    }



    @Override
    public ChatHead<User> addChatHead(User user, boolean isSticky, boolean animated) {
        ChatHead<User> chatHead = findChatHeadByKey(user);
        if (chatHead == null) {
            chatHead = new ChatHead<User>(this, springSystem, getContext(), isSticky);
            chatHead.setUser(user);
            chatHeads.add(chatHead);
            ViewGroup.LayoutParams layoutParams = chatHeadContainer.createLayoutParams(getConfig().getHeadWidth(), getConfig().getHeadHeight(), Gravity.START | Gravity.TOP, 0);

            chatHeadContainer.addView(chatHead, layoutParams);
            if (chatHeads.size() > config.getMaxChatHeads(maxWidth, maxHeight) && activeArrangement != null) {
                activeArrangement.removeOldestChatHead();
            }
            reloadDrawable(user);
            if (activeArrangement != null)
                activeArrangement.onChatHeadAdded(chatHead, animated);
            else {
                chatHead.getHorizontalSpring().setCurrentValue(-100);
                chatHead.getVerticalSpring().setCurrentValue(-100);
            }
            closeButtonShadow.bringToFront();
        }
        return chatHead;
    }

    @Override
    public ChatHead<User> findChatHeadByKey(User user) {
        for (ChatHead<User> chatHead : chatHeads) {
            if (chatHead.getUser().equals(user))
                return chatHead;
        }

        return null;
    }

    @Override
    public void reloadDrawable(User user) {
        Drawable chatHeadDrawable = viewAdapter.getChatHeadDrawable(user);
        if (chatHeadDrawable != null) {
            findChatHeadByKey(user).setImageDrawable(viewAdapter.getChatHeadDrawable(user));
         //   findChatHeadByKey(key).addShadow();
        }
    }

    @Override
    public void removeAllChatHeads(boolean userTriggered) {
        for (Iterator<ChatHead<User>> iterator = chatHeads.iterator(); iterator.hasNext(); ) {
            ChatHead<User> chatHead = iterator.next();
            iterator.remove();
            onChatHeadRemoved(chatHead, userTriggered);
        }
    }

    @Override
    public boolean removeChatHead(User user, boolean userTriggered) {
        ChatHead chatHead = findChatHeadByKey(user);
        if (chatHead != null) {
            chatHeads.remove(chatHead);
            onChatHeadRemoved(chatHead, userTriggered);
            return true;
        }
        return false;
    }

    private void onChatHeadRemoved(ChatHead chatHead, boolean userTriggered) {
        if (chatHead != null && chatHead.getParent() != null) {
            chatHead.onRemove();
            chatHeadContainer.removeView(chatHead);
            if (activeArrangement != null)
                activeArrangement.onChatHeadRemoved(chatHead);
        }
    }

    @Override
    public ChatHeadOverlayView getOverlayView() {
        return overlayView;
    }

    private void init(Context context, ChatHeadConfig chatHeadDefaultConfig) {
        chatHeadContainer.onInitialized(this);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        this.displayMetrics = metrics;
        this.config = chatHeadDefaultConfig; //TODO : needs cleanup
        chatHeads = new ArrayList<>(5);
        arrowLayout = new UpArrowLayout(context);
        arrowLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chatHeadContainer.addView(arrowLayout, arrowLayout.getLayoutParams());
        arrowLayout.setVisibility(View.GONE);
        springSystem = SpringSystem.create();
        closeButton = new ChatHeadCloseButton(context, this, maxHeight, maxWidth);
        ViewGroup.LayoutParams layoutParams = chatHeadContainer.createLayoutParams(chatHeadDefaultConfig.getCloseButtonHeight(), chatHeadDefaultConfig.getCloseButtonWidth(), Gravity.TOP | Gravity.START, 0);
        chatHeadContainer.addView(closeButton, layoutParams);
        closeButtonShadow = new ImageView(getContext());
        ViewGroup.LayoutParams shadowLayoutParams = chatHeadContainer.createLayoutParams(metrics.heightPixels / 8, metrics.widthPixels, Gravity.BOTTOM, 0);
        closeButtonShadow.setImageResource(R.drawable.dismiss_shadow);
        closeButtonShadow.setVisibility(View.GONE);
        chatHeadContainer.addView(closeButtonShadow, shadowLayoutParams);

        arrangements.put(MinimizedArrangement.class, new MinimizedArrangement(this));
        arrangements.put(MaximizedArrangement.class, new MaximizedArrangement<User>(this));
        setupOverlay(context);
        setConfig(chatHeadDefaultConfig);
        SpringConfigRegistry.getInstance().addSpringConfig(SpringConfigsHolder.DRAGGING, "dragging mode");
        SpringConfigRegistry.getInstance().addSpringConfig(SpringConfigsHolder.NOT_DRAGGING, "not dragging mode");
    }

    private void setupOverlay(Context context) {
        overlayView = new ChatHeadOverlayView(context);
        overlayView.setBackgroundResource(R.drawable.overlay_transition);
        ViewGroup.LayoutParams layoutParams = getChatHeadContainer().createLayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, Gravity.NO_GRAVITY, 0);
        getChatHeadContainer().addView(overlayView, layoutParams);
    }

    public double getDistanceCloseButtonFromHead(float touchX, float touchY) {
        if (closeButton.isDisappeared()) {
            return Double.MAX_VALUE;
        } else {
            int left = closeButton.getLeft();
            int top = closeButton.getTop();
            double xDiff = touchX - left - getChatHeadContainer().getViewX(closeButton) - closeButton.getMeasuredWidth() / 2;
            double yDiff = touchY - top - getChatHeadContainer().getViewY(closeButton) - closeButton.getMeasuredHeight() / 2;
            double distance = Math.hypot(xDiff, yDiff);
            return distance;
        }
    }

    @Override
    public UpArrowLayout getArrowLayout() {
        return arrowLayout;
    }

    @Override
    public void captureChatHeads(ChatHead causingChatHead) {
        activeArrangement.onCapture(this, causingChatHead);
    }


    @Override
    public ChatHeadArrangement getArrangement(Class<? extends ChatHeadArrangement> arrangementType) {
        return arrangements.get(arrangementType);
    }

    @Override
    public void setArrangement(Class<? extends ChatHeadArrangement> arrangement, Bundle extras) {
        setArrangement(arrangement, extras, true);
    }

    @Override
    public void setArrangement(final Class<? extends ChatHeadArrangement> arrangement, Bundle extras, boolean animated) {
        this.requestedArrangement = new ArrangementChangeRequest(arrangement, extras, animated);
        chatHeadContainer.requestLayout();
    }

    /**
     * Should only be called after onMeasure
     *
     * @param requestedArrangementParam
     */
    private void setArrangementImpl(ArrangementChangeRequest requestedArrangementParam) {
        boolean hasChanged = false;
        ChatHeadArrangement requestedArrangement = arrangements.get(requestedArrangementParam.getArrangement());
        ChatHeadArrangement oldArrangement = null;
        ChatHeadArrangement newArrangement = requestedArrangement;
        Bundle extras = requestedArrangementParam.getExtras();
        if (activeArrangement != requestedArrangement) hasChanged = true;
        if (extras == null) extras = new Bundle();

        if (activeArrangement != null) {
            extras.putAll(activeArrangement.getRetainBundle());
            activeArrangement.onDeactivate(maxWidth, maxHeight);
            oldArrangement = activeArrangement;
        }
        activeArrangement = requestedArrangement;
        activeArrangementBundle = extras;
        requestedArrangement.onActivate(this, extras, maxWidth, maxHeight, requestedArrangementParam.isAnimated());
        if (hasChanged) {
            chatHeadContainer.onArrangementChanged(oldArrangement, newArrangement);
        }

    }

    @Override
    public void hideOverlayView(boolean animated) {
        if (overlayVisible) {
            TransitionDrawable drawable = (TransitionDrawable) overlayView.getBackground();
            int duration = OVERLAY_TRANSITION_DURATION;
            if (!animated) duration = 0;
            drawable.reverseTransition(duration);
            overlayView.setClickable(false);
            overlayVisible = false;
        }
    }

    @Override
    public void showOverlayView(boolean animated) {
        if (!overlayVisible) {
            TransitionDrawable drawable = (TransitionDrawable) overlayView.getBackground();
            int duration = OVERLAY_TRANSITION_DURATION;
            if (!animated) duration = 0;
            drawable.startTransition(duration);
            overlayView.setClickable(true);
            overlayVisible = true;
        }
    }

    @Override
    public int[] getChatHeadCoordsForCloseButton(ChatHead chatHead) {
        int[] coords = new int[2];
        int x = (int) (closeButton.getLeft() + closeButton.getEndValueX() + closeButton.getMeasuredWidth() / 2 - chatHead.getMeasuredWidth() / 2);
        int y = (int) (closeButton.getTop() + closeButton.getEndValueY() + closeButton.getMeasuredHeight() / 2 - chatHead.getMeasuredHeight() / 2);
        coords[0] = x;
        coords[1] = y;
        return coords;
    }

    @Override
    public void bringToFront(ChatHead chatHead) {
        if (activeArrangement != null) {
            activeArrangement.bringToFront(chatHead);
        }
    }





    public void onCloseButtonAppear() {
        if (!getConfig().isCloseButtonHidden()) {
            closeButtonShadow.setVisibility(View.VISIBLE);
        }
    }

    public void onCloseButtonDisappear() {
        closeButtonShadow.setVisibility(View.GONE);
    }






    @Override
    public View attachView(ChatHead<User> activeChatHead, ViewGroup parent) {
        View view = viewAdapter.attachView(activeChatHead.getUser(), activeChatHead, parent);
        return view;
    }

    @Override
    public void removeView(ChatHead<User> chatHead, ViewGroup parent) {
        viewAdapter.removeView(chatHead.getUser(), chatHead, parent);
    }


    @Override
    public void detachView(ChatHead<User> chatHead, ViewGroup parent) {
        viewAdapter.detachView(chatHead.getUser(), chatHead, parent);
    }


    @Override
    public ChatHeadConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(ChatHeadConfig config) {
        this.config = config;
        if (closeButton != null) {
//            LayoutParams params = (LayoutParams) closeButton.getLayoutParams();
//            params.width = config.getCloseButtonWidth();
//            params.height = config.getCloseButtonHeight();
//            params.bottomMargin = config.getCloseButtonBottomMargin();
//            closeButton.setLayoutParams(params);
            if (config.isCloseButtonHidden()) {
                closeButton.setVisibility(View.GONE);
                closeButtonShadow.setVisibility(View.GONE);
            } else {
                closeButton.setVisibility(View.VISIBLE);
                closeButtonShadow.setVisibility(View.VISIBLE);
            }
        }
        for (Map.Entry<Class<? extends ChatHeadArrangement>, ChatHeadArrangement> arrangementEntry : arrangements.entrySet()) {
            arrangementEntry.getValue().onConfigChanged(config);
        }

    }



    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (closeButton != null) {
            closeButton.onParentHeightRefreshed();
        }
    }


    private class ArrangementChangeRequest {
        private final Bundle extras;
        private final Class<? extends ChatHeadArrangement> arrangement;
        private final boolean animated;

        public ArrangementChangeRequest(Class<? extends ChatHeadArrangement> arrangement, Bundle extras, boolean animated) {
            this.arrangement = arrangement;
            this.extras = extras;
            this.animated = animated;
        }

        public Bundle getExtras() {
            return extras;
        }

        public Class<? extends ChatHeadArrangement> getArrangement() {
            return arrangement;
        }

        public boolean isAnimated() {
            return animated;
        }
    }
}

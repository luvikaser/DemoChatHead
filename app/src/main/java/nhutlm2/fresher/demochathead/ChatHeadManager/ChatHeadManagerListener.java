package nhutlm2.fresher.demochathead.ChatHeadManager;

/**
 * Created by cpu1-216-local on 07/03/2017.
 */

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHead;
import nhutlm2.fresher.demochathead.ChatHeadArrangement.ChatHeadArrangement;
import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadCloseButton;
import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadContainer.ChatHeadContainer;
import nhutlm2.fresher.demochathead.ChatHeadUI.PopupFragment.ChatHeadViewAdapter;
import nhutlm2.fresher.demochathead.ChatHeadUI.PopupFragment.UpArrowLayout;
import nhutlm2.fresher.demochathead.Utils.ChatHeadConfig;
import nhutlm2.fresher.demochathead.Utils.ChatHeadOverlayView;

public interface ChatHeadManagerListener<User extends Serializable> {
        List<ChatHead<User>> getChatHeads();

        void setViewAdapter(ChatHeadViewAdapter chatHeadViewAdapter);

        ChatHeadCloseButton getCloseButton();

        ChatHeadArrangement getActiveArrangement();

        void selectChatHead(ChatHead chatHead);

        void onMeasure(int height, int width);

        ChatHead<User> addChatHead(User user, boolean isSticky, boolean animated);

        ChatHead<User> findChatHeadByKey(User user);

        void reloadDrawable(User user);

        void removeAllChatHeads(boolean userTriggered);

        boolean removeChatHead(User user, boolean userTriggered);

        ChatHeadOverlayView getOverlayView();

        void captureChatHeads(ChatHead causingChatHead);

        ChatHeadArrangement getArrangement(Class<? extends ChatHeadArrangement> arrangementType);

        void setArrangement(Class<? extends ChatHeadArrangement> arrangement, Bundle extras);

        void setArrangement(Class<? extends ChatHeadArrangement> arrangement, Bundle extras, boolean animated);

        View attachView(ChatHead<User> activeChatHead, ViewGroup parent);

        void detachView(ChatHead<User> chatHead, ViewGroup parent);

        void removeView(ChatHead<User> chatHead, ViewGroup parent);

        ChatHeadConfig getConfig();

        void setConfig(ChatHeadConfig config);

        double getDistanceCloseButtonFromHead(float rawX, float rawY);

        void hideOverlayView(boolean animated);

        void showOverlayView(boolean animated);

        int[] getChatHeadCoordsForCloseButton(ChatHead chatHead);

        void bringToFront(ChatHead chatHead);

        UpArrowLayout getArrowLayout();

        ChatHeadContainer getChatHeadContainer();

        DisplayMetrics getDisplayMetrics();

        int getMaxWidth();

        int getMaxHeight();

        Context getContext();

        void onSizeChanged(int w, int h, int oldw, int oldh);


        }
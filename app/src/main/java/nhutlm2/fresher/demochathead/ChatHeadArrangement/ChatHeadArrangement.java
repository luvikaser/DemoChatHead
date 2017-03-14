package nhutlm2.fresher.demochathead.ChatHeadArrangement;

import android.os.Bundle;

import com.facebook.rebound.Spring;

import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHead;
import nhutlm2.fresher.demochathead.Utils.ChatHeadConfig;
import nhutlm2.fresher.demochathead.ChatHeadManager.ChatHeadManager;

/**
 * Created by cpu1-216-local on 07/03/2017.
 */

public abstract class ChatHeadArrangement {
    public abstract void onActivate(ChatHeadManager container, Bundle extras, int maxWidth, int maxHeight, boolean animated);

    public abstract void onDeactivate(int maxWidth, int maxHeight);

    public abstract void onSpringUpdate(ChatHead activeChatHead, boolean isDragging, int maxWidth, int maxHeight, Spring spring, Spring activeHorizontalSpring, Spring activeVerticalSpring, int totalVelocity);

    public abstract boolean handleTouchUp(ChatHead activeChatHead, int xVelocity, int yVelocity, Spring activeHorizontalSpring, Spring activeVerticalSpring, boolean wasDragging);

    public abstract void onChatHeadAdded(ChatHead chatHead, boolean animated);

    public abstract void onChatHeadRemoved(ChatHead removed);

    public abstract void onCapture(ChatHeadManager container, ChatHead activeChatHead);

    public abstract void selectChatHead(ChatHead chatHead);

    public abstract void bringToFront(ChatHead chatHead);

    public abstract boolean shouldShowCloseButton(ChatHead chatHead);

    public abstract Integer getHeroIndex();

    public abstract void onConfigChanged(ChatHeadConfig newConfig);

    public abstract Bundle getRetainBundle();

    public abstract boolean canDrag(ChatHead chatHead);

    public abstract void removeOldestChatHead();
}

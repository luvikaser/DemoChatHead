package nhutlm2.fresher.demochathead.ChatHeadUI.PopupFragment;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHead;

/**
 * Created by luvikaser on 01/03/2017.
 */

public interface ChatHeadViewAdapter<User> {

    View attachView(User user, ChatHead<? extends Serializable> chatHead, ViewGroup parent);

    void detachView(User user, ChatHead<? extends Serializable> chatHead, ViewGroup parent);

    void removeView(User user, ChatHead<? extends Serializable> chatHead, ViewGroup parent);

    Drawable getChatHeadDrawable(User user);

}

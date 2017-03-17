package nhutlm2.fresher.demochathead.ChatHeadService;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHead;
import nhutlm2.fresher.demochathead.ChatHeadArrangement.MinimizedArrangement;
import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadContainer.ChatHeadContainer;
import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadDrawable.AvatarDrawer;
import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadDrawable.ChatHeadDrawable;
import nhutlm2.fresher.demochathead.ChatHeadUI.ChatHeadDrawable.NotificationDrawer;
import nhutlm2.fresher.demochathead.ChatHeadManager.ChatHeadManager;
import nhutlm2.fresher.demochathead.ChatHeadUI.PopupFragment.ChatHeadViewAdapter;
import nhutlm2.fresher.demochathead.R;

/**
 * Created by luvikaser on 07/03/2017.
 */

public class ChatHeadService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private ChatHeadManager<User> chatHeadManager;
    private ChatHeadContainer chatHeadContainer;
    private Map<User, View> viewCache = new HashMap<>();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        chatHeadContainer = new ChatHeadContainer(this);
        chatHeadManager = new ChatHeadManager<User>(this, chatHeadContainer);
        chatHeadManager.setViewAdapter(new ChatHeadViewAdapter<User>() {

            @Override
            public View attachView(User user, ChatHead chatHead, ViewGroup parent) {
                View cachedView = viewCache.get(user);
                if (cachedView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.fragment_test, parent, false);
                    TextView identifier = (TextView) view.findViewById(R.id.identifier);
                    identifier.setText(String.valueOf(user.id));
                    cachedView = view;
                    viewCache.put(user, view);
                }
                cachedView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                parent.addView(cachedView);
                return cachedView;
            }

            @Override
            public void detachView(User user, ChatHead<? extends Serializable> chatHead, ViewGroup parent) {
                View cachedView = viewCache.get(user);
                if(cachedView != null) {
                    parent.removeView(cachedView);
                }
            }

            @Override
            public void removeView(User user, ChatHead<? extends Serializable> chatHead, ViewGroup parent) {
                View cachedView = viewCache.get(user);
                if(cachedView != null) {
                    viewCache.remove(user);
                    parent.removeView(cachedView);
                }
                if (chatHeadManager.getChatHeads().size() == 0){
                    chatHeadContainer.destroy();
                    stopSelf();
                }
            }

            @Override
            public Drawable getChatHeadDrawable(User user) {
                return ChatHeadService.this.getChatHeadDrawable(user);
            }
        });

        chatHeadManager.setArrangement(MinimizedArrangement.class, null);
        testData();

        return super.onStartCommand(intent, flags, startId);
    }



    private void testData() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.avatar1, options);
        User user1 = new User(1, 5, bitmap1);
        addChatHead(user1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.avatar2, options);
        User user2 = new User(2, 10, bitmap2);
        addChatHead(user2);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.avatar3, options);
        User user3 = new User(3, 15, bitmap3);
        addChatHead(user3);
        Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.avatar4, options);
        User user4 = new User(4, 20, bitmap4);
        addChatHead(user4);

        Bitmap bitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.avatar4, options);
        User user5 = new User(4, 20, bitmap5);
        addChatHead(user5);
        Bitmap bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.avatar4, options);
        User user6 = new User(4, 20, bitmap6);
        addChatHead(user6);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Drawable getChatHeadDrawable(User user) {
        ChatHeadDrawable chatHeadDrawable = new ChatHeadDrawable();

        chatHeadDrawable.setAvatarDrawer(new AvatarDrawer(user.avatar, new BitmapShader(user.avatar, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)));
        chatHeadDrawable.setNotificationDrawer(new NotificationDrawer().setNotificationText(String.valueOf(user.countMessage)).setNotificationAngle(135).setNotificationColor(Color.WHITE, Color.RED));
        return chatHeadDrawable;
    }

    public void addChatHead(User user) {
        chatHeadManager.addChatHead(user);
        chatHeadManager.bringToFront(chatHeadManager.findChatHeadByKey(user));
    }


    public void minimize() {
        chatHeadManager.setArrangement(MinimizedArrangement.class, null);
    }

    public class LocalBinder extends Binder {
        public ChatHeadService getService() {
            return ChatHeadService.this;
        }
    }
}
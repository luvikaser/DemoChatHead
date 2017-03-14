package nhutlm2.fresher.demochathead.ChatHeadService;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by cpu1-216-local on 13/03/2017.
 */

public class User implements Serializable {
    public int id;
    public int countMessage;
    public Bitmap avatar;

    public User(int id, int countMessage, Bitmap avatar) {
        this.id = id;
        this.countMessage = countMessage;
        this.avatar = avatar;
    }
}

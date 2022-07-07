package jereme.urban_network_dating.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import jereme.urban_network_dating.R;


public class GlideToast {

    private String message;
    private Activity activity;
    private int icon;
    public static int LENGTHTOOLONG = 1500;
    public static int LENGTHLONG = 2000;
    public static int LENGTHMEDIUM = 750;
    public static int LENGTHSHORT = 500;
    public static int LENGTHQUICK = 1250;
    public static int TOP = 1;
    public static int CENTER = 2;
    public static int BOTTOM = 3;
    public static String DEFAULTTOAST = "DEFAULT";
    public static String SUCCESSTOAST = "SUCCESS";
    public static String FAILTOAST = "FAIL";
    public static String WARNINGTOAST = "WARN";
    public static String INFOTOAST = "INFO";
    public static String CUSTOMTOAST = "CUSTOM";


    private GlideToast() {
    }


    public static class makeToast {
        private String message;
        private Activity activity;
        private int duration;
        private int gravity;
        private String style;
        private String backgroundcolor;


        public makeToast(Activity activity, String message) {
            this.activity = activity;
            this.message = message;
            this.duration = LENGTHLONG;
            this.style = CUSTOMTOAST;
            this.backgroundcolor = "#00ff00";
            this.gravity = TOP;
            show();

        }


        public void setStyle(String style, LinearLayout background) {
            switch (style) {
                case "CUSTOM":
                    background.setBackgroundColor(Color.parseColor(backgroundcolor));
                    break;
                default:
                    break;
            }
        }


        public void setGravity(int gravity, Dialog dialog) {
            switch (gravity) {
                case 1:
                    dialog.getWindow().setGravity(Gravity.TOP);
                    break;
                case 2:
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    break;
                case 3:
                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                    break;
                default:
                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                    break;
            }
        }


        TextView messageTV;
        ImageView iconImg;
        Dialog dialog;
        LinearLayout background;

        public void show() {
            dialog = new Dialog(activity, R.style.BottomDialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.item_toast);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            setGravity(gravity,dialog);
            messageTV = (TextView) dialog.findViewById(R.id.toast_text);
            background = (LinearLayout) dialog.findViewById(R.id.background);
            messageTV.setText(message);
            dialog.setCancelable(true);
            setStyle(style, background);
            dialog.show();
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, duration);

        }


    }


}


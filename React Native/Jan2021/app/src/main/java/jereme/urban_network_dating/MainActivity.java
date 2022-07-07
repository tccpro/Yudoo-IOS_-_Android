package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import jereme.urban_network_dating.Utils.WeiboDialogUtils;


public class MainActivity extends AppCompatActivity {

    Dialog loadingDialog;
    LinearLayout containerLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        containerLL = (LinearLayout)findViewById(R.id.slackloadder);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        Handler handler = new Handler();
        TashieLoader tashie = new TashieLoader(this, 5, 30, 10, ContextCompat.getColor(this, R.color.colorPrimary));

        tashie.setAnimDuration(500);
        tashie.setAnimDelay(100);
        tashie.setInterpolator(new LinearInterpolator());

        containerLL.addView(tashie);
        //  loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Authenticating");
        //  loadingDialog.show();

        Runnable r = new Runnable() {
            public void run() {
                WeiboDialogUtils.closeDialog(loadingDialog);
                Intent intent = new Intent(MainActivity.this, SelectLanguageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        };
        handler.postDelayed(r, 4000);
    }
}

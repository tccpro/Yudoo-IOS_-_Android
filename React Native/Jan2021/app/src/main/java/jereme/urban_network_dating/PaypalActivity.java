package jereme.urban_network_dating;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PaypalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        WebView w = (WebView) findViewById(R.id.webview);
        w.loadDataWithBaseURL(null, _loadHTML(), "text/html", "utf-8", null);
        w.getSettings().setJavaScriptEnabled(true);
        w.setWebViewClient(new WebViewClient());
    }

    String _loadHTML() {
        return " <html>"+
                "<body onload='document.f.submit();'>"+
                " <form id='f' name='f' method='post' action='https://urban.network/pg/create-payment-intent-paypal'>"+
                "<input type='hidden' name='price' value='11' />"+
                " </form>"+
                "  </body>"+
                "</html>";
    }
}
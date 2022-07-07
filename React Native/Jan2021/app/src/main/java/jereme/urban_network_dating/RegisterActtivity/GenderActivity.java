package jereme.urban_network_dating.RegisterActtivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import jereme.urban_network_dating.R;

public class GenderActivity extends AppCompatActivity implements View.OnClickListener {
    Button continues;
    final String onbkcolor = "#FF5776";
    final String offbkcolor = "#FFF1F3";
    final String onfcolor = "#ffffff";
    final String offcolor = "#FF5776";
    ArrayList<String> lookinglist = new ArrayList<String>();
    boolean isbtnmaleSelected = false, isbtnfemaleSelected = false;
    Button btnmale, btnfemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geneder);
        btnmale = (Button) findViewById(R.id.btnmale);
        btnfemale = (Button) findViewById(R.id.btnfemale);

        btnmale.setOnClickListener(this);
        btnfemale.setOnClickListener(this);

        continues = (Button) findViewById(R.id.continues);
        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selected_gender = isbtnmaleSelected?"Male":"Female";
                Intent intent = new Intent(GenderActivity.this, NameActivity.class);
                SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(GenderActivity.this);
                SharedPreferences.Editor editor = saveUser.edit();
                editor.putString("storedgender", selected_gender);
                editor.commit();
                startActivity(intent);
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnmale) {
            btnmale.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
            btnmale.setTextColor(Color.parseColor(onfcolor));
            btnfemale.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
            btnfemale.setTextColor(Color.parseColor(offcolor));
            isbtnmaleSelected = true;
            isbtnfemaleSelected = false;
        } else if(v.getId() == R.id.btnfemale) {
            btnfemale.setBackgroundTintList(getResources().getColorStateList(R.color.rose));
            btnfemale.setTextColor(Color.parseColor(onfcolor));
            btnmale.setBackgroundTintList(getResources().getColorStateList(R.color.literose));
            btnmale.setTextColor(Color.parseColor(offcolor));
            isbtnfemaleSelected = true;
            isbtnmaleSelected = false;
        }
    }
}

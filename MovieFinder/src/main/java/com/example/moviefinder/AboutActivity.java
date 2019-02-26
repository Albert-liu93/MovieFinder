package com.example.moviefinder;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.moviefinder.Constants.Constants;

/**
 * Created by Albert on 5/29/2018.
 */

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private Button privacyPolicyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_page);
        privacyPolicyBtn = findViewById(R.id.privacyPolicyBtn);
        privacyPolicyBtn.setOnClickListener(this);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.privacyPolicyBtn) {
            try {
                Uri webpage = Uri.parse(Constants.PRIVACY_POLICY_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            } catch (Exception e) {
                Toast.makeText(this, "No application can handle this request. Please install a web browser.",  Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }




        }
    }
}

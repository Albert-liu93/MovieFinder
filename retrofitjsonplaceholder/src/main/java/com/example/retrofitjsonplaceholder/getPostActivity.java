package com.example.retrofitjsonplaceholder;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retrofitjsonplaceholder.API.PostClient;
import com.example.retrofitjsonplaceholder.Model.Post;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class getPostActivity extends AppCompatActivity {

    private EditText postId;
    private Button getPostBtn;
    private TextView postResult;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_post);


        mContext = this;
        postId = findViewById(R.id.postId_et);
        getPostBtn = findViewById(R.id.getPost_btn);
        postResult = findViewById(R.id.postResult_TV);

        getPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postId.getText().toString().isEmpty()) {
                    Toast.makeText(mContext , "Must enter a post Id to retrieve data!", Toast.LENGTH_LONG).show();
                } else {
                    makeRetrofitCall();
                }
            }
        });





    }


    private void makeRetrofitCall() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();
        PostClient client = retrofit.create(PostClient.class);
        int POST_ID;
        try {
            POST_ID = Integer.parseInt(postId.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered text is not a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<String> call = client.getPostReturnString(POST_ID);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                postResult.setText(null);
                postResult.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_LONG).show();
            }
        });

    }
}

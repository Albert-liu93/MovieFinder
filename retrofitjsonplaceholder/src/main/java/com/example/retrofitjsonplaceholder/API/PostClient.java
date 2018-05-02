package com.example.retrofitjsonplaceholder.API;

import com.example.retrofitjsonplaceholder.Model.Post;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Albert on 4/29/2018.
 */

public interface PostClient {


    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") int id);

    @GET("posts/{id}")
    Call<String> getPostReturnString(@Path("id") int id);
}

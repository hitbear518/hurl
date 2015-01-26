package me.sw.hurl;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by hitbe_000 on 1/26/2015.
 */
public interface StackOverflowInterface {
	@GET("/2.1/questions?order=desc&sort=creation&site=stackoverflow")
	void questions(@Query("tagged") String tags, Callback<SOQuestions> cb);
}

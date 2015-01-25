package me.sw.hurl;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;

/**
 * Created by Sen on 1/25/2015.
 */
public class LoadThread extends Thread {
	static final String SO_URL = "https://api.stackexchange.com/2.1/questions?"
			+ "order=desc&sort=creation&site=stackoverflow&tagged=android";

	@Override
	public void run() {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL(SO_URL).openConnection();

			try {
				InputStream in = c.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				SOQuestions questions = new Gson().fromJson(reader, SOQuestions.class);

				reader.close();

				EventBus.getDefault().post(new QuestionLoadedEvent(questions));
			} catch (IOException e) {
				Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
			} finally {
				c.disconnect();
			}
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
		}
	}
}

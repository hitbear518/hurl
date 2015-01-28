package me.sw.hurl;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sen on 1/25/2015.
 */
public class QuestionsFragment extends ListFragment implements Callback<SOQuestions>, FutureCallback<JsonObject> {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

//		RestAdapter restAdapter = new RestAdapter.Builder()
//				.setEndpoint("https://api.stackexchange.com")
//				.build();
//		StackOverflowInterface so = restAdapter.create(StackOverflowInterface.class);
//		so.questions("android", this);
		Ion.with(this).load("https://api.stackexchange.com/2.1/questions?"
				+ "order=desc&sort=creation&site=stackoverflow&"
				+ "tagged=android").asJsonObject().setCallback(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		EventBus.getDefault().registerSticky(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Item item = ((ItemsAdapter) getListAdapter()).getItem(position);

		EventBus.getDefault().post(new QuestionClickedEvent(item));
	}

	public void onEventMainThread(QuestionLoadedEvent event) {
		setListAdapter(new ItemsAdapter(event.questions.items));
	}

	@Override
	public void success(SOQuestions soQuestions, Response response) {
		setListAdapter(new ItemsAdapter(soQuestions.items));
	}

	@Override
	public void failure(RetrofitError error) {
		Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
		Log.e(getClass().getSimpleName(), "Exception from retrofit request to StackOverflow", error);
	}

	@Override
	public void onCompleted(Exception e, JsonObject result) {
		if (e != null) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e(getClass().getSimpleName(), "Exception from Ion request to StackOverflow", e);
		}

		if (result != null) {
			JsonArray items = result.getAsJsonArray("items");
			ArrayList<JsonObject> normalized = new ArrayList<>();
			for (int i = 0; i < items.size(); i++) {
				normalized.add(items.get(i).getAsJsonObject());
			}
			setListAdapter(new ItemsAdapter2(normalized));
		}
	}

	class ItemsAdapter extends ArrayAdapter<Item> {
		ItemsAdapter(List<Item> items) {
			super(getActivity(), R.layout.row, R.id.title, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);

			Item item = getItem(position);

			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			Picasso.with(getActivity()).load(item.owner.profileImage)
					.placeholder(R.drawable.owner_placeholder)
					.error(R.drawable.owner_error).into(icon);

//			TextView title = (TextView) row.findViewById(R.id.title);
//			title.setText(Html.fromHtml(getItem(position).title));

			return row;
		}
	}

	class ItemsAdapter2 extends ArrayAdapter<JsonObject> {

		int size;

		public ItemsAdapter2(List<JsonObject> items) {
			super(getActivity(), R.layout.row, R.id.title, items);

			float temp = getResources().getDimension(R.dimen.icon);
			size = getResources().getDimensionPixelSize(R.dimen.icon);
			temp = 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView,parent);

			JsonObject item = getItem(position);

			TextView title = (TextView) row.findViewById(R.id.title);
			title.setText(item.get("title").getAsString());

			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			Ion.with(icon).placeholder(R.drawable.owner_placeholder)
					.resize(size, size)
					.centerCrop()
					.error(R.drawable.owner_error)
					.load(item.getAsJsonObject("owner").get("profile_image").getAsString());
			return row;
		}
	}
}

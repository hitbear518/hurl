package me.sw.hurl;

import android.app.ListFragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sen on 1/25/2015.
 */
public class QuestionsFragment extends ListFragment implements Callback<SOQuestions> {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint("https://api.stackexchange.com")
				.build();
		StackOverflowInterface so = restAdapter.create(StackOverflowInterface.class);
		so.questions("android", this);
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

	class ItemsAdapter extends ArrayAdapter<Item> {
		ItemsAdapter(List<Item> items) {
			super(getActivity(), android.R.layout.simple_list_item_1, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			TextView title = (TextView) row.findViewById(android.R.id.text1);

			title.setText(Html.fromHtml(getItem(position).title));

			return row;
		}
	}
}

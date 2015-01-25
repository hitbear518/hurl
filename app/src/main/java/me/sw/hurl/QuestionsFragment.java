package me.sw.hurl;

import android.app.ListFragment;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Sen on 1/25/2015.
 */
public class QuestionsFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		new LoadThread().start();
	}

	@Override
	public void onResume() {
		super.onResume();

		EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Item item = ((ItemAdapter) getListAdapter()).getItem(position);

		EventBus.getDefault().post(new QuestionClickedEvent(item));
	}

	public void onEventMainThread(QuestionLoadedEvent event) {
		setListAdapter(new ItemsAdapter(event.questions.items));
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

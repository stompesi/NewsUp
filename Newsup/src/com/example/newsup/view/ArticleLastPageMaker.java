package com.example.newsup.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newsup.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


public class ArticleLastPageMaker extends YouTubeBaseActivity implements
YouTubePlayer.OnInitializedListener{

	private LayoutInflater Inflater;
	private Context context;

	public static final String API_KEY = "AIzaSyASbhpfpjbXyEAg9sOejF8hol3dLmJNgHI";
	public static final String VIDEO_ID = "o7VVHhK9zf0";


	public ArticleLastPageMaker(Context context, LayoutInflater Inflater)
	{
		this.context = context;
		this.Inflater = Inflater;
	}

	public LinearLayout getLastPage(){
		ArrayList<ListItem> itemArray;
		ListView itemList; 
		TextView textview;
		YouTubePlayerView youTubePlayerView;

		LinearLayout lastLayout = (LinearLayout) Inflater.inflate(R.layout.view_article_detail_last_page, null);
		itemList =(ListView)(lastLayout).findViewById(R.id.itemList);
		textview = (TextView)(lastLayout).findViewById(R.id.viewArticleBottom);

		youTubePlayerView = (YouTubePlayerView)(lastLayout).findViewById(R.id.youtube_view);
		youTubePlayerView.initialize(API_KEY, this);

		itemArray = new ArrayList<ListItem>();
		ListItem listItem; 
		listItem = new ListItem(R.drawable.ic_launcher, "오늘의 기사1", "최희철");itemArray.add(listItem);
		listItem = new ListItem(R.drawable.ic_launcher, "오늘의 기사2", "최희철");itemArray.add(listItem);
		listItem = new ListItem(R.drawable.ic_launcher, "오늘의 기사3", "최희철");itemArray.add(listItem);
		LastPageListAdapter lastPageListAdapter = new LastPageListAdapter(context, R.layout.view_article_detail_last_page_list_item, itemArray);
		itemList.setAdapter(lastPageListAdapter);

		//		setPageNumber(lastLayout, maxPageNumber+1, maxPageNumber+1);
		return lastLayout;

	}
	public class ListItem{

		int sumNail;
		String title,author;
		public ListItem(int sumNail,String title, String author) {

			this.sumNail = sumNail;
			this.title = title;
			this.author = author;

		}
	}

	private class LastPageListAdapter extends BaseAdapter{

		Context context; 
		LayoutInflater Inflater;
		ArrayList<ListItem> array;
		int layout;

		public LastPageListAdapter(Context context, int layout , ArrayList<ListItem> array){
			this.context = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.array = array;
			this.layout = layout;
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return array.size();
		}
		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return array.get(position).title;
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final int pos = position;
			if(convertView ==null){
				convertView = Inflater.inflate(layout, parent,false);
			}
			ImageView imageItem = (ImageView)convertView.findViewById(R.id.iamgeItem);
			imageItem.setImageResource(array.get(position).sumNail);

			TextView titleItem = (TextView)convertView.findViewById(R.id.titleItem);
			titleItem.setText(array.get(position).title);

			TextView authorItem = (TextView)convertView.findViewById(R.id.authorItem);
			authorItem.setText(array.get(position).author);


			return convertView;
		}

	}

	@Override
	public void onInitializationFailure(
			com.google.android.youtube.player.YouTubePlayer.Provider provider,
			YouTubeInitializationResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitializationSuccess(
			com.google.android.youtube.player.YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		
		 if (!wasRestored) {
				player.cueVideo(VIDEO_ID);
			}
		
	}





}

package com.example.newsup.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newsup.R;
import com.example.newsup.activity.ArticleActivity;
import com.example.newsup.activity.transmission.structure.Image;
import com.example.newsup.network.NewsUpImageLoader;
import com.example.newsup.view.structure.ArticleDetailInfomation;
import com.example.newsup.view.structure.RelatedArticle;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


public class ArticleLastPageMaker extends YouTubeBaseActivity implements
YouTubePlayer.OnInitializedListener{

	private LayoutInflater Inflater;
	private Context context;

	public static final String API_KEY = "AIzaSyASbhpfpjbXyEAg9sOejF8hol3dLmJNgHI";
	public String videoId;
	WebView webView;
	private boolean isExistVideo;
	LinearLayout relatevie_second_layout,relatevie_original_layout;

	private ArticleDetailInfomation articleDetailInfomation;

	public ArticleLastPageMaker(Context context, LayoutInflater Inflater, boolean isExistVideo,
			ArticleDetailInfomation articleDetailInfomation)
	{
		this.context = context;
		this.Inflater = Inflater;
		this.isExistVideo = isExistVideo;
		this.articleDetailInfomation = articleDetailInfomation;
	}

	public LinearLayout getLastPage(){
		ArrayList<ListItem> itemArray;
		ListView itemList; 
		TextView textview;
		YouTubePlayerView youTubePlayerView;
		


		LinearLayout lastLayout = (LinearLayout) Inflater.inflate(R.layout.view_article_detail_last_page, null);
		itemList =(ListView)(lastLayout).findViewById(R.id.itemList);
		textview = (TextView)(lastLayout).findViewById(R.id.viewArticleBottom);
		webView = (WebView)(lastLayout).findViewById(R.id.relative_webview);
		relatevie_second_layout = (LinearLayout)(lastLayout).findViewById(R.id.relatevie_second_layout);
		relatevie_original_layout = (LinearLayout)(lastLayout).findViewById(R.id.relatevie_second_layout);

		youTubePlayerView = (YouTubePlayerView)(lastLayout).findViewById(R.id.youtube_view);
		if(articleDetailInfomation.getRelatedArticleList().size() == 0) {
			itemList.setVisibility(View.GONE);
			TextView relatedArticle = (TextView)(lastLayout).findViewById(R.id.relatedArticle);
			relatedArticle.setVisibility(View.GONE);
		}

		if(isExistVideo) {
			videoId = articleDetailInfomation.getVideoId();
			youTubePlayerView.initialize(API_KEY, this);
		} else {
			TextView relatedArticleTitle = (TextView)(lastLayout).findViewById(R.id.relatedArticleTitle);
			youTubePlayerView.setVisibility(View.GONE);
			relatedArticleTitle.setVisibility(View.GONE);
		}

		itemArray = new ArrayList<ListItem>();
		ListItem listItem; 
		ArrayList<RelatedArticle> relatedArticleList = articleDetailInfomation.getRelatedArticleList();
		for(int i = 0 ; i < relatedArticleList.size() ; i++) {
			String title = relatedArticleList.get(i).getTitle();
			String description = relatedArticleList.get(i).getDescription();

			listItem = new ListItem(relatedArticleList.get(i).getImageInfo(), title, description);
			itemArray.add(listItem);
		}

		LastPageListAdapter lastPageListAdapter = new LastPageListAdapter(context, R.layout.view_article_detail_last_page_list_item, itemArray);
		itemList.setAdapter(lastPageListAdapter);
		itemList.setOnItemClickListener(mItemClickListener);

		//		setPageNumber(lastLayout, maxPageNumber+1, maxPageNumber+1);
		return lastLayout;

	}
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long l_position) {
			String URL = articleDetailInfomation.getRelatedArticleList().get(position).getURL();
			
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
			context.startActivity(intent);
			
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM")));
		}
	};

	private class WebViewClientClass extends WebViewClient { 
		@Override 
		public boolean shouldOverrideUrlLoading(WebView view, String url) { 
			view.loadUrl(url); 
			return true; 
		} 
	}


	public class ListItem{

		Image imageInfo;
		String title,author;
		public ListItem(Image imageInfo,String title, String author) {
			this.imageInfo = imageInfo;
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

			if(array.get(position).imageInfo != null){
				ImageView imageView = (ImageView)convertView.findViewById(R.id.iamgeItem);
				NewsUpImageLoader.loadImage(imageView, array.get(position).imageInfo.getURL(), 
						array.get(position).imageInfo.getColor());
			}

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
			player.cueVideo(videoId);
		}

	}
}

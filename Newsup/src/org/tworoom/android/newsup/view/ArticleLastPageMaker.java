package org.tworoom.android.newsup.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.tworoom.android.newsup.R;
import org.tworoom.android.newsup.activity.transmission.structure.Image;
import org.tworoom.android.newsup.network.NewsUpImageLoader;
import org.tworoom.android.newsup.view.structure.ArticleDetailInfomation;
import org.tworoom.android.newsup.view.structure.RelatedArticle;
import org.tworoom.android.newsup.view.structure.RelatedVideo;


public class ArticleLastPageMaker{

	private LayoutInflater Inflater;
	private Context context;

	public static final String API_KEY = "AIzaSyASbhpfpjbXyEAg9sOejF8hol3dLmJNgHI";
	public String videoId;
	WebView webView;
	LinearLayout relatevie_second_layout,relatevie_original_layout;

	private ArticleDetailInfomation articleDetailInfomation;
	
	

	public ArticleLastPageMaker(Context context, LayoutInflater Inflater, ArticleDetailInfomation articleDetailInfomation)
	{
		this.context = context;
		this.Inflater = Inflater;
		this.articleDetailInfomation = articleDetailInfomation;
	}

	public LinearLayout getLastPage(){
		ArrayList<ListItem> itemArray;
		ListView itemList;
		LinearLayout lastLayout = (LinearLayout) Inflater.inflate(R.layout.view_article_detail_last_page, null);
		itemList =(ListView)(lastLayout).findViewById(R.id.itemList);
		FrameLayout youtube_1 =(FrameLayout)(lastLayout).findViewById(R.id.youtube_1);
		FrameLayout youtube_2 =(FrameLayout)(lastLayout).findViewById(R.id.youtube_2);
		
		if(articleDetailInfomation.getRelatedArticleList().size() == 0) {
			itemList.setVisibility(View.GONE);
			TextView relatedArticle = (TextView)(lastLayout).findViewById(R.id.relatedArticle);
			relatedArticle.setVisibility(View.GONE);
		}
		switch(articleDetailInfomation.getRelatedVideoList().size()) {
		case 1:
			RelatedVideo relatedVideo= articleDetailInfomation.getRelatedVideoList().get(0);
			youtube_1.setVisibility(View.VISIBLE);
			TextView title = (TextView) youtube_1.findViewById(R.id.youtube_1_title);
			ImageView imageView = (ImageView) youtube_1.findViewById(R.id.youtube_1_image);
			
			title.setText(relatedVideo.getTitle());
			NewsUpImageLoader.loadImage(imageView, relatedVideo.getImageURL(), "#ffffff");
			break;
		case 2:
			RelatedVideo relatedVideo1 = articleDetailInfomation.getRelatedVideoList().get(0);
			RelatedVideo relatedVideo2 = articleDetailInfomation.getRelatedVideoList().get(1);
			youtube_1.setVisibility(View.VISIBLE);
			TextView youtube_1_title = (TextView) youtube_1.findViewById(R.id.youtube_1_title);
			ImageView youtube_1_imageView = (ImageView) youtube_1.findViewById(R.id.youtube_1_image);
			
			youtube_1_title.setText(relatedVideo1.getTitle());
			NewsUpImageLoader.loadImage(youtube_1_imageView, relatedVideo1.getImageURL(), "#ffffff");
			
			youtube_2.setVisibility(View.VISIBLE);
			TextView youtube_2_title = (TextView) youtube_2.findViewById(R.id.youtube_2_title);
			ImageView iyoutube_2_mageView = (ImageView) youtube_2.findViewById(R.id.youtube_2_image);
			
			youtube_2_title.setText(relatedVideo2.getTitle());
			NewsUpImageLoader.loadImage(iyoutube_2_mageView, relatedVideo2.getImageURL(), "#ffffff");
			break;
		case 0:
			TextView relatedVideoTitle = (TextView)(lastLayout).findViewById(R.id.relatedVideoTitle);
			relatedVideoTitle.setVisibility(View.GONE);
			break;
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
		return lastLayout;
	}
	
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long l_position) {
			String URL = articleDetailInfomation.getRelatedArticleList().get(position).getURL();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
			context.startActivity(intent);
		}
	};

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
		LayoutInflater Inflater;
		ArrayList<ListItem> array;
		int layout;

		public LastPageListAdapter(Context context, int layout , ArrayList<ListItem> array){
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
}

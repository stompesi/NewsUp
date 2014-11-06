package article.view.detail;

import java.util.ArrayList;

import manager.ImageViewManager;
import network.Network;
import ArticleReadInfo.ArticleReadInfo;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import application.NewsUpApp;
import article.view.ArticleFlipViewManager;
import article.view.detail.schema.ImageInfo;
import article.view.detail.splitter.ArticleDetailPage;
import article.view.detail.splitter.ContentSplitter;
import article.view.detail.splitter.PageSplitter;

import com.example.flipview.R;

import database.Article;

public class ArticleDetailManager extends ArticleFlipViewManager {
	private int viewHeight;
	private int viewWidht;
	private int viewPadding;
	private int textSize;
	private int contentSize;

	private ArticleReadInfo articleReadInfo;
	private int pageReadStartTime;

	public ArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
		super(context, flipper, offset);
		this.context = context;
		this.viewPadding = 50;
	}

	public void getArticleDetail(int articleId) {
		String str;
		Article article;
		PageSplitter splitter;
		TextPaint textPaint;
		ArrayList<Object> list;
		ContentSplitter contentSplitter;
		ArrayList<ArticleDetailPage> articleDetailPageList;

		removeAllFlipperItem();
		article = Article.getArticle(articleId);
		list = new ArrayList<Object>();
		str = article.getBody();
		contentSplitter = new ContentSplitter(viewWidht);
		list  = (ArrayList<Object>) contentSplitter.split(str);

		// article content 추출 

		TextView textView = new TextView(context);
		textView.setPadding(viewPadding, 0, viewPadding, 0);
		textView.setLineSpacing((float)1.1, (float)1.1);
		textView.setWidth(viewWidht);
		textSize = NewsUpApp.getInstance().getTextSize();
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(textSize));
		ApplyFont(context,textView);

		textPaint = textView.getPaint();

		splitter = new PageSplitter(textPaint, viewHeight - 200 , viewWidht - (viewPadding * 2));

		// TODO: 시작페이지를 만들어야한다 (상세기사 첫화면)
		splitter.makePageList(list);
		articleDetailPageList = (ArrayList<ArticleDetailPage>) splitter.getPageList();

		//첫페이지 title, author
		
		LinearLayout view, layout;
		layout = (LinearLayout) inflater.inflate(R.layout.view_article_detail, null);
		view = (LinearLayout)(layout).findViewById(R.id.viewArticleDetail);
		
		textSize = R.dimen.text_title;
		textView = setTextView(context,300,textSize);
		textView.setPadding(viewPadding, 100, 0, 0);
		textView.setMaxLines(2);
		textView.setEllipsize(TextUtils.TruncateAt.END);

		textView.setText(article.getAuthor());
		view.addView(textView);
	
		
		
		textSize = R.dimen.text_author;
		textView = setTextView(context,100,textSize);
		textView.setPadding(viewPadding, 10, 0, 0);
	
	
		textView.setText(article.getAuthor());
		view.addView(textView);
		viewMaker(view, articleDetailPageList.get(0));
		setPageNumber(layout, 1, articleDetailPageList.size()+1);
		addView(layout);

		textSize = R.dimen.text_pagenumber;
		//중간페이지 
		for (int i = 1 ; i < articleDetailPageList.size() ; i++) {
			layout = (LinearLayout) inflater.inflate(R.layout.view_article_detail, null);
			view = (LinearLayout)(layout).findViewById(R.id.viewArticleDetail);
			viewMaker(view, articleDetailPageList.get(i));
			setPageNumber(layout, i + 1, articleDetailPageList.size()+1);

			addView(layout);
			
		}

		//마지막 페이지.
	}




	private void viewMaker(LinearLayout view, ArticleDetailPage articleDetailPage) {
		ArrayList<Object> articleContent;

		Object object;

		articleContent = (ArrayList<Object>) articleDetailPage.getContent();


		for (int i = 0 ; i < articleContent.size() ; i++) {
			object = articleContent.get(i);

			// 이미지 처리
			if (object instanceof ImageInfo) {
				ImageInfo imageInfo;
				ImageView imageView;

				imageInfo = (ImageInfo) object;
				imageView = new ImageView(context);

				view.addView(imageView);

				imageView.getLayoutParams().height = imageInfo.getHeight() - 50;
				imageView.getLayoutParams().width = imageInfo.getWidth();
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

				ImageViewManager.loadImage(imageView, imageInfo.getURL());
			}
			// 텍스트 처리
			else {
				String text, save;
				TextView textView;
				Paint mPaint;
				int end;
				String[] textArr;

				text = (String) object;
				save = "";

				Log.e("text", text);
				Log.e("-----", "--------------");
				textView = new TextView(context);
				textView.setWidth(viewWidht);
				textView.setPadding(viewPadding, 0, viewPadding, 0);
				textView.setLineSpacing((float)1.1, (float)1.1);

				int textSize = NewsUpApp.getInstance().getTextSize();
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(textSize));
				ApplyFont(context,textView);

				mPaint = textView.getPaint();

				end = 0;
				textArr = text.split("\n");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() == 0)
						textArr[j] = " ";
					do {
						// 글자가 width 보다 넘어가는지 체크
						end = mPaint.breakText(textArr[j], true, viewWidht - (viewPadding * 2), null);
						if (end > 0) {
							// 자른 문자열을 문자열 배열에 담아 놓는다.
							save += textArr[j].substring(0, end) + "\n";
							// 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
							textArr[j] = textArr[j].substring(end);
						}
					} while (end > 0);
				}

				textView.setText(save);
				view.addView(textView);
			}
		}
		
		
	}
	
	
	private void setPageNumber(LinearLayout layout, int pageNumber, int maxPageNumber){
		TextView textView =  (TextView)(layout).findViewById(R.id.viewArticleBottom);
		textView.setText(pageNumber+"/" + maxPageNumber);
		
	}
	
	private TextView setTextView(Context context,int height,int textSize){
		
		TextView textView = new TextView(context);
		textView.setWidth(viewWidht);
		textView.setHeight(height);
		textView.setLineSpacing((float)1.1, (float)1.1);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(textSize));
		ApplyFont(context,textView);

		return textView;
	}


	private void ApplyFont(Context context, TextView tv){
		Typeface face = Typeface.createFromAsset(context.getAssets(),"NanumGothic.ttf.mp3");
		tv.setTypeface(face);
	}

	private void ApplyFont(Context context, TextPaint tv){
		Typeface face = Typeface.createFromAsset(context.getAssets(),"NanumGothic.ttf.mp3");
		tv.setTypeface(face);
	}

	@Override
	public void outArticleDetail() {
		setReadTime();
		if(Network.isNetworkState(context)){
			Network.getInstance().updateUserLog(articleReadInfo);
		}
	}

	@Override
	public boolean upDownSwipe(int increase){
		int checkIndex = currentChildIndex + increase;
		if (checkIndex >= getChildChount() || checkIndex < minChildIndex) {
			return false;
		}
		setReadTime();
		display(checkIndex);
		return true;
	}

	private void setReadTime() {
		int index = getChildChount() - (currentChildIndex + 1);
		articleReadInfo.setReadTime(index, getTimestamp() - pageReadStartTime);
		pageReadStartTime = getTimestamp();
	}

	@Override
	public void inArticleDetail(int articleId) {
		getArticleDetail(articleId);
		pageReadStartTime = getTimestamp();
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime, getChildChount());
		display(getChildChount() - 1);
	}

	public void changeTextSize(int articleId) {
		removeAllFlipperItem();
		getArticleDetail(articleId);
		pageReadStartTime = getTimestamp();
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime, getChildChount());
		display(getChildChount() - 1);
	}

	private int getTimestamp() {
		return (int)(System.currentTimeMillis() / 1000L);
	}


	public void setLayoutWidth(int width) {
		viewWidht = width;
	};
	public void setLayoutHeight(int height) {
		viewHeight = height;
	}
}

package manager;

import network.Network;
import ArticleReadInfo.ArticleReadInfo;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;
import application.NewsUpApp;

import com.android.volley.toolbox.NetworkImageView;
import com.example.flipview.R;

import database.Article;

public class ArticleDetailManager extends ArticleFlipViewManager {
	
	private ArticleReadInfo articleReadInfo;
	private int pageReadStartTime;
	
	public ArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
		super(context, flipper, offset);
	}

	private void addArticleDetail(int layoutId, String str) {
		View view = inflater.inflate(layoutId, null);
		TextView content = (TextView) view.findViewById(R.id.textView1);
		NetworkImageView image = (NetworkImageView) view.findViewById(R.id.imageView1);
		content.setText(Html.fromHtml(str));
		image.setImageUrl("http://tour.yp21.net/multi/uploadFile/20090729_1732.JPG", 
				NewsUpApp.getInstance().getImageLoader());
		addView(view);
	}
	
	// TODO : 상세 기사 검색
	public void getArticleDetail(int articleId) {
		 Article article = articleDBManager.selectArticle(articleId);
		 String contents = "[마이데일리 = 전원 기자] 가수 션, 배우 정혜영 부부가 10년 째 결혼기념일마다 ‘밥퍼’를 찾아 봉사와 기부를 실천해오며 귀감이 되고 있다. 션은 지난 8일 저녁 자신의 인스타그램을 통해 “결혼 10주년인 오늘 혜영이와 작년 결혼기념일 다음날부터 매일 만원씩 모은 365만원을 들고 ‘밥퍼’에 어르신들을 뵈러 찾아갔습니다”라고 밝혔다." 
				 + "이어 “결혼1주년 때 일년 동안 모은 365만원을 들고 처음으로 ‘밥퍼’에 찾아가서 드리고 하루 동안 봉사하고 돌아 오는 길에 차안에서 혜영이가 했던 말. ‘작은걸 드리지만 큰 행복을 가지고 돌아간다고’. 그렇게 밥퍼를 찾아간 게 올해로 10번째. 그 작은 하루 만원의 나눔이 씨앗이 되어 우리 가정에 여러 나눔의 열매로 큰 행복이 되어 진행 되고 있습니다”라고 전했다."
				 + "또 “하루가 지나 내일이 오늘이 되면 나의 오늘을 그렇게 또 살아갈 겁니다. 내일부터 내년 결혼기념일에 ‘밥퍼’에 드리고 가져올 큰 행복을 위해서 우리의 작은 나눔, 만원의 행복은 시작됩니다”라며 내년에도 결혼기념일에 맞춰 ‘밥퍼’ 기부와 봉사를 약속했다." 
				 + "션은 결혼 10주년 기부와 함께 9일 특별한 콘서트를 진행한다. 션은 제일모직과 함께 삼성전자 서초 사옥에서 개최하는 ‘THE WEDDING CONCERT’를 통해 200쌍(400명) 예비 부부를 대상으로 실제 행복한 결혼생활을 위해 어떤 마음가짐과 행동이 필요한지에 대한 강연을 할 예정이다. ";
		 try {
			 removeAllFlipperItem();
			 addArticleDetail(R.layout.article_detail_item, "" + article.getIdx());
			 addArticleDetail(R.layout.article_detail_item, contents);
			 addArticleDetail(R.layout.article_detail_item, contents);
			 addArticleDetail(R.layout.article_detail_item, contents);
			 addArticleDetail(R.layout.article_detail_item, contents);
			 addArticleDetail(R.layout.article_detail_item, contents);
			 maxChildIndex = getChildChount();
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	}
	
	@Override
	public void outArticleDetail() {
		setReadTime();
		
		if(Network.isNetworkStat(context)){
			Network.getInstance().updateUserLog(articleReadInfo);
		}
		
	}
	
	@Override
	public boolean upDownSwipe(int increase){
		int checkIndex = currentChildIndex + increase;
		if (checkIndex > maxChildIndex
				|| isMenuState()
				|| checkIndex < minChildIndex) {
			return false;
		}
		
		setReadTime();
		
		display(checkIndex);
		return true;
	}
	
	private void setReadTime() {
		int index = getChildChount() - currentChildIndex;
		articleReadInfo.setReadTime(index, getTimestamp() - pageReadStartTime);
		pageReadStartTime = getTimestamp();
	}
	
	@Override
	public void inArticleDetail(int articleId) {
		getArticleDetail(articleId);
		pageReadStartTime = getTimestamp();
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime, getChildChount());
		display(getChildChount());
	}
	
	private int getTimestamp() {
		return (int) System.currentTimeMillis() / 1000;
	}
}

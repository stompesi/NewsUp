package ArticleReadInfo;

import java.util.ArrayList;

public class ArticleReadInfo {
	private int id;
	private int startTime;
	private ArrayList<Integer> pagesReadTime;
	
	public ArticleReadInfo(int id, int startTime, int pageNumber) {
		this.id = id;
		this.startTime = startTime;
		pagesReadTime = new ArrayList<Integer>();
		for(int i = 0 ; i < pageNumber ; i++) {
			pagesReadTime.add(0);
		}
	}
	
	public void setReadTime(int index, int readTime) {
		int totalPageReadTime = pagesReadTime.get(index) + readTime;
		pagesReadTime.set(index, totalPageReadTime);
	}
	
	public int getArticleId() {
		return id;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public ArrayList<Integer> getPagesReadTime() {
		return pagesReadTime;
	}
}

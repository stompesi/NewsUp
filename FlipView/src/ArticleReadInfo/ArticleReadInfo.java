package ArticleReadInfo;

import java.util.ArrayList;

public class ArticleReadInfo {
	private int id;
	private int startTime;
	private ArrayList<Integer> pagesReadTime;
	
	public ArticleReadInfo(int id, int startTime) {
		this.id = id;
		this.startTime = startTime;
		pagesReadTime = new ArrayList<Integer>();
	}
	
	public void setReadTime(int index, int readTime) {
		int totalPageReadTime = pagesReadTime.get(index) + readTime;
		pagesReadTime.set(index, totalPageReadTime);
	}
	
	public void addPage() {
		pagesReadTime.add(0);
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

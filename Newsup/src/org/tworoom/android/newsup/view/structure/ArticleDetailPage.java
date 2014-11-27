package org.tworoom.android.newsup.view.structure;

import java.util.ArrayList;
import java.util.List;

public class ArticleDetailPage {
	ArrayList<Object> content; 
	
	public ArticleDetailPage() {
		this.content = new ArrayList<Object>();
	}
	
	public void add(Object object) {
		content.add(object);
	}
	
	public List<Object> getContent() {
		return content;
	}
	
	public boolean isEmpty() {
		return content.size() == 0;
	}
}

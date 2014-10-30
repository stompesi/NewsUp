package hc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.text.TextPaint;

public class Splitter {
	ArrayList<Object> result = new ArrayList<Object>();
	TextPaint textPaint;

	String string = new String();
	String totalString = new String();

	int availableWidth;

	// height 관련 정보
	int availableHeight;
	int currentHeight;
	int textLineHeight;
	Queue<Object> q = new LinkedList<Object>();
	List<ArticleDetailPage> articleDatailPageList= new ArrayList<ArticleDetailPage>();
	ArticleDetailPage articleDatailPage;
	
	public Splitter(TextPaint textPaint, int availableHeight, int availableWidth) {
		this.textPaint = textPaint;
		this.textLineHeight = (int) Math.ceil(textPaint.getFontMetrics(null));
		this.availableHeight = availableHeight;
		this.availableWidth = availableWidth;
	}

	public List<ArticleDetailPage> getList(ArrayList<Object> list) {
		
		currentHeight = availableHeight;
		articleDatailPage = new ArticleDetailPage();

		for (int i = 0; i < list.size() || !q.isEmpty(); i++) {
			Object object;
			if (!q.isEmpty()
					&& ((list.get(i - 1) != q.peek() || i == list.size()))) {
				object = q.poll();
				i--;
			} else {
				object = list.get(i);
			}

			// 이미지 처리
			if (object instanceof ImageInfo) {
				if (currentHeight > ((ImageInfo) object).getImage_height()) {
					if (!totalString.equals("")) {
						articleDatailPage.add(totalString);
//						result.add(totalString);
					}
					articleDatailPage.add(object);
//					result.add(object);
					currentHeight = currentHeight - ((ImageInfo) object).getImage_height();
				} else if (currentHeight > (int) (((ImageInfo) object)
						.getImage_height() * 0.6)) {
					if (!totalString.equals("")) {
//						result.add(totalString);
						articleDatailPage.add(totalString);
					}
					articleDatailPage.add(object);
//					result.add(object);
					currentHeight = currentHeight - (int) (((ImageInfo) object).getImage_height() * 0.6);
				} else {
					q.offer(object);
				}
				if (0 > currentHeight - textLineHeight) {
					currentHeight = availableHeight;
					articleDatailPageList.add(articleDatailPage);
					articleDatailPage = new ArticleDetailPage();
				}
			}
			// 텍스트 처리
			else {
				String text = (String) object;
				processText(text);
			}
		}
		
		if(articleDatailPage.getContent().size() != 0) {
			articleDatailPageList.add(articleDatailPage);
		}
		

		return articleDatailPageList;
	}

	private void processText(String text) {
		if (get(text)) {
			articleDatailPage.add(totalString);
			
//			result.add(totalString);
			currentHeight = availableHeight;
			totalString = "";
			articleDatailPageList.add(articleDatailPage);
			articleDatailPage = new ArticleDetailPage();
			

			if (!(string.equals(""))) {
				q.offer(string);
			}
		}
	}

	private boolean get(String text) {
		int end = 0;
		string = text;
		do {
			// 글자가 width 보다 넘어가는지 체크
			end = textPaint.breakText(string, true, availableWidth, null);
			if (end > 0) {
				// 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
				totalString += string.substring(0, end);
				string = string.substring(end);

				// 다음라인 높이 지정
				currentHeight = currentHeight - textLineHeight;
				if (0 > currentHeight - textLineHeight) {
					return true;
				}
			}
		} while (end > 0);
		// 여기변경해야함
		totalString += "\n\n";
		currentHeight = currentHeight - textLineHeight;
		return false;
	}
}

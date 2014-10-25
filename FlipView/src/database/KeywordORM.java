package database;

import android.content.Context;

import com.orm.SugarRecord;

public class KeywordORM extends SugarRecord<KeywordORM> {
	private String keyword;
	
	public KeywordORM(){}
	
	public KeywordORM(String keyword) {
		this.keyword = keyword;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}

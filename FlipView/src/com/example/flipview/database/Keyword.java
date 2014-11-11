package com.example.flipview.database;

import com.orm.SugarRecord;

public class Keyword extends SugarRecord<Keyword> {
	
	private String keyword;
	
	public Keyword(){}
	
	public Keyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getKeyword() { return keyword; }
	public void setKeyword(String keyword) { this.keyword = keyword; }
}

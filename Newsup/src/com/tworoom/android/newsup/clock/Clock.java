package com.tworoom.android.newsup.clock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Clock {
		
		private SimpleDateFormat CurDateFormat;
		private SimpleDateFormat CurTimeFormat;
		
		private String[] week = { "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
		
		public Clock() {
			
			// 시간 포맷 지정
			CurDateFormat = new SimpleDateFormat("MM월 dd일  ");
			CurTimeFormat = new SimpleDateFormat("hh : mm ");
		}
		
		public String getDate() {
			return CurDateFormat.format((new Date()));
		}
		
		public String getTime() {
			return CurTimeFormat.format((new Date()));
		}
		
		public String getAMPM() {
			return (new Date()).getHours() > 12 ? "PM" : "AM"; 
		}
		
		
		public String getWeek() {
			Calendar c = Calendar.getInstance();
			return week[c.get(Calendar.DAY_OF_WEEK) - 1];
		}
	}
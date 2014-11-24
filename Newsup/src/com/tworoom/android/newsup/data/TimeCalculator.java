package com.tworoom.android.newsup.data;

public class TimeCalculator {

	private final static int ONE_DAY_SECOND = 86400;
	private final static int ONE_HOUR_SECOND = 3600;
	private final static int ONE_MINUTE_SECOND = 60;
	
	private int currentTime, prevTime;
	
	private int differenceTime;
	public TimeCalculator(int prevTiem) {
		this.prevTime = prevTiem;
		this.currentTime = (int) (System.currentTimeMillis() / 1000L);
	}
	
	public String calculatorTimeDifference(){
		String differenceTimeString;
		
		differenceTime = currentTime - prevTime;
		
		if((differenceTime / ONE_DAY_SECOND) > 0) {
			differenceTimeString = (differenceTime / ONE_DAY_SECOND) + "일 전";
		} else if(((differenceTime % ONE_DAY_SECOND) / ONE_HOUR_SECOND) > 0) {
			differenceTimeString = ((differenceTime % ONE_DAY_SECOND) / ONE_HOUR_SECOND) + "시간 전";
		} else {
			differenceTimeString = ((differenceTime % ONE_HOUR_SECOND) / ONE_MINUTE_SECOND) + "분 전";
		}
		return differenceTimeString;
	}
}

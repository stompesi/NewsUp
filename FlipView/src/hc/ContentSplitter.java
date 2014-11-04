package hc;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ContentSplitter {
	private int width;
	public ContentSplitter(int width) {
		this.width = width;		
	}
	public List<Object> split(String str) {
		String[] midTemp, finalTemp, temp;
		ArrayList<Object> articleContentList = new ArrayList<Object>();
		
		temp  = str.split("<<IMAGESTART>>");
		
		for(int i = 0 ; i < temp.length ; i++) {
			if(temp[i].contains("<<IMAGEEND>>")) {
				midTemp = temp[i].split("<<IMAGEEND>>");
				if(midTemp.length == 1) {
					articleContentList.add(imageParser(midTemp[0].trim()));	
				}
				else {
					articleContentList.add(imageParser(midTemp[0].trim()));
					finalTemp = midTemp[1].split("\n\n");
					for(int j = 0 ; j < finalTemp.length ; j++) {
						if(finalTemp[j].trim().length() != 0) {
							articleContentList.add(finalTemp[j].trim());
						}
					}
				}
			} 
			else {
				midTemp = temp[i].split("\n\n");
				for(int j = 0 ; j < midTemp.length ; j++) {
					if(midTemp[j].trim().length() != 0) {
						articleContentList.add(midTemp[j].trim());
					}
				}
			}
		}
		return articleContentList;
	}

	private ImageInfo imageParser(String str) {
		ImageInfo imageInfo;
		String imageURL;
		int imageHeight;
		int imgaeWidth;
		double ratio;

		String[] imageResult = str.split(" ");

		imageURL = imageResult[0];
		
		Log.e("width", "" + width);
		Log.e("width", "");
		Log.e("Integer.parseInt(imageResult[1])", "" + Integer.parseInt(imageResult[1]));
		ratio = (double) width / Integer.parseInt(imageResult[1]);
		Log.e("ratio", "" + ratio);
		
		
		
		imgaeWidth = width;
		Log.e("(Integer.parseInt(imageResult[2])", "" + (Integer.parseInt(imageResult[2])));
		imageHeight = (int)(Integer.parseInt(imageResult[2]) * ratio);
		Log.e("(imageHeight", "" + imageHeight);

		imageInfo = new ImageInfo(imageURL, imgaeWidth, imageHeight);

		return imageInfo;
	}
}




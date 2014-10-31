package hc;

import java.util.ArrayList;
import java.util.List;

public class ContentSplitter {
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

		String[] imageResult = str.split(" ");

		imageURL = imageResult[0];
		imgaeWidth = Integer.parseInt(imageResult[1]);
		imageHeight = Integer.parseInt(imageResult[2]);

		imageInfo = new ImageInfo(imageURL, imgaeWidth, imageHeight);

		return imageInfo;
	}
}




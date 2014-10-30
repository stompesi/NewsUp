package hc;

import java.util.ArrayList;

public class FirstSplitter {// 서버에서 가져온 부분을 파싱한다.
	public ArrayList<Object> FirstSplitter(String str)// MTHL에서 이미지와 글을 분리해서 순서대로 list에 저장 한다.
	{
		ArrayList<Object> jb_list = new ArrayList<Object>();// 뉴스 기사를 순서 대로 저장 arraylist
		String temp[] = str.split("<<IMAGESTART>>");// 일단 이거 대로 자른다
		
		for(int i = 0 ; i < temp.length ; i++) {
			if(temp[i].contains("<<IMAGEEND>>")) {
				String temp_temp[] = temp[i].split("<<IMAGEEND>>");// 일단 이거 대로 자른다
				// 이미지 
				if(temp_temp.length == 1) {
					jb_list.add(ImageParser(temp_temp[0].trim()));	
				}
				//이미지 & 텍스트 
				else {
					jb_list.add(ImageParser(temp_temp[0].trim()));
					String temp_temp2[] = temp_temp[1].split("\n\n");
					for(int j = 0 ; j < temp_temp2.length ; j++) {
						if(temp_temp2[j].trim().length() != 0) {
							jb_list.add(temp_temp2[j].trim());
						}
					}
				}
			} 
			// 텍스트
			else {
				String temp_temp[] = temp[i].split("\n\n");
				for(int j = 0 ; j < temp_temp.length ; j++) {
					if(temp_temp[j].trim().length() != 0) {
						jb_list.add(temp_temp[j].trim());
					}
				}
			}
		}

		return jb_list;
	}

	private ImageInfo ImageParser(String str) {
		ImageInfo imageInfo;
		String Image_URL;
		int Image_Height;
		int Imgae_Width;

		String[] Image_result = str.split(" "); // 이미지 URL 이미지 넓이 이미지 높이 구분.

		Image_URL = Image_result[0];
		Imgae_Width = Integer.parseInt(Image_result[1]);
		Image_Height = Integer.parseInt(Image_result[2]);

		imageInfo = new ImageInfo(Image_URL, Imgae_Width, Image_Height);

		return imageInfo;
	}

}




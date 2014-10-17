package hc;

import java.util.ArrayList;

import android.util.Log;

public class FirstSplitter {//서버에서 가져온 부분을 파싱한다.
			
		

	
	
			public ArrayList<Object> FirstSplitter(String str)//MTHL에서 이미지와 글을 분리해서 순서대로 list에 저장 한다.
			{
				String result[];
				ArrayList<Object> PC_list = new ArrayList<Object>();//뉴스 기사를 순서 대로 저장  arraylist

				String temp[] = str.split("<<IMAGESTART>>");//일단 이거 대로 자른다

				temp[0]=temp[0].trim();//앞쪽에 기사 없다 즉 이미지가 기사 본문 제일 처음 부터 시작 할 경우 기사가 없는 0번째 배열을 없앤
				if(temp[0].length() !=0)
				{
					PC_list.add(temp[0]);//이미지 앞쪽이 기사 일수 있기 때문에 저장 
				}

				for(int i =0; i<temp.length; i++)
				{
					if(temp[i].contains("<<IMAGEEND>>"))
					{
						result = temp[i].split("<<IMAGEEND>>");
						for(int j = 0; j<result.length;j++)
						{
							result[j] = result[j].trim();
							if(j%2==0)
							{
								
								PC_list.add(ImageParser(result[j]));//이미지 객체를 넣어 준다.
		
							}
							else
							{
								PC_list.add(result[j]);
							}
							
							
//							PC_list.add(result[j]);

						}
					}
				}

				return PC_list;
			}
			
			
	private ImageInfo ImageParser(String str)
	{	
		ImageInfo imageInfo;
		String Image_URL;
		int Image_Height;
		int Imgae_Width;
		int Image_Start;
	
		
		String[] Image_result=str.split(" "); //이미지 URL 이미지 넓이 이미지 높이 구분.
		
	
		
		Image_URL = Image_result[0];
		Imgae_Width = Integer.parseInt(Image_result[1]);	
		Image_Height = Integer.parseInt(Image_result[2]);

		imageInfo = new ImageInfo(Image_URL, Imgae_Width, Image_Height,0);
		
		
		
		
		return imageInfo;
	}
			
			
			

}















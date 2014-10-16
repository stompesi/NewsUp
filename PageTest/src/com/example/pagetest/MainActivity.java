package com.example.pagetest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

	private int View_height = 1605;
	private int View_widht = 984;
	private ArrayList<Object> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		list = new ArrayList<Object>();
		FirstSplitter firstSplitter= new FirstSplitter();//신문 기사 처음 이미지랑 기사 구분 하는 객체.

		try {
			String str = readText("html.txt");
			list  = firstSplitter.FirstSplitter(str);//파싱된 걸 넣는다.
			StaticClass.result_lits= getResult_List(list);



			for(int i = 0; i<StaticClass.result_lits.size(); i++)
			{
				if( StaticClass.result_lits.get(i) instanceof ImageInfo)
				{
					Log.d("TAG1["+i+"]", ((ImageInfo) StaticClass.result_lits.get(i)).getImageURL()+"");
					Log.d("TAG1["+i+"]", ((ImageInfo) StaticClass.result_lits.get(i)).getImage_width()+"");
					Log.d("TAG1["+i+"]", ((ImageInfo) StaticClass.result_lits.get(i)).getImage_height()+"");
					Log.d("TAG1["+i+"]", ((ImageInfo) StaticClass.result_lits.get(i)).getImage_start()+"");

				}else if( StaticClass.result_lits.get(i) instanceof String)
				{
					Log.d("TAG1["+i+"]", ((String) StaticClass.result_lits.get(i)).length()+":"+((String) StaticClass.result_lits.get(i)));
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//읽는

	}

	private ArrayList<Object> getResult_List(ArrayList<Object> list)
	{
		ArrayList<Object> result_list = new ArrayList<Object>();//결과 값을 담은 arraylist
		int ExtrTextHeight = 0; //남는 여분의 텍스트 뷰(텍스트가 길어서 다음으로 넘어 갔을 때 넘어간 TextView의 높이)
		int Image_flag = 0; //텍스트 앞에 이미지가 있는지 없는지 판단.
		int Text_flag =0; //이미지 앞에 텍스트가 있는지 없는지 판단.
		int Text_hight = 0; //텍스트 높이.
		String Temp_text,Temp_text1;
		PageSplitter pageSplitter;//pageSpliter선언.
		TextViewHeight textViewHeight;
		TextPaint textPaint = new TextPaint();//pagespliter에 넘겨줄 그림판.
		textPaint.setTextSize(getResources().getDimension(R.dimen.text_size));//textsize설정.
		Log.d("TAG", "시작");
		for(int i = 0; i<list.size(); i++)
		{
			if( list.get(i) instanceof ImageInfo)//이미지 판별 
			{
				if(Text_flag == 1)//ImagView앞에 위에서 짤린 TextView가 있을경우(한페이지가 TextView부터 시작하는 경우)
				{
					//전체 뷰 높이에서 Extra 높이 뺐을때 뷰 높이가 원래 높이의 2/3높이보다 작으면 그림 다음으로 넘겨 
					//2/3보다 크면 리사이즈 해서 맞춰.
					int Image_Start = View_height - ExtrTextHeight;//전체 뷰에서 앞에 TextView 높이를 빼고 뺀 좌표를 이미지의 시작점으로 설정.
					((ImageInfo)list.get(i)).setImage_start(Image_Start);
					Log.d("TAGImageStart",Image_Start+":"+ ((ImageInfo)list.get(i)).getImage_start()+"");
					result_list.add(list.get(i));//앞에 TextView가 있을 때 시작 점 위치를 바꿔서 저장.
					Text_flag = 0; 


				}
				else  //한페이지가 이미지로 시작 하는 경우.
				{
					Text_hight = View_height -((ImageInfo)list.get(i)).getImage_height();//ImageView 밑에 TextView의 크기를 구한다.
					Log.d("TAG_Image", Text_hight+"="+View_height+"-"+((ImageInfo)list.get(i)).getImage_height());
					result_list.add(list.get(i));//현재 이미지를 결과 리스트에 저장.
					Image_flag = 1; 
				}

			}else if( list.get(i) instanceof String)//String 판별 
			{
				Log.d("TAG", "String들어");
				//				((String)list.get(i)).replaceAll("\n", "");
				if(Image_flag ==1)// TextView앞에가 이미지가 있는 경우.
				{	
					Log.d("TAG", "이미지가 앞일때");
					pageSplitter = new PageSplitter(View_widht, Text_hight,1,0);//이미지를 자르고 남은 높이에 들어갈 수 있는 글자를 리턴.
					pageSplitter.append(((String) list.get(i)), textPaint);
					Temp_text1= pageSplitter.getPages().get(0).toString();// TextView에 들어갈 글자들.

					result_list.add(Temp_text1);//이미지 뷰 뒤에 남은 Textview크기에 맞는 글자를 저장.
					//앞에 이미지 제외 하고 여기 까지면 한 레리아웃에 이미지 + textView가 꽉찬 형태가 만들어짐.
					pageSplitter = null;//pageSpliter 객체 삭제.

					if(((String)list.get(i)).length() - Temp_text1.length() > 0 )//이미지 밑에 글자가 짤렸때 남은 글자를 처리 하는 로직.
					{
						Log.d("TAG_Temp_text1", Temp_text1);

						Temp_text1 = ((String) list.get(i)).replace("\n", "").substring(Temp_text1.length());//해당 배열에서 TextView에 들어갈 글자 빼고 나머지.
						list.remove(i);//해당 배열 삭제.
						list.add(i,Temp_text1);
						Log.d("TAG_list", ((String) list.get(i)));


						pageSplitter = new PageSplitter(View_widht, View_height, 1, 0);//다음 layout은 뷰가 전체로 가정할때 처리 하는 조건.(넓이 높이를 뷰에 맞춘다)
						pageSplitter.append(((String) list.get(i)), textPaint);

						textViewHeight = new TextViewHeight(View_widht, View_height, 1, 0);
						Log.d("TAG Arraylist", pageSplitter.getPages().size()+"");

						int pagelength = pageSplitter.getPages().size();
						if(pagelength==1)
						{

							ExtrTextHeight = textViewHeight.getTextheight(pageSplitter.getPages().get(0).toString(), textPaint);
							if(ExtrTextHeight<View_height/2)
							{
								Log.d("TAG", "이미지 플래그 보냄"+":"+ExtrTextHeight);
								Text_flag = 1; 
							}
							//현재 높이를 측정 할 수 있는 함수가 있어서 만약 높이가 절반 이상이 넘으면 Text_flag = 1 날리지 말고 
							//그 이하면 Text_flag = 1날려 
							Log.d("TAG 앞에 이미지 있고 첫 페이지", pageSplitter.getPages().get(0).toString());

						}else if(pagelength>1)
						{
							for(int j = 0; j<pageSplitter.getPages().size(); j++)
							{
								Log.d("TAG 앞에 이미지 있고 전체 페이지 ",pageSplitter.getPages().get(j).length()+":"+pageSplitter.getPages().get(j).toString());
								result_list.add(pageSplitter.getPages().get(j).toString());//뷰가 layout하나가 한 페이지 라고 가정할때 페이지 단위로 결과 리스트에 저장.

							}
							ExtrTextHeight = textViewHeight.getTextheight(pageSplitter.getPages().get(pagelength-1).toString(), textPaint);
							if(ExtrTextHeight<View_height/2)
							{
								Log.d("TAG", "이미지 플래그 보냄"+":"+ExtrTextHeight);
								Text_flag = 1; 
							}


							//현 높이를 측정 할 수 있는 함수가 있어서 마지막 글자의 높이가 만약 높이가 절반 이상이 넘으면 Text_flag = 1 날리지 말고 
							//그 이하면 Text_flag = 1날려 
						}
					}

					Image_flag = 0;
				}
				else//앞에 이미지가 아닐경우. 첫 페이지가 String로 시작 하는 경우.
				{
					Log.d("TAG", "앞에 이미지가 아닐경우");
					pageSplitter = new PageSplitter(View_widht, View_height, 1, 0);
					pageSplitter.append(((String) list.get(i)), textPaint);
					Log.d("TAG", pageSplitter.getPages().size()+"");
					textViewHeight = new TextViewHeight(View_widht, View_height, 1, 0);



					int pagelength = pageSplitter.getPages().size();
					if(pagelength==1)
					{

						ExtrTextHeight = textViewHeight.getTextheight(pageSplitter.getPages().get(0).toString(), textPaint);
						if(ExtrTextHeight<View_height/2)
						{
							Log.d("TAG", "이미지 플래그 보냄"+":"+ExtrTextHeight);
							Text_flag = 1; 
						}
						//현재 높이를 측정 할 수 있는 함수가 있어서 만약 높이가 절반 이상이 넘으면 Text_flag = 1 날리지 말고 
						//그 이하면 Text_flag = 1날려 

						Log.d("TAG 스트링 첫 페이지", pageSplitter.getPages().get(0).toString());

					}else if(pagelength>1)
					{
						for(int j = 0; j<pageSplitter.getPages().size(); j++)
						{
							Log.d("TAG 스트링 전체 페이지 ",pageSplitter.getPages().get(j).length()+":"+pageSplitter.getPages().get(j).toString());
							result_list.add(pageSplitter.getPages().get(j).toString());//뷰가 layout하나가 한 페이지 라고 가정할때 페이지 단위로 결과 리스트에 저장.

						}
						ExtrTextHeight = textViewHeight.getTextheight(pageSplitter.getPages().get(pagelength-1).toString(), textPaint);
						if(ExtrTextHeight<View_height/2)
						{
							Log.d("TAG", "이미지 플래그 보냄"+":"+ExtrTextHeight);
							Text_flag = 1; 
						}


						//현 높이를 측정 할 수 있는 함수가 있어서 마지막 글자의 높이가 만약 높이가 절반 이상이 넘으면 Text_flag = 1 날리지 말고 
						//그 이하면 Text_flag = 1날려 
					}
				}


			} 
		}

		return result_list;
	}








	private int getHeight(Context context, CharSequence text,  int deviceWidth,int padding) {
		TextView textView = new TextView(context);
		textView.setPadding(padding,0,padding,padding);
		//        textView.setTypeface(typeface);
		textView.setText(text, TextView.BufferType.SPANNABLE);
		textView.setTextSize(getResources().getDimension(R.dimen.text_size));
		int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
		int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		textView.measure(widthMeasureSpec, heightMeasureSpec);
		return textView.getMeasuredHeight();
	}

	private String readText(String file) throws IOException {
		InputStream is = getAssets().open(file);

		int size = is.available();
		byte[] buffer = new byte[size];
		is.read(buffer);
		is.close();

		String text = new String(buffer);

		return text;
	}

}

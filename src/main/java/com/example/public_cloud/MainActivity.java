package com.example.public_cloud;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.InputStream;

public class MainActivity extends Activity {

	private TextView tv;
	private Button public_bt, car_num, car_allow;
	private final String imgfilepath = Environment.getExternalStorageDirectory() + "/myImage/detect.jpg";
	final File file = new File(imgfilepath); // 上传的待识别图片
	private long start_time,end_time;

	private final String key = "LqvbFtyCRaRq31HqbWN9c6"; // 用户ocrKey
	private final String secret = "328bca7f82f04fe9a34171049659ca4f"; // 用户ocrSecret
	private final String typeId = "19"; // 证件类型(例如:二代证正面为"2"。详见文档说明)
//	private final String format = "xml";
	 private String format = "json"; //(返回的格式可以是xml，也可以是json)
	private final String url = "http://www.netocr.com/api/recog.do"; // http接口调用地址
	static String result = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.text);
		public_bt = (Button) findViewById(R.id.public_bt);
		car_num = (Button) findViewById(R.id.car_num);
		car_num.setVisibility(View.INVISIBLE);// 初始状态没有进行车牌识别，不显示车牌号
		car_allow = (Button)findViewById(R.id.car_allow);
		car_allow.setVisibility(View.INVISIBLE);// 初始状态没有进行车牌识别，不显示车牌号

		public_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 判断目标文件是否存在
				if (!file.exists()) {
					Toast.makeText(getApplicationContext(), "目录文件不存在", Toast.LENGTH_LONG).show();
					return;
				}
				tv.setText("正在识别....");
				new Thread() {
					public void run() {
						start_time = System.currentTimeMillis();
						String result = doPost(url, file, key, secret, typeId, format);
						end_time = System.currentTimeMillis()-start_time;
						Message mes = new Message();
						mes.obj = result;
						mes.what = 0;
						handler.sendMessage(mes);

					};
				}.start();
			}
		});
		car_num.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

//			car_num.setVisibility(View.VISIBLE);
//				car_num.setText(  );
			}
		});
		car_allow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				car_allow.setVisibility(View.VISIBLE);
				MainActivity.this.finish();
			}
		});

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					String result = (String) msg.obj;
					Log.d("TIEJIANG", "RESULT FROM SERVER= " + result);
					tv.setText(result+"\n"+"识别时间："+end_time+"ms"+"\n");
					car_num.setVisibility(View.VISIBLE);
					car_allow.setVisibility(View.VISIBLE);
					car_num.setText("车牌号码为： " + getCarNumber(result));
					break;
				case 1:
					Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
					tv.setText("识别失败，请重新处理 ！ ");
					break;
			}

		}
	};

	public String getCarNumber(String json_str){
		String jsonString = json_str;
		if (jsonString.length() <  200){
			return "车牌解析失败";
		}else{
			String tempSplit = jsonString.substring(88, 147);
			String[] arrayf = tempSplit.split("\"");

			Log.d("TIEJIANG", "车牌号码： " + arrayf[8]);
			return arrayf[8];
		}
	}

	public final String doPost(String url, File file, String key, String secret, String typeId, String format) {
		String result = "";
		try {

			HttpClient client = new DefaultHttpClient(); // 1.创建httpclient对象
			HttpPost post = new HttpPost(url); // 2.通过url创建post方法

			if ("json".equalsIgnoreCase(format)) {
				post.setHeader("accept", "application/json");
			} else if ("xml".equalsIgnoreCase(format) || "".equalsIgnoreCase(format)) {
				post.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			}

			// ***************************************<向post方法中封装实体>************************************//3.向post方法中封装实体
			/*
			 * post方式实现文件上传则需要使用multipart/form-data类型表单，httpclient4.
			 * 3以后需要使用MultipartEntityBuilder来封装 对应的html页面表单： <form name="input"
			 * action="http://netocr.com/api/recog.do" method="post"
			 * enctype="multipart/form-data"> 请选择要上传的文件<input type="file"
			 * NAME="file"><br /> key:<input type="text" name="key"
			 * value="W8Nh5AU2xsTYzaduwkzEuc" /> <br /> secret:<input
			 * type="text" name="secret"
			 * value="9646d012210a4ba48b3ba16737d6f69f" /><br /> typeId:<input
			 * type="text" name="typeId" value="2"/><br /> format:<input
			 * type="text" name="format" value=""/><br /> <input type="submit"
			 * value="提交"> </form>
			 */

			MultipartEntityBuilder builder = MultipartEntityBuilder.create(); // 实例化实体构造器
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE); // 设置浏览器兼容模式

			builder.addPart("file", new FileBody(file)); // 添加"file"字段及其值；此处注意字段名称必须是"file"
			builder.addPart("key", new StringBody(key, ContentType.create("text/plain", Consts.UTF_8))); // 添加"key"字段及其值
			builder.addPart("secret", new StringBody(secret, ContentType.create("text/plain", Consts.UTF_8))); // 添加"secret"字段及其值
			builder.addPart("typeId", new StringBody(typeId, ContentType.create("text/plain", Consts.UTF_8))); // 添加"typeId"字段及其值
			builder.addPart("format", new StringBody(format, ContentType.create("text/plain", Consts.UTF_8))); // 添加"format"字段及其值

			HttpEntity reqEntity = builder.setCharset(CharsetUtils.get("UTF-8")).build(); // 设置请求的编码格式，并构造实体

			post.setEntity(reqEntity);
			// **************************************</向post方法中封装实体>************************************

			HttpResponse response = client.execute(post); // 4.执行post方法，返回HttpResponse的对象
			if (response.getStatusLine().getStatusCode() == 200) { // 5.如果返回结果状态码为200，则读取响应实体response对象的实体内容，并封装成String对象返回
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
				Message mes = new Message();
				mes.obj = result;
				mes.what = 2;
				handler.sendMessage(mes);
			} else {
				System.out.println("服务器返回异常");
				Toast.makeText(getApplicationContext(), "服务器返回异常", Toast.LENGTH_LONG).show();
				Message mes = new Message();
				mes.obj = "服务器返回异常";
				mes.what = 1;
				handler.sendMessage(mes);
			}

			try {
				HttpEntity e = response.getEntity(); // 6.关闭资源
				if (e != null) {
					InputStream instream = e.getContent();
					instream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				((InputStream) response).close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		System.out.println("result:"+result);
		return result; // 7.返回识别结果

	}
}

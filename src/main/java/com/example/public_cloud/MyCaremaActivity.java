package com.example.public_cloud;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class MyCaremaActivity extends Activity {

	String mFilePath = "/sdcard/myImage/";
	String fileName = "/sdcard/myImage/detect.jpg";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_page);

		Button button = (Button) findViewById(R.id.button);

		File file = new File(fileName);
		file.mkdirs();// 创建文件夹

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri uri = Uri.fromFile(file);
		//指定存储路径，这样就可以保存原图了
		intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
		startActivityForResult(intent, 1);

//		button.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				startActivityForResult(intent, 1);
//			}
//		});
	}
	public void parameters(Camera camera) {
		List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
		List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
		Camera.Size psize;
		for (int i = 0; i < pictureSizes.size(); i++) {
			psize = pictureSizes.get(i);
			Log.d("pictureSize",psize.width+" x "+psize.height);
		}
		for (int i = 0; i < previewSizes.size(); i++) {
			psize = previewSizes.get(i);
			Log.d("previewSize",psize.width+" x "+psize.height);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {

			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.v("TestFile", "SD card is not avaiable/writeable right now.");
				return;
			}
			String filePath=fileName;
//			Bitmap bitmap= BitmapFactory.decodeFile(filePath,getBitmapOption(2));//将图片的长和宽缩小味原来的1/2
			Bitmap bitmap= BitmapFactory.decodeFile(filePath,null);
//			Bundle bundle = data.getExtras();
//			Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
//			FileOutputStream b = null;
//
//			try {
//				b = new FileOutputStream(fileName);
//				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					b.flush();
//					b.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}

			((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);// 将图片显示在ImageView里

			//进入识别车牌界面  准备上传图片
			Intent intent = new Intent(MyCaremaActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
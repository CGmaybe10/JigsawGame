package com.example.cutimage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {
	GridView gridView;
	Bitmap bitmap;
	Bitmap cutBitmap;
	List<Bitmap> btmList;
	List<Bitmap> randomBtmList;
	CutAdapter cutAdapter;
	int btmWidth, btmHeight;
	int viewWidth, viewHeight;

	Bitmap exchangeBtm;
	Bitmap nullBtm;
	int locationNull;
	int locationClick;

	Random random;
	boolean flagLayout;

	int shatterNum = 4;
	int shatterTotalNum = shatterNum * shatterNum;
	int clickRow, locationNullRow;

	// 动画
	Animation itemAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		flagLayout = true;

		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			// arg0指的是GridView，arg1指的是点击的那个item，arg2指的是adapter里的位置，arg3指的是item在GridView里的位置
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int diff = arg2 - locationNull;
				locationClick = Math.abs(diff);
				clickRow = arg2 / shatterNum;
				locationNullRow = locationNull / shatterNum;
				
				View viewNull = (View) arg0.getChildAt(locationNull);
				if (((locationClick == 1) && (clickRow == locationNullRow))
						|| (locationClick == shatterNum)) {
					if (diff == 1) {
						// 切换点击的Item的图片
						arg1.setAnimation(getItemTranslateAnimation(
								-arg1.getWidth(), 0, 0, 0));
						// 切换空位的图片
						viewNull.setAnimation(getItemTranslateAnimation(
								arg1.getWidth(), 0, 0, 0));
					} else if (diff == -1) {
						arg1.setAnimation(getItemTranslateAnimation(
								arg1.getWidth(), 0, 0, 0));
						viewNull.setAnimation(getItemTranslateAnimation(
								-arg1.getWidth(), 0, 0, 0));
					} else if (diff == shatterNum) {
						arg1.setAnimation(getItemTranslateAnimation(0, 0,
								-arg1.getHeight(), 0));
						viewNull.setAnimation(getItemTranslateAnimation(0, 0,
								arg1.getHeight(), 0));
					} else if (diff == -shatterNum) {
						arg1.setAnimation(getItemTranslateAnimation(0, 0,
								arg1.getHeight(), 0));
						viewNull.setAnimation(getItemTranslateAnimation(0, 0,
								-arg1.getHeight(), 0));
					}

					exchangeBtm = randomBtmList.get(arg2);
					randomBtmList.set(arg2, nullBtm);
					randomBtmList.set(locationNull, exchangeBtm);
					locationNull = arg2;

					cutAdapter.notifyDataSetChanged();
				}

			}
		});

		setImageview();
	}

	public void setImageview() {
		/*
		 * 设置imageView
		 */
		Resources res = getResources();
		InputStream inStrem = res.openRawResource(R.drawable.welcome);
		bitmap = BitmapFactory.decodeStream(inStrem);
		btmWidth = bitmap.getWidth();
		btmHeight = bitmap.getHeight();

		int count = 0;
		int x = 0;
		int y = 0;
		int width = bitmap.getWidth() / shatterNum;
		int height = bitmap.getHeight() / shatterNum;
		btmList = new ArrayList<Bitmap>();
		for (int i = 0; i < shatterNum; i++) {
			y = i * height;
			for (int j = 0; j < shatterNum; j++) {
				x = j * width;
				cutBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
				count++;
				if (count < shatterTotalNum) {
					btmList.add(cutBitmap);
				} else {
					// 设置空位图片的透明度
					int[] argb = new int[cutBitmap.getWidth()
							* cutBitmap.getHeight()];
					cutBitmap.getPixels(argb, 0, cutBitmap.getWidth(), 0, 0,
							cutBitmap.getWidth(), cutBitmap.getHeight());// 获得图片的ARGB值
					int alpha = 25;
					alpha = alpha * 255 / 100;
					for (int num = 0; num < argb.length; num++) {
						argb[num] = (alpha << 24) | (argb[num] & 0x00FFFFFF);
					}
					cutBitmap = Bitmap.createBitmap(argb, cutBitmap.getWidth(),
							cutBitmap.getHeight(), Config.ARGB_8888);

					nullBtm = cutBitmap;
					btmList.add(cutBitmap);
				}
			}
		}

		randomImagesLocation(btmList);
	}

	// 设置图片的随机位置
	public void randomImagesLocation(List<Bitmap> btmList) {
		random = new Random();
		randomBtmList = new ArrayList<Bitmap>();
		int number;
		int count = 0;
		Set<Integer> randomInt = new HashSet<Integer>();
		while (true) {
			number = random.nextInt(shatterTotalNum);
			if (randomInt.add(number)) {
				randomBtmList.add(btmList.get(number));
				if (number == (shatterTotalNum - 1))
					locationNull = count;
				count++;
			}
			if (randomInt.size() == shatterTotalNum)
				break;
		}
	}

	/*
	 * get screen width, height and set GridView ImageView
	 */
	public void getScreenDensity() {
		// 获取view的区域的大小
		Rect outRect = new Rect();
		getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(
				outRect);
		viewWidth = outRect.width();
		viewHeight = outRect.height() - 100;

		LayoutParams layoutParams = (LayoutParams) gridView.getLayoutParams();
		if ((viewWidth / viewHeight) >= (btmWidth / btmHeight)) {
			layoutParams.width = (viewHeight * btmWidth / btmHeight);
			cutAdapter = new CutAdapter(this, randomBtmList,
					(viewHeight / shatterNum));
		} else {
			layoutParams.width = viewWidth;
			cutAdapter = new CutAdapter(this, randomBtmList, (viewWidth
					* btmHeight / btmWidth / shatterNum));
		}
		gridView.setLayoutParams(layoutParams);
		gridView.setNumColumns(shatterNum);
		gridView.setAdapter(cutAdapter);

		gridView.setLayoutAnimation(getAnimationController());
	}

	// GridView的布局动画
	public LayoutAnimationController getAnimationController() {
		LayoutAnimationController controller;
		Animation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);// 从0.5倍放大到1倍
		anim.setDuration(500);
		controller = new LayoutAnimationController(anim, 0.1f);
		controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
		return controller;
	}

	// 点击item的缩放动画
	public Animation getItemScaleAnimation() {
		itemAnim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);// 从0.5倍放大到1倍
		itemAnim.setDuration(200);
		return itemAnim;
	}

	// 向点击item的滑动动画
	public Animation getItemTranslateAnimation(int fromX, int toX, int fromY,
			int toY) {
		itemAnim = new TranslateAnimation(fromX, toX, fromY, toY);
		itemAnim.setDuration(200);
		return itemAnim;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (flagLayout) {
			getScreenDensity();
		}
		flagLayout = false;
	}
}

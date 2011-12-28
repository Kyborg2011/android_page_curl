/*
   Copyright 2011 Harri Smått

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package fi.harism.curl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */
public class CurlActivity extends Activity {

	private CurlView mCurlView;
	private TextView[] mTextViewIds = new TextView[3];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		for (int i=0; i<3; i++) {
			TextView text = new TextView(this);
			text.setText("English is a West Germanic language " +
					"that arose in the Anglo-Saxon kingdoms of " +
					"England and spread into what was to become " +
					"south-east Scotland under the influence of " +
					"the Anglian medieval kingdom of Northumbria." +
					" Following the extensive influence of Great " +
					"Britain and the United Kingdom from the 18th" +
					" century, via the British Empire, and of the" +
					" United States since the mid-20th century,");
			mTextViewIds[i] = text;
		}

		int index = 0;
		if (getLastNonConfigurationInstance() != null) {
			index = (Integer) getLastNonConfigurationInstance();
		}
		mCurlView = (CurlView) findViewById(R.id.textCurl);
		mCurlView.setBitmapProvider(new BitmapProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index);
		mCurlView.setBackgroundColor(0xFF202830);
		mCurlView.setUpdateMethod(new UpdatePageNumber()); // Added a page counter
		


		// This is something somewhat experimental. Before uncommenting next
		// line, please see method comments in CurlView.
		// mCurlView.setEnableTouchPressure(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		mCurlView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurlView.onResume();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mCurlView.getCurrentIndex();
	}

	/**
	 * Bitmap provider. Added new BitmapProvider with Text bitmaps.
	 */
    public class BitmapProvider implements CurlView.BitmapProvider {

        @Override
        public Bitmap getBitmap(int width, int height, int index) {
            Bitmap renderSurface = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(renderSurface);
            Paint paint = new Paint();
            paint.setTextSize(35);
            // maybe color the bacground..
            canvas.drawPaint(paint);
            // Setup a textview like you normally would with your activity context
            TextView tv = mTextViewIds[index];
            // setup text
            // you have to enable setDrawingCacheEnabled, or the getDrawingCache will return null
            tv.setDrawingCacheEnabled(true);
            // we need to setup how big the view should be..which is exactly as big as the canvas
            tv.measure(View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));
            // assign the layout values to the textview
            tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
            // draw the bitmap from the drawingcache to the canvas
            canvas.drawBitmap(tv.getDrawingCache(), 0, 0, paint);
            // disable drawing cache
            tv.setDrawingCacheEnabled(false);
            return renderSurface;
        }

        @Override
        public int getBitmapCount() {
            return mTextViewIds.length;
        }
    }
    private class UpdatePageNumber implements CurlView.UpdatePageNumber {
        public void updateNumber() {
            reloadPages.sendEmptyMessage(0);

        }
    }
    private Handler reloadPages = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView pages = (TextView) findViewById(R.id.pages);
            pages.setText("Страница " + mCurlView.getCurrentIndex() + " из " + mTextViewIds.length);
        }

    };

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
				mCurlView.setMargins(.1f, .05f, .1f, .05f);
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
				mCurlView.setMargins(.1f, .1f, .1f, .1f);
			}
		}
	}

}
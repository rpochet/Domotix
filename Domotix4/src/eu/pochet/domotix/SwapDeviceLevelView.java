package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.SwapDevice;

public class SwapDeviceLevelView extends LevelView {
	
	private static final String TAG = SwapDeviceLevelView.class.getName();

	private Bitmap mSwapDeviceBitmap;

	private Bitmap mTempBitmap;

	public SwapDeviceLevelView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
	}

	public SwapDeviceLevelView(Context context, Level level) {
		super(context, level);
	}

	public float getSwapDeviceX(SwapDevice swapDevice) {
		return swapDevice.getRoom().getX() + levelX + swapDevice.getX();
	}

	public float getSwapDeviceY(SwapDevice swapDevice) {
		return swapDevice.getRoom().getY() + levelY + swapDevice.getY();
	}

	public void init() {
		android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
		options.inDither = true;
		options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
		mSwapDeviceBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.swapdevice, options);
		mTempBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.swapdevice_temp, options);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (SwapDevice swapDevice : level.getSwapDevices()) {
			float swapDeviceX = getSwapDeviceX(swapDevice);
			float swapDeviceY = getSwapDeviceY(swapDevice);
			//if (swapDevice.getStatus() == 0) {
				canvas.drawBitmap(mSwapDeviceBitmap, 
						Constants.CARD_OFFSET_X + swapDeviceX * getRatioX(), 
						Constants.CARD_OFFSET_Y + swapDeviceY * getRatioY(), 
						null);
			/*} else {
				canvas.drawBitmap(mTempBitmap, 
						Constants.CARD_OFFSET_X + swapDeviceX * getRatioX(), 
						Constants.CARD_OFFSET_Y + swapDeviceY * getRatioY(), 
						null);
			}*/
		}
	}
}

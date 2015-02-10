package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Room;
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
		for(Room room : level.getRooms()) {
			for (SwapDevice swapDevice : room.getSwapDevices()) {
				float swapDeviceX = swapDevice.getLocation().getAbsoluteX();
				float swapDeviceY = swapDevice.getLocation().getAbsoluteY();
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
}

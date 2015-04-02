package eu.pochet.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class TabListener implements android.app.ActionBar.TabListener {
	
	private final Activity mActivity;

	private final Class mClass;

	private Fragment mFragment;

	private final String mTag;

	private Bundle mArguments = null;

	public TabListener(Activity activity, String s, Class class1) {
		mActivity = activity;
		mTag = s;
		mClass = class1;
	}

	public TabListener(Activity activity, String s, Class class1, Bundle arguments) {
		this(activity, s, class1);
		mArguments = arguments;
	}

	public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		if (mFragment == null) {
			mFragment = Fragment.instantiate(mActivity, mClass.getName());
			if(mArguments != null) {
				mFragment.setArguments(mArguments);
			}
			fragmentTransaction.add(0x1020002, mFragment, mTag);
			return;
		} else {
			fragmentTransaction.attach(mFragment);
			return;
		}
	}

	public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		if (mFragment != null) {
			fragmentTransaction.detach(mFragment);
		}
	}
	
}
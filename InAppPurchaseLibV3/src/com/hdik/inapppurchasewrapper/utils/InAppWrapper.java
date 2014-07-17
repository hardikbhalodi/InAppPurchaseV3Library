package com.hdik.inapppurchasewrapper.utils;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;

/**
 * @author Hardik A Bhalodi
 * 
 */
public class InAppWrapper {

	IabHelper mHelper;
	private final String TAG = "InAppWrapper";
	private Activity activity;
	private InAppListener inAppListener;

	public InAppWrapper(Activity activity, InAppListener inAppListener) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.inAppListener = inAppListener;
		try {
			if (inAppListener == null)
				// Log.e(TAG,
				// "Null Not Allowed, You Must Pass a Reference of InAppListner in Constructor as second parameter");

				throw new Exception(
						"Null Not Allowed, You Must Pass a Reference of InAppListner in Constructor as second parameter");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	public void Init(final String base64EncodedPublicKey) {
		// Some sanity checks to see if the developer (that's you!) really
		// followed the
		// instructions to run this sample (don't put these checks on your app!)
		// if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
		// throw new RuntimeException(
		// "Please put your app's public key in MainActivity.java. See README.");
		// }
		// if (getPackageName().startsWith("com.example")) {
		// throw new RuntimeException(
		// "Please change the sample's package name! See README.");
		// }

		// Create the helper, passing it our context and the public key to
		// verify signatures with

		Log.d(TAG, "Creating IAB helper.");
		mHelper = new IabHelper(activity, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(true);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				if (mGotInventoryListener != null)
					mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

		// Listener that's called when we finish querying the items and
		// subscriptions we own

	}

	public void complain(String message) {
		Log.e(TAG, "****Error: " + message);
		alert("Error: " + message);
	}

	private void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(activity);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	// Callback for when a invetory is finished
	private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			if (inAppListener != null)
				inAppListener.InAppInvetoryFinished(result, inventory);

		}
	};

	// Callback for when a purchase is finished
	private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (inAppListener != null)
				inAppListener.InAppPurchaseFinished(result, purchase);

		}
	};
	// Callback for when a consume is finished
	private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			if (inAppListener != null)
				inAppListener.InAppConsumeFinished(purchase, result);
		}
	};

	private IabHelper.OnConsumeMultiFinishedListener mConsumeMultiFinishedListener = new IabHelper.OnConsumeMultiFinishedListener() {

		@Override
		public void onConsumeMultiFinished(List<Purchase> purchases,
				List<IabResult> results) {
			if (inAppListener != null)
				inAppListener.InApponConsumeMultiFinished(purchases, results);
			// TODO Auto-generated method stub

		}
	};

	/** Verifies the developer payload of a purchase. */
	public boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

		return true;
	}

	public void launchPurchaseFlow(String sku, int requestCode) {
		if (mHelper == null || mPurchaseFinishedListener == null) {
			Log.e(TAG, "you first must call Init method to launchPurchase");
			return;
		}

		mHelper.launchPurchaseFlow(activity, sku, requestCode,
				mPurchaseFinishedListener);

	}

	public void launchPurchaseFlow(String sku, int requestCode, String extraData) {
		if (mHelper == null || mPurchaseFinishedListener == null) {
			Log.e(TAG, "you first must call Init method to launchPurchase");
			return;
		}
		mHelper.launchPurchaseFlow(activity, sku, requestCode,
				mPurchaseFinishedListener, extraData);

	}

	public void launchPurchaseFlow(String sku, String itemType,
			int requestCode, String extraData) {
		if (mHelper == null || mPurchaseFinishedListener == null) {
			Log.e(TAG, "you first must call Init method to launchPurchase");
			return;
		}
		mHelper.launchPurchaseFlow(activity, sku, itemType, requestCode,
				mPurchaseFinishedListener, extraData);

	}

	public void launchSubscriptionPurchaseFlow(String sku, int requestCode) {
		if (mHelper == null || mPurchaseFinishedListener == null) {
			Log.e(TAG, "you first must call Init method to launchPurchase");
			return;
		}
		mHelper.launchSubscriptionPurchaseFlow(activity, sku, requestCode,
				mPurchaseFinishedListener);
	}

	public void launchSubscriptionPurchaseFlow(String sku, int requestCode,
			String extraData) {
		if (mHelper == null || mPurchaseFinishedListener == null) {
			Log.e(TAG, "you first must call Init method to launchPurchase");
			return;
		}
		mHelper.launchSubscriptionPurchaseFlow(activity, sku, requestCode,
				mPurchaseFinishedListener, extraData);

	}

	public void consumeAsync(Purchase purchase) {
		if (mHelper == null || mPurchaseFinishedListener == null) {
			Log.e(TAG, "you first must call Init method to consume purchase");
			return;
		}
		mHelper.consumeAsync(purchase, mConsumeFinishedListener);

	}

	public void consumeAsyncInternal(List<Purchase> listPurchase) {
		if (mHelper == null || mPurchaseFinishedListener == null) {
			Log.e(TAG, "you first must call Init method to consume purchase");
			return;
		}
		mHelper.consumeAsyncInternal(listPurchase, mConsumeFinishedListener,
				mConsumeMultiFinishedListener);

	}

	public void consumeAsync(List<Purchase> listPurchase) {
		if (mHelper == null || mPurchaseFinishedListener == null) {
			Log.e(TAG, "you first must call Init method to consume purchase");
			return;
		}
		mHelper.consumeAsync(listPurchase, mConsumeMultiFinishedListener);
	}

	public boolean handleActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (mHelper == null)
			return false;
		return mHelper.handleActivityResult(requestCode, resultCode, data);
	}
}

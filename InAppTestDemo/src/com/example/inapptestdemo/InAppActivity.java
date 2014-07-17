package com.example.inapptestdemo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.inapppurchasewrapper.utils.IabResult;
import com.example.inapppurchasewrapper.utils.InAppListener;
import com.example.inapppurchasewrapper.utils.InAppWrapper;
import com.example.inapppurchasewrapper.utils.Inventory;
import com.example.inapppurchasewrapper.utils.Purchase;

public class InAppActivity extends Activity implements InAppListener {

	private Button btnPurchase, btnConsume;
	private InAppWrapper inApp;
	static final String BASE_64_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArFKsWOCguKlHkKngnwp3JFU/VF+ChVUNSgNlWdOXPKoW+ZEHZsWRD9CReNhiJoLvnkhlGioWyl729J9uNZF0j3qOAzTIBJpkDfYcGz8nYmUJrWcPxzGuQ7OMcIArJYJvpnd3bxafomhdqc1OzEhK/fEX26lbzh4HHvOpcFmrhfUjgkLvwzO/+mlvHAv9wUWMNplnEMrAOJkMyewz3qZuzAHY04m6OQYu/Xqp6cGtBXfntzx3UtaSY9bpjE0c3dGZQsf+4J9JtLqsGEfMf7JvAiqpBLeYwfNhYMrL7eeBlqTEN/pTkqJRsRdP24jxePWgA0VMK3R8ueh55/Tl0cvN3QIDAQAB";
	protected final String SKU = "android.test.purchased";
	private boolean isGoPro;

	private int requestCode = 22211;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		inApp = new InAppWrapper(this, this);
		inApp.Init(BASE_64_KEY);
		btnPurchase = (Button) findViewById(R.id.btnPurchase);

		btnPurchase.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				inApp.launchPurchaseFlow(SKU, requestCode);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void InAppInvetoryFinished(IabResult result, Inventory inventory) {
		// TODO Auto-generated method stub

		// Have we been disposed of in the meantime? If so, quit.

		// Is it a failure?
		if (result.isFailure()) {
			inApp.complain("Failed to query inventory: " + result);

			return;
		}

		System.out.println("Query inventory was successful.");

		Purchase goPro = inventory.getPurchase(SKU);

		isGoPro = (goPro != null && inApp.verifyDeveloperPayload(goPro));
		Log.d("InApp", "User is "
				+ (isGoPro ? "YES PRO VERSION " : "NOT PRO VERSION"));
		if (isGoPro)
			inApp.consumeAsync(goPro);

	}

	@Override
	public void InAppPurchaseFinished(IabResult result, Purchase purchase) {
		// TODO Auto-generated method stub
		if (result.isFailure()) {
			Toast.makeText(this, "Purchase Failed", Toast.LENGTH_SHORT).show();
			return;
		}
		if (purchase.getSku().equals(SKU)) {
			Log.d("In app Purchase", "Purchase successful.");
			Toast.makeText(this, "Purchase Successfull", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void InAppConsumeFinished(Purchase purchase, IabResult result) {
		// TODO Auto-generated method stub
		Log.d("In app Purchase", "Consumption finished. Purchase: " + purchase
				+ ", result: " + result);
		if (result == null || result.isFailure()) {
			Toast.makeText(this, "Error in consumption", Toast.LENGTH_SHORT)
					.show();
			return;
		} else {
			Toast.makeText(this, "previous purchase successfully consume",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void InApponConsumeMultiFinished(List<Purchase> purchases,
			List<IabResult> results) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("In App Purchase", "onActivityResult(" + requestCode + ","
				+ resultCode + "," + data);

		// Pass on the activity result to the helper for handling
		if (!inApp.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d("In App Purchase", "onActivityResult handled by IABUtil.");
		}
	}

}

package com.hdik.inapppurchasewrapper.utils;

import java.util.List;

public interface InAppListener {

	void InAppInvetoryFinished(IabResult result, Inventory inventory);

	void InAppPurchaseFinished(IabResult result, Purchase purchase);

	void InAppConsumeFinished(Purchase purchase, IabResult result);

	void InApponConsumeMultiFinished(List<Purchase> purchases,
			List<IabResult> results);

}

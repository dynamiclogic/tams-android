package com.dynamiclogic.tams.database.model.callback;

import com.dynamiclogic.tams.database.model.Asset;

/**
 * Created by ntessema on 8/23/15.
 */
public interface AssetListener {
    public void onAssetUpdate(Asset asset);
}

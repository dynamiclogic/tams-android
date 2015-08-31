package com.dynamiclogic.tams.model.callback;

import com.dynamiclogic.tams.model.Asset;

import java.util.List;

/**
 * Created by ntessema on 8/23/15.
 */
public interface AssetsListener {
    public void onAssetsUpdated(List<Asset> assets);
}

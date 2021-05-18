package com.esi.navigator_22.utils;

import android.app.Activity;
import android.os.Environment;
import android.view.View;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.io.File;
import java.util.Set;


public class MapUtils implements FileUtils.FileTransferListener {

    private MapView map;
    private MapListener mapListener;
    private View animatedView;
    private Activity activity;
    private MapUtils mapUtils;

    public MapView getMap() {
        return map;
    }

    public MapUtils(MapListener mapListener, Activity activity) {
        this.mapListener = mapListener;
        this.activity = activity;
        mapUtils = this;
        map = new MapView(activity);
        FileUtils.copyMapFilesToSdCard(activity, this);
    }


    public void setInitialZoom(double zoomLevel) {
        if (map != null) {
            IMapController mapController = new MapController(map);
            mapController.zoomTo(zoomLevel);
        }
    }

    public void setInitialPosition(GeoPoint initialPosition, double zoomLvl) {
        if (map != null) {
            IMapController mapController = new MapController(map);
            mapController.setCenter(initialPosition);
            mapController.zoomTo(zoomLvl);
            map.getController().animateTo(initialPosition);
        }
    }

    public void setInitialPositionX(GeoPoint initialPosition) {
        if (map != null) {
            IMapController mapController = new MapController(map);
            mapController.setCenter(initialPosition);
            map.getController().animateTo(initialPosition);
        }
    }

    private void setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setUseDataConnection(false);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(13.);
        map.setMaxZoomLevel(19.);
        setMapOfflineSource();
    }


    private void setMapOfflineSource() {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/");
        if (f.exists()) {
            File[] list = f.listFiles();
            if (list != null) {
                for (File aList : list) {
                    if (aList.isDirectory()) {
                        continue;
                    }
                    String name = aList.getName().toLowerCase();
                    if (!name.contains(".")) {
                        continue;
                    }
                    name = name.substring(name.lastIndexOf(".") + 1);
                    if (name.length() == 0) {
                        continue;
                    }
                    if (ArchiveFileFactory.isFileExtensionRegistered(name)) {
                        try {
                            OfflineTileProvider tileProvider = new OfflineTileProvider(new SimpleRegisterReceiver(activity),
                                    new File[]{aList});
                            map.setTileProvider(tileProvider);
                            String source = "";
                            IArchiveFile[] archives = tileProvider.getArchives();
                            if (archives.length > 0) {
                                Set<String> tileSources = archives[0].getTileSources();
                                if (!tileSources.isEmpty()) {
                                    source = tileSources.iterator().next();
                                    map.setTileSource(FileBasedTileSource.getSource(source));
                                } else {
                                    map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                                }
                            } else {
                                map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                            }
                            map.invalidate();
                            mapListener.mapLoadSuccess(map, mapUtils);
                            return;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            mapListener.mapLoadFailed(ex.toString());
                        }
                    }
                }
            }
        } else {
            if (!FileUtils.isMapFileExists()) {
                FileUtils.copyMapFilesToSdCard(activity, new FileUtils.FileTransferListener() {
                    @Override
                    public void onLoadFailed() {
                        //WARNING Fabric.getInstance() custom event
                        mapListener.mapLoadFailed("");
                    }

                    @Override
                    public void onLoadSuccess() {
                        setMapOfflineSource();
                    }
                });
            }
        }
    }

    @Override
    public void onLoadFailed() {
        mapListener.mapLoadFailed("Asset files cannot be copied");
    }

    @Override
    public void onLoadSuccess() {
        setupMap();
    }
}

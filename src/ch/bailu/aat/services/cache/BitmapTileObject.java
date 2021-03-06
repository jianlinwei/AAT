package ch.bailu.aat.services.cache;

import java.io.File;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

import android.content.Context;
import android.graphics.Bitmap;
import ch.bailu.aat.helpers.AppBroadcaster;
import ch.bailu.aat.helpers.AppDirectory;
import ch.bailu.aat.services.background.DownloadHandle;
import ch.bailu.aat.services.background.FileHandle;
import ch.bailu.aat.services.cache.CacheService.SelfOn;


public class BitmapTileObject extends TileObject {
    private final Source source;
    private final MapTile tile;


    private final FileHandle load;
    private final DownloadHandle download;
    private final String url;


    private final SynchronizedBitmap bitmap=new SynchronizedBitmap();


    public BitmapTileObject(String id, SelfOn self,  MapTile t, Source s) {
        super(id);
        
        tile = t;
        source=s;
        url = source.getTileURLString(tile);

        self.broadcaster.put(this);
        download = new DownloadHandle(url, new File(id) );
        
        load = new FileHandle(id) {

            @Override
            public long bgOnProcess() {
                if (download.isLocked()==false) {
                    bitmap.load(toString(),EmptyTileObject.NULL_BITMAP.get());
                    return bitmap.getSize();
                }
                return 0;
            }

            @Override
            public void broadcast(Context context) {
                AppBroadcaster.broadcast(context, AppBroadcaster.FILE_CHANGED_INCACHE, BitmapTileObject.this.toString());
            }
        };
        
    }

    
    @Override
    public void onInsert(SelfOn self) {
        if (isLoadable()) self.background.load(load);
        else if (isDownloadable()) self.background.download(download);
    }


    private boolean isLoadable() {
        return new File(toString()).exists();
    }
    
    private boolean isDownloadable() {
        return (
                !new File(toString()).exists() &&
                source.getMaximumZoomLevel() >= tile.getZoomLevel() &&
                source.getMinimumZoomLevel() <= tile.getZoomLevel());
    }
    
    
    @Override
    public void onDownloaded(String id, String u, SelfOn self) {
        if (u.equals(url) && isLoadable()) {
            self.background.load(load);
        }
        
    }


    @Override
    public void onChanged(String id, SelfOn self) {}
    
    

    @Override
    public boolean isReady() {
        boolean d = bitmap.getDrawable()!=null;
        boolean l = isLoadable()==false;
        
        /*
        if (d) AppLog.d(this, "have drawable");
        else AppLog.d(this, "have NO drawable");
        if (l) AppLog.d(this, "is not loadable");
        */
        
        return  d || l;
    }

    
    @Override
    public long getSize() {
        return bitmap.getSize();
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap.get();
    }


  




    public static class Factory extends ObjectHandle.Factory {
        private final MapTile mapTile;
        private final Source source;

        
        public Factory(MapTile mt, Source s) {
            mapTile=mt;
            source = s;
        }

        @Override
        public ObjectHandle factory(String id, SelfOn self) {
            return new BitmapTileObject(id, self, mapTile, source);
        }
    }
    
    public static class Source extends TileObject.Source {

        private final XYTileSource osmdroidSource;
        private final TileBitmapFilter filter;

        
        
        public Source (String sourceName, TileBitmapFilter f, int minZ, int maxZ, final String... url) {
            osmdroidSource = new XYTileSource(sourceName, ResourceProxy.string.mapnik, minZ, maxZ, 256, ".png", url);
            filter = f;
        }
        

        @Override
        public String getName() {
            return osmdroidSource.name();
        }
        
        
        @Override
        public String getID(MapTile mt, Context context) {
            return AppDirectory.getTileFile(mt, osmdroidSource, context).getAbsolutePath();
        }

        @Override
        public int getMinimumZoomLevel() {
            return osmdroidSource.getMinimumZoomLevel();
        }

        @Override
        public int getMaximumZoomLevel() {
            return osmdroidSource.getMaximumZoomLevel();
        }

        @Override
        public Factory getFactory(MapTile mt) {
            return new BitmapTileObject.Factory(mt, this);
        }

        public String getTileURLString(MapTile tile) {
            return osmdroidSource.getTileURLString(tile);
        }


        @Override
        public TileBitmapFilter getBitmapFilter() {
            return filter;
        }
    }
}

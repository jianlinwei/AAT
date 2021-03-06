package ch.bailu.aat.services.icons;

import java.io.File;
import java.io.IOException;

import ch.bailu.aat.gpx.GpxAttributes;
import ch.bailu.aat.gpx.GpxList;
import ch.bailu.aat.gpx.GpxListWalker;
import ch.bailu.aat.gpx.GpxPointNode;
import ch.bailu.aat.gpx.GpxSegmentNode;
import ch.bailu.aat.gpx.interfaces.GpxType;
import ch.bailu.aat.helpers.AppDirectory;
import ch.bailu.aat.helpers.AppLog;
import ch.bailu.aat.services.AbsService;

public class IconMapService extends AbsService {
    public final static String ICON_SUFFIX_BIG=".n.64.png";
    public final static String ICON_SUFFIX_SMALL=".n.48.png";
    public final static String MAP_FILE="iconmap.txt";
    public static final String KEY_ICON_SMALL = "icon:small";
    public static final String KEY_ICON_BIG = "icon:big";
    
    
    private final IconMap map = new IconMap();
    
    private File directory;
    
    @Override
    public void onCreate() {
        super.onCreate();

        directory = AppDirectory.getDataDirectory(this, AppDirectory.DIR_OSM_FEATURES_ICONS);
        
        final File mapFile = new File(directory, MAP_FILE);

        if (mapFile.exists()) {
            try {
                new IconMapParser(mapFile, map);
            } catch (IOException e) {
                AppLog.e(this, e);
            }
        }
    }
    
    
    @Override
    public void onServicesUp() {}
    

    public String getSmallIconPath(String key, String value) {
        String prefix = getIconName(key,value);
         
        if (prefix == null) {
            return null;
        }
        return new File(new File(directory,"png"), prefix+ICON_SUFFIX_SMALL).toString();
    }
    

    public String getBigIconPath(String key, String value) {
        String prefix = getIconName(key,value);
        
        if (prefix == null) {
            return null;
        }
        return new File(new File(directory,"png"), prefix+ICON_SUFFIX_BIG).toString();
    }

    
    private String getIconName(String key, String value) {
        return map.get(key, value);
    }
    

    
    public void iconify(StringBuilder html, String key, String value) {
        String icon = getBigIconPath(key, value);
            
        if (icon != null) {
            html.append("<p><img src=\"");
            html.append(icon);
            html.append("\"/></p>");
        }
    }

    
    public void iconify(GpxList list) {
        new GpxIconifier().walkTrack(list);
    }
    
    
    
    private class GpxIconifier extends GpxListWalker {

        @Override
        public boolean doList(GpxList track) {
            return track.getDelta().getType()==GpxType.WAY;
        }

        @Override
        public boolean doSegment(GpxSegmentNode segment) {
            return true;
        }

        @Override
        public boolean doMarker(GpxSegmentNode marker) {
            return true;
        }

        @Override
        public void doPoint(GpxPointNode point) {
            GpxAttributes a = point.getAttributes();
            
            for(int i = 0; a != null && i< a.size(); i++) {
                final String key = a.getKey(i);
                final String value = a.getValue(i);
                
                final String sicon = getSmallIconPath(key,value);
                final String bicon = getBigIconPath(key,value);
                
                if (sicon != null && bicon != null) {
                    a.put(KEY_ICON_SMALL, sicon);
                    a.put(KEY_ICON_BIG, bicon);
                    break;
                }
            }
        }
    }
}

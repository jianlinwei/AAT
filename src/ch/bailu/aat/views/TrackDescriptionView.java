package ch.bailu.aat.views;

import android.content.Context;
import android.view.ViewGroup;
import ch.bailu.aat.description.DescriptionInterface;
import ch.bailu.aat.gpx.GpxInformation;

public abstract class TrackDescriptionView extends ViewGroup 
implements DescriptionInterface, GpxInformation.ID {
    public static final String DEFAULT_SOLID_KEY=TrackDescriptionView.class.getSimpleName();
    
    
    public final Filter filter;
    public final String solidKey;

    public TrackDescriptionView(Context context, String key, int infoID) {
        super(context);
        filter = new Filter(infoID);
        solidKey=key;
    }

    public static class Filter {
        public final int id;
        
        public Filter(int f) {
            id = f;
        }
        
        public boolean pass(GpxInformation info) {
            return (id==INFO_ID_ALL || id==info.getID());
        }
    }
    
    
}

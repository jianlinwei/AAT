package ch.bailu.aat.description;

import android.content.Context;

import ch.bailu.aat.coordinates.CH1903Coordinates;
import ch.bailu.aat.gpx.GpxInformation;

import ch.bailu.aat.R;
public class CH1903NorthingDescription extends CH1903EastingDescription {

    public CH1903NorthingDescription(Context c) {
        super(c);
    }

    @Override
    public String getLabel() {
        return getString(R.string.d_chx);
    }

    @Override
    public void updateGpxContent(GpxInformation info) {
        if (setCache(info.getLatitude())) setCH1903_x(info);
    }
    
    private void setCH1903_x(GpxInformation info) {
        coordinate = new CH1903Coordinates(info.getLatitude(), info.getLongitude()).getNorthing();
    }

}

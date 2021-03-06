package ch.bailu.aat.description;

import java.util.Locale;

import android.content.Context;
import ch.bailu.aat.coordinates.CH1903Coordinates;
import ch.bailu.aat.gpx.GpxInformation;
import ch.bailu.aat.R;

public class CH1903EastingDescription extends DoubleDescription {

    protected int coordinate=0;
    
    public CH1903EastingDescription(Context c) {
        super(c);
    }

    @Override
    public String getLabel() {
        return getString(R.string.d_chy);
    }

    @Override
    public String getUnit() {
        return "km";
    }

    @Override
    public String getValue() {
        return String.format((Locale)null,"%.3f",  ( (float)(coordinate) ) / 1000 );
    }

    @Override
    public void updateGpxContent(GpxInformation info) {
        if (setCache(info.getLongitude())) setCH1903_y(info);
    }

    private void setCH1903_y(GpxInformation info) {
        coordinate = new CH1903Coordinates(info.getLatitude(), info.getLongitude()).getEasting();
    }
}

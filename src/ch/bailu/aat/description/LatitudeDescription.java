package ch.bailu.aat.description;

import android.content.Context;

import ch.bailu.aat.gpx.GpxInformation;

import ch.bailu.aat.R;
public class LatitudeDescription extends LongitudeDescription {

    public LatitudeDescription(Context context) {
        super(context);
    }

    @Override
    public String getLabel() {
        return getString(R.string.d_latitude);
    }

    @Override
    public void updateGpxContent(GpxInformation info) {
        setCache(info.getLatitude());
    }

}

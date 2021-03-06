package ch.bailu.aat.description;

import java.util.Locale;

import android.content.Context;

import ch.bailu.aat.gpx.GpxInformation;
import ch.bailu.aat.preferences.SolidUnit;

import ch.bailu.aat.R;

public class DistanceDescription extends FloatDescription {
    private final static String FORMAT_STRING[] = {"%.3f", "%.2f", "%.1f", "%.0f"}; 
    private final SolidUnit unit;
    
    public DistanceDescription(Context context) {
        super(context);
        
        unit = new SolidUnit(this);
    }

    
    @Override
    public String getLabel() {
        return getString(R.string.distance);
    }
    
    
    @Override
    public String getUnit() {
        return unit.getDistanceUnit();
    }

    
    @Override
    public String getValue() {
        float dist = unit.getDistanceFactor() * getCache();
        
        int format=0;
        for (int x=10; dist >= x && format < FORMAT_STRING.length-1; x*=10) format++;
        return String.format(Locale.getDefault(),FORMAT_STRING[format], dist);
    }

    


    @Override
    public void updateGpxContent(GpxInformation info) {
        setCache(info.getDistance());
    }


    public String getDistanceDescription(float distance) {
        float nonSI=distance * unit.getDistanceFactor();
        
        if (nonSI < 1)
            return getAltitudeDescription(distance);
        
        return String.format(Locale.getDefault(),"%1.1f%s", nonSI, unit.getDistanceUnit());
    }
    

    public String getDistanceDescriptionRounded(float distance) {
        float nonSI=distance * unit.getDistanceFactor();
        
        if (nonSI < 1)
            return getAltitudeDescription(distance);
        
        return String.format(Locale.getDefault(),"%1.0f%s", nonSI, unit.getDistanceUnit());
    }

    
    public String getAltitudeDescription(double value) {
        return String.format(Locale.getDefault(),"%1.0f%s", value*unit.getAltitudeFactor(), unit.getAltitudeUnit());
    }

}

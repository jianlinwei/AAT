package ch.bailu.aat.preferences;

import ch.bailu.aat.R;
import android.content.Context;

public class SolidAutopause extends SolidIndexList {
    private final static String KEY="autopause";
    private final Context context;
    
    
    
    private static final float[] SPEED_VALUES = {0, 
        0.25f, 0.50f, 0.75f, 1.00f, 1.25f, 1.50f,   
        0.25f, 0.50f, 0.75f, 1.00f, 1.25f, 1.50f,
        0.25f, 0.50f, 0.75f, 1.00f, 1.25f, 1.50f,
        0.25f, 0.50f, 0.75f, 1.00f, 1.25f, 1.50f,
        0.25f, 0.50f, 0.75f, 1.00f, 1.25f, 1.50f,
    };

    
    private static final int[] TRIGGER_VALUES = {0,
        3 ,3 ,3 ,3 ,3 ,3,
        4 ,4 ,4 ,4 ,4 ,4,
        5 ,5 ,5 ,5 ,5 ,5,
        10,10,10,10,10,10,
        20,20,20,20,20,20,
    };
    
    private SolidUnit sunit;

    public SolidAutopause(Context c, int i) {
        super(Storage.preset(c), KEY+i);
        context=c;
        sunit = new SolidUnit(context);
    }
    
    public float getTriggerSpeed() {
        return SPEED_VALUES[getIndex()];
    }
    
    public int getTriggerLevel() {
        return TRIGGER_VALUES[getIndex()];
    }
    
    @Override
    public String getLabel() {
        return context.getString(R.string.p_autopause_title);
    }

    public boolean isEnabled() {
        return getIndex()>0;
    }

    @Override
    public int length() {
        return SPEED_VALUES.length;
    }

    
    private String getString(int i) {
        if (i==0) return getContext().getString(R.string.off);
        
        return String.format("%.3f%s - %d", SPEED_VALUES[i] * sunit.getSpeedFactor(), sunit.getSpeedUnit(), TRIGGER_VALUES[i]);
    }
    
    @Override
    public String getString() {
        return getString(getIndex());
    }

    @Override
    public String[] getStringArray() {
        String[] list = new String[length()];
        for (int i=0; i<length(); i++){
            list[i]=getString(i);
        }
        
        return list;
    }
}

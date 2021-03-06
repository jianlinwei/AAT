package ch.bailu.aat.description;


import android.content.Context;

import ch.bailu.aat.gpx.GpxInformation;

import ch.bailu.aat.R;

public class NameDescription extends ContentDescription {
    private String name = "";
    
    public NameDescription(Context context) {
        super(context);
    }

    @Override
    public String getLabel() {
        return getString(R.string.d_name);
    }

    @Override
    public String getUnit() {
        return "";
    }

    @Override
    public String getValue() {
        return name;
    }

    public boolean updateName(String s) {
        boolean r = !name.equals(s);
        name=s;
        return r;
    }
    
    @Override
    public void updateGpxContent(GpxInformation info) {
        updateName(info.getName());
    }

}

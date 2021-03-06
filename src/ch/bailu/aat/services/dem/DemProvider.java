package ch.bailu.aat.services.dem;

public interface DemProvider {
    public short getElevation(int index);
    public DemDimension getDim();
    
    public float getCellsize();
    
    public boolean inverseLatitude();
    public boolean inverseLongitude();
}

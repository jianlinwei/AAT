package ch.bailu.aat.gpx.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import ch.bailu.aat.gpx.interfaces.GpxPointInterface;
import ch.bailu.aat.services.dem.ElevationProvider;

public class WayWriter extends GpxWriter {

    public WayWriter(File file) throws FileNotFoundException {
        super(file);
    }

    @Override
    public void writeFooter() throws IOException {
        writeEndElement(QNAME_GPX);
    }

    @Override
    public void writeSegment() throws IOException {
    }

    @Override
    public void writeFirstSegment(long timestamp) throws IOException {
    }

    @Override
    public void writeTrackPoint(GpxPointInterface tp) throws IOException {
        writeString("\t");
        writeBeginElementStart(QNAME_WAY_POINT);
        writeParameter(QNAME_LATITUDE, String.format((Locale)null, "%.6f", tp.getLatitude()));
        writeParameter(QNAME_LONGITUDE, String.format((Locale)null, "%.6f", tp.getLongitude()));

        writeBeginElementEnd();

        if (tp.getAltitude() != ElevationProvider.NULL_ALTITUDE) {
            writeBeginElement(QNAME_ALTITUDE);
            writeString(String.format((Locale)null, "%d",(int)tp.getAltitude()));
            writeEndElement(QNAME_ALTITUDE);
        }

        
        writeEndElement(QNAME_WAY_POINT);
        writeString("\n");
    }

}

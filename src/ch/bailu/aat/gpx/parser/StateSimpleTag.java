package ch.bailu.aat.gpx.parser;

import java.io.IOException;

import ch.bailu.aat.gpx.GpxAttributes.Tag;
import ch.bailu.aat.gpx.parser.XmlParser.ParserIO;

public class StateSimpleTag extends ParserState {
    
    
    
    @Override
    public void parse(ParserIO io) throws IOException {
        String key="", value="";
        
        while (true) {
            if (io.stream.haveQuotation()) {
                if (key.equals("lat")) {
                    io.latitude.scan();
                } else if (key.equals("lon")) {
                    io.longitude.scan();
                } else {
                    parseQuotedString(io);
                    value = io.builder.toString();
                    io.tagList.add(new Tag(key, value));
                }
                break;
                
            } else if (io.stream.haveCharacter()) {
                parseSimpleString(io);
                key = io.builder.toString();
            }
            

            io.stream.read();
            if (io.stream.haveEOF() || io.stream.haveA('>')) break;
        }
    }




    private void parseSimpleString(ParserIO io) throws IOException {
        io.builder.setLength(0);
        while(true) {
            if (io.stream.haveCharacter() || io.stream.haveDigit()) {
                io.builder.append((char)io.stream.get());
            } else {
                break;
            }
            io.stream.read();
        }
    }

    
    private void parseQuotedString(ParserIO io) throws IOException {
        io.builder.setLength(0);
        
        while(true) {
            io.stream.read();
            
            if (io.stream.haveEOF() || io.stream.haveQuotation()) { 
                break;
            } else {
                io.builder.append((char)io.stream.get());
            }
        }
    }

}

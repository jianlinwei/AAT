package ch.bailu.aat.gpx.parser;
import java.io.IOException;



public class IntegerScanner {
	private int integer;
	private SimpleStream stream;
	
	public IntegerScanner(SimpleStream s) {
		stream = s;
	}
	
	
	public void scan() throws IOException {
		integer=0;
		
		stream.read();
		stream.skipWhitespace();
		
		while (true) {
			if (stream.haveDigit()) {
				integer*=10;
				integer+=stream.getDigit();
			} else {
				break;
			}
			stream.read();
		}
		
	}
	
	public int getInteger() {
		return integer;
	}
}

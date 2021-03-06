package ch.bailu.aat.gpx.parser;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import ch.bailu.aat.helpers.AppLog;


public class SimpleStream implements Closeable {
    private final static String CHARSET="UTF-8";
    private final static int BUFFER_BYTES=1024*10;
    
    
    private final Reader reader;
    private int c=0;

    public SimpleStream(File file) throws FileNotFoundException, UnsupportedEncodingException {
        
        
        InputStream istream = new FileInputStream(file);
        Reader ireader = new InputStreamReader(istream, CHARSET);
        reader = new BufferedReader(ireader, BUFFER_BYTES);            
    }

    
    public SimpleStream(String string) {
        reader = new StringReader(string);
    }
    
    
    public int get() {
        return c;
    }

    public void skip(long n) throws IOException {
        reader.skip(n);
    }

    public void read() throws IOException {
        c=reader.read();
    }

    public void read(long n) throws IOException {
        skip(n);
        read();
    }

    public boolean haveA(int x) {
        return c == x;
    }


    public boolean nextIs(int x) throws IOException {
        read();
        return haveA(x);
    }


    public boolean nextIs(String string) throws IOException {
        for (int i =0; i<string.length(); i++) {
            read();
            if (!haveA(string.charAt(i))) {
                return false;
            }

        }
        return true;
    }

    public void skipWhitespace() throws IOException {
        while (c== ' ' || c=='\n' || c=='\r' || c=='\t') read();
    }

    public boolean haveDigit() {
        return (c>='0' && c<='9');
    }

    public boolean haveCharacter() {
        return (c >= 'A' && c <= 'z');
    }
    
    public int getDigit() {
        return c-'0';
    }    


    
    public boolean haveEOF() {
        return c==-1;
    }

    public void to(int x) throws IOException {
        while(true) {
            read();
            if (haveA(x) || haveEOF()) break;
        }
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            AppLog.e(this, e);
        }
    }

    public boolean haveQuotation() {
        return (c=='\'' || c=='"');
    }
    
    public void toQuotation() throws IOException {
        while (haveQuotation()==false && haveEOF()==false) read();
    }
}

package com.putable.xrandom;

import java.io.CharConversionException;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class HexInputStream extends FilterInputStream {

	public HexInputStream(InputStream in) throws IOException {
		super(in);
	}

	private int readHex() throws IOException {
		int ch = this.in.read();
		if (ch < 0) 
			return -1;
		if (ch >= '0' && ch <= '9') return ch-'0';
		if (ch >= 'a' && ch <= 'f') return ch-'a'+10;
		if (ch >= 'A' && ch <= 'F') return ch-'A'+10;
		throw new CharConversionException();
	}
	@Override
	public int read() throws IOException {
		int ch = readHex();
		if (ch < 0) 
			return ch;
		int ch2 = readHex();
		if (ch < 0) 
			throw new EOFException(); // Can't quit halfway through a byte
		return (ch<<4)|ch2;
	}

	@Override
	public boolean markSupported() {  
		return false;
	}
	
	@Override
	public int read(byte[] buf, int off, int len) throws IOException {
		for (int i = 0; i < len; ++i) {
			int ch = read();
			if (ch < 0)
				if (i==0)
					return -1;
				else
					return i;
			buf[off+i] = (byte) ch;
		}
		return len;
	}
	
	@Override
	public long skip(long n) throws IOException {
		for (long i = 0; i < n; ++i) {
			int ch = read();
			if (ch < 0)
				return i;
		}
		return n;
	}
}

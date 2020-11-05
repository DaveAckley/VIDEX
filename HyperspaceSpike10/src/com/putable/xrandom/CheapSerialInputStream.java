package com.putable.xrandom;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CheapSerialInputStream extends FilterInputStream implements CheapSerialIn {

	public CheapSerialInputStream(InputStream in) {
		super(in);
	}

	public long readLong() throws IOException {
		long value = 0;
		for (int i = 0; i < 8; ++i) {
			long b = read();
			if (b < 0)
				throw new EOFException();
			value |= b<<(i<<3);
		}
		return value;
	}
		
	public int readInt() throws IOException {
		int value = 0;
		for (int i = 0; i < 4; ++i) {
			int b = read();
			if (b < 0)
				throw new EOFException();
			value |= b<<(i<<3);
		}
		return value;
	}

	public long readInteger() throws IOException {
		long value = 0;
		int sign = 1;
		byte count = (byte) readByte(); 
		if (count < 0) {
			sign = -1;
			count = (byte) -count;
		}
		while (count-- > 0) {
			value = (value<<8)|(((long)readByte())&0xff);
		}
		return sign*value;
	}
	
	/**
	 * Reads the next byte, returns 0..255, or throws EOFException if
	 * no more input is available.
	 * @return
	 * @throws IOException
	 */
	public int readByte() throws IOException {
		int b = read();
		if (b < 0)
			throw new EOFException();
		return b;
	}

	public short readShort() throws IOException {
		int value = readByte();
		return (short) (((((int) readByte())&0xff) <<8)|value);
	}

	public char readChar() throws IOException {
		int b = readByte();
		if (b <= 0x7f) return (char) b;
		return (char) readShort();
	}
		
	public String readString() throws IOException {
		long len = readSize();
		if (len > Integer.MAX_VALUE)
			throw new NumberFormatException();
		char[] chs = new char[(int) len];
		for (int i = 0; i < len; ++i) {
			chs[i] = readChar();
		}
		return new String(chs);
	}

	public long readSize() throws IOException {
		long value = 0;
		int count = 0;
		while (true) {
			long b = readByte();
			value |= (b&0x7f)<<count;
			if ((b&0x80)==0) break;
			count += 7;
		}
		return value;
	}
}

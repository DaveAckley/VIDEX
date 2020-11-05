package com.putable.xrandom;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CheapSerialOutputStream extends FilterOutputStream implements CheapSerialOut {

	public CheapSerialOutputStream(OutputStream out) {
		super(out);
	}
	
	public void writeLong(long val) throws IOException {
		for (int i = 0; i < 8; ++i) {               // Barf it out little endian
			int b = (int) ((val>>>(i<<3))&0xff);
			write(b);
		}
	}

	public void writeInt(int val) throws IOException {
		for (int i = 0; i < 4; ++i) {               // Barf it out little endian
			int b = (int) ((val>>>(i<<3))&0xff);
			write(b);
		}
	}

	/**
	 * Write out an integer (int or long, mostly) trying to
	 * save bytes on smaller numbers.  Saves at least one byte 
	 * over writeInt when num fits in a short; saves over
	 * writeLong when |num| &lt; 2^48.
	 * @param num
	 * @throws IOException
	 */
	public void writeInteger(long num) throws IOException {
		int sign = 1;
		if (num < 0) {
			sign = -1;
			num = -num;
		}
		byte count = 0;
		long copy = num;
		while (copy != 0) {
			copy >>>= 8;
			++count;
		}

		write(sign*count);
		while (count-- > 0) {
			write((int) ((num>>>(count<<3))&0xff));
		}
	}

	public void writeShort(short val) throws IOException {
		for (int i = 0; i < 2; ++i) {               // Barf it out little endian
			int b = (int) ((val>>>(i<<3))&0xff);
			write(b);
		}
	}
	
	public void writeChar(char val) throws IOException {
		if (val <= 0x7f) 
			writeByte((byte) val);
		else {
			writeByte((byte) 0x80);
			writeShort((short) val);
		}
	}

	public void writeSize(long size) throws IOException {
		if (size<0)
			throw new IllegalArgumentException();
		while (size > 0x7f) {
			writeByte((byte)( 0x80|(size&0x7f)));
			size >>= 7;
		}
		writeByte((byte) size);
	}
	public void writeString(String str) throws IOException {
		writeString(str,Integer.MAX_VALUE);
	}

	public void writeString(String str, int maxLen) throws IOException {
		if (str.length() < maxLen)
			maxLen = str.length();
		writeSize(maxLen);
		for (int i = 0; i < maxLen; ++i) {
			writeChar(str.charAt(i));
		}
	}

	public void writeByte(byte b) throws IOException {
		write(b);
	}
}

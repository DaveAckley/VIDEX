package com.putable.xrandom;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5InputStream extends FilterInputStream {
	private MessageDigest md;
	private boolean digesting = true;

	public MD5InputStream(InputStream in, byte [] initial) throws IOException {
		super(in);
		try {	
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IOException("Can't MD5");
		}
		md.update(initial);
	}

	public void checkFinalDigest(int byteCount) throws IOException {
		byte[] digest = getFinalDigest();
		int bytes = Math.min(digest.length,byteCount);
		for (int i = 0; i < bytes; ++i) {
			int ch = read();
			if (ch < 0)
				throw new EOFException();
			if (((byte) ch) != digest[i])
				throw new NumberFormatException();
		}
	}
	public byte[] getFinalDigest() {
		if (!digesting)
			throw new IllegalStateException("Already not digesting");
		digesting = false;
		return md.digest();
	}
	
	@Override
	public int read() throws IOException {
		int ch = in.read();
		if (ch >= 0 && digesting)
			md.update((byte)ch);
		return ch;
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

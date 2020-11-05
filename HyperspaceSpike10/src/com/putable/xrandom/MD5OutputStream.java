package com.putable.xrandom;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5OutputStream extends FilterOutputStream {
	private MessageDigest md;
	private boolean digesting = true;
	public static byte[] digest(String input) {
		MessageDigest md;
		try {	
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			return null;
		}
		return md.digest(input.getBytes());
	}
	public MD5OutputStream(OutputStream out, byte[] initial) throws IOException {
		super(out);
		try {	
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IOException("Can't MD5");
		}
		md.update(initial);
	}

	public void appendFinalDigest(int byteCount) throws IOException {
		this.flush();
		byte[] digest = getFinalDigest();
		int bytes = Math.min(digest.length,byteCount);
		for (int i = 0; i < bytes; ++i) {
			write(digest[i]);
		}
	}
	public byte[] getFinalDigest() {
		if (!digesting)
			throw new IllegalStateException("Already not digesting");
		digesting = false;
		return md.digest();
	}
	@Override
	public void write(int b) throws IOException {
		if (digesting) md.update((byte) b);
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; ++i) {
			write(b[off+i]);
		}
	}

}

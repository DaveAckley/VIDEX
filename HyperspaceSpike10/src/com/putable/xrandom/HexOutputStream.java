package com.putable.xrandom;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class HexOutputStream extends FilterOutputStream {
	
	public HexOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	private void writeHex(int fourBits) throws IOException {
		this.out.write((int)("0123456789abcdef".charAt(fourBits)));
	}
	@Override
	public void write(int b) throws IOException {
		writeHex((b>>4)&0xf);
		writeHex(b&0xf);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; ++i) {
			write(b[off+i]);
		}
	}

}

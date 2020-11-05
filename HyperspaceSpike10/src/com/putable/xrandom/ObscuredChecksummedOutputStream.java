package com.putable.xrandom;

import java.io.IOException;
import java.io.OutputStream;


public class ObscuredChecksummedOutputStream extends CheapSerialOutputStream {
	private ObscureChecksum ocs;
	
	public ObscuredChecksummedOutputStream(long obscureKey, OutputStream out) throws IOException {
		super(out);
		out.write('o'); out.write('\n'); out.write('\0');  // Write our magic: "o\n\0" Oh no! 
		this.ocs = new ObscureChecksum(obscureKey); // Use the given obscuring key
		long seed = obscureKey&0x1ff;               // Extract some bits from it
		XRandom r = new XRandom(seed);              // Use them as a seed
		long mask = r.nextLong()|0x1ff;             // Get a mask, but don't mess with our seed
		obscureKey ^= mask;                         // Obscure the obscureKey..
		for (int i = 0; i < 8; ++i) {               // Barf it out little endian, unobscured
			int b = (int) ((obscureKey>>>(i<<3))&0xff);
			out.write(b);
		}
	}
	
	@Override
	public void write(int b) throws IOException {
		byte ob = ocs.obscure((byte) b);
		this.out.write(ob);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; ++i) {
			write(b[off+i]);
		}
	}
	
	public long getChecksum() {
		return ocs.getChecksum();
	}
	
}

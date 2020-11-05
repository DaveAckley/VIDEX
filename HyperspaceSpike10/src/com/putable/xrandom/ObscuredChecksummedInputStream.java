package com.putable.xrandom;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


public class ObscuredChecksummedInputStream extends CheapSerialInputStream {

	private final ObscureChecksum ocs;
	private long obscureKey;

	public long getObscureKey() { return obscureKey; }
	
	public ObscuredChecksummedInputStream(InputStream in) throws IOException {
		super(in);

		if (in.read() != 'o' || in.read() != '\n' || in.read() != '\0') // Check magic "o\n\0"
			throw new IOException("ONO!  Wrong or short header");

		long key = 0;
		for (int i = 0; i < 8; ++i) {
			long b = in.read();
			if (b < 0)
				throw new EOFException();
			key |= b<<(i<<3);
		}

		long seed = ((~key)&0x1ff);          // Extract seed
		XRandom r = new XRandom(seed);       // Init the generator     
		long mask = r.nextLong()|0x1ff;      // Derive the mask
		key ^= mask;                         // Obtain the final obscureKey
			
		obscureKey = key;                    // Stash the key
		ocs = new ObscureChecksum(key);      // Init the checksum with it
	}

	public int getChecksum() {
		return ocs.getChecksum();
	}
	
	@Override
	public int read() throws IOException {
		int ch = this.in.read();
		if (ch < 0) 
			return ch;
		return ((int) ocs.deobscure((byte) ch))&0xff;
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

package com.putable.xrandom;


public class ObscureChecksum {

	private int checksum;
	private XRandom random;

	private void update(byte b) {
		int cs = checksum;
		cs ^= b;                  
		cs <<= 1;
		cs |= (cs>>>31)^(cs>>>28)^1&1;  /* this amounts to 'bit 31 xnor bit 28'
		                                   of the unshifted checksum, using
		                                   starting-from-1 bit positions the
		                                   way the LFSR people tend to.. */
		checksum = cs;
	}

	public byte obscure(byte b) { // Identical to deobscure but included anyway!               
		byte ret = (byte) (b^random.nextByte());
		update(b);
		return ret;
	}

	public byte deobscure(byte b) { // Identical to obscure but included anyway!               
		byte ret = (byte) (b^random.nextByte());
		update(ret);
		return ret;
	}
	
	public ObscureChecksum(long seed) {
		this.random = new XRandom(seed);
		this.checksum = random.nextInt();
	}

	public int getChecksum() { 
		return checksum; 
	}
}

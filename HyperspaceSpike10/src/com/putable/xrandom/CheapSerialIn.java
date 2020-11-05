package com.putable.xrandom;

import java.io.IOException;

public interface CheapSerialIn {

	public long readLong() throws IOException ;
		
	public int readInt() throws IOException ;

	public long readInteger() throws IOException;
	
	public char readChar() throws IOException ;

	public int readByte() throws IOException ;
}

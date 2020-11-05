package com.putable.xrandom;

import java.io.IOException;

public interface CheapSerialOut {

	public void writeLong(long num) throws IOException ;
		
	public void writeInt(int num) throws IOException ;

	public void writeInteger(long num) throws IOException;
	
	public void writeChar(char ch) throws IOException ;
	
	public void writeByte(byte b) throws IOException ;

}

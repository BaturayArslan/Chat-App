package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;


public class App{
	
    public static void main( String[] args ){
    	ConnectionManager connManager = new ConnectionManager();
		WindowManager windowManager = new WindowManager(connManager);
    }
}

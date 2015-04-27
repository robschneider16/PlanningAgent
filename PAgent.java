// Planning Agent
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.StringReader;
import java.util.Vector;


public class PlaningAgent{
	private static final int MAEDENPORT = 7237;
	public static boolean finished = false;
	private Socket gridSocket;				// socket for communicating w/ server
	private BufferedReader gridIn;
	private PrintWriter gridOut;
	private Vector<String> visFeild = new Vector(35);

	//private Vector<String> rowOne = new Vector(5);
	//private Vector<String> rowTwo = new Vector(5);
	//private Vector<String> rowThree = new Vector(5);
	//private Vector<String> rowFour = new Vector(5);
	private Vector<String> rowFive = new Vector(5);
	private Vector<String> rowSix = new Vector(5);
	private Vector<String> rowSeven = new Vector(5);
	public boolean wallhuh = false;
	private String myID;
	private String cheesedirection;
	public boolean good;
	
	private LinkedList visField;                    // stores GOB's for painting the visual field
    private GridDisplay gd;
    public int xtemp = 0;

    public void registerWithGrid(String h, int p) {
        try {
	    // connects to h machine on port p
            gridSocket = new Socket(h, p);

	    // create output stream to communicate with grid
            gridOut = new PrintWriter(gridSocket.getOutputStream(), true); 
            gridOut.println("base"); // send role to server

	    //buffered reader reads from input stream from grid
            gridIn = new BufferedReader(new InputStreamReader(gridSocket.getInputStream()));
            myID = gridIn.readLine(); // read this agent's ID number
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + h);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + h);
            System.exit(1);
        }
    }

    public void processRetinalField(String calc){
		try{
		System.out.println(calc);
		StringReader blah = new StringReader(calc);
		int rowindex = 0;
		int rownumber = 1;
		int prev = 0;
		while(true){
			int temp = blah.read();
			if(temp == -1){break;}else{
			if(temp == 40){prev = 40;}
			if(temp == 41){if(prev == 40){visFeild.add("_"); prev = 0;}}
			if(temp == 36){visFeild.add("$"); prev = 0;}//money
			if(temp == 43){visFeild.add("+"); prev = 0;}//cheese
			if(temp == 42){visFeild.add("*"); prev = 0;}//wall
			if(temp == 35){visFeild.add("#"); prev = 0;}// door # 35
			if(temp == 75){visFeild.add("K"); prev = 0;}// key 75 k
			if(temp == 84){visFeild.add("T"); prev = 0;}// hammer 84 T
			if(temp == 64){visFeild.add("@"); prev = 0;}//rock == 64 == @
			//if(temp == 48){visFeild.add("0"); prev = 0;} remember that 7 is the current location
		}}}catch (IOException e){
			System.out.print("poop");
		}

		for (int counter = 0;counter < visFeild.size(); counter++){
		System.out.print(visFeild.get(counter) + " ");
		if(counter == 4 || counter == 9 || counter == 14 || counter == 19 || counter == 24 || counter == 29){
			System.out.println();
		}
		}
		nitRows();
		System.out.println();
		System.out.print(visFeild.size());
		System.out.println();
	}

	public void nitRows(){
		for(int i = 29; i < 35; i++){
			rowSeven.add(visFeild.get(i));
		}
		for(int i = 24; i < 30; i++){
			rowSix.add(visFeild.get(i));
		}
		for(int i = 19; i < 25; i++){
			rowFive.add(visFeild.get(i));
		}
	}

	public void getSensoryInfo() {
		try {
		    String status = gridIn.readLine().toLowerCase();
		    if((status.equals("die") || status.equals("success")) || status.equals("end")) {
			System.out.println("Final status: " + status);
			System.exit(1);
		    }
		    if ( ! status.equals("8") ){
			System.out.println("getSensoryInfo: Unexpected number of data lines - " + status);
			System.exit(1);
		    }
		    // 1: get the smell info
		    String heading = gridIn.readLine();
		    cheesedirection = heading;
		    // 2: get the inventory
		    String inventory = gridIn.readLine();
		    // 3: get the visual info
		    String info = gridIn.readLine();
		    visFeild = new Vector(35);
		    processRetinalField(info);
		    wallhuh = wallAtHuh(12);
		    // 4: get ground contents
		    String ground = gridIn.readLine();
		    // 5: get messages
		    String message = gridIn.readLine(); //CHECKS MESSAGES ****CHANGE****
		    // 6: energy
		    String energy = gridIn.readLine();
		    // 7: lastActionStatus
		    String lastActionStatus = gridIn.readLine();
		    System.out.println(lastActionStatus);
		    // 8: world time
		    String worldTime = gridIn.readLine();
		    

		    // store or update according to the data just read. . . .
		    gd.updateGDObjects(visField);
		   //db.updateLabels(heading, inventory, ground, energy, message, lastActionStatus, worldTime);
		}catch(Exception e) {}
    }
	
	public void sendEffectorCommand(String command) {
		gridOut.println(command);
		getSensoryInfo();
		}



	public boolean wallAtHuh(int loc){
		if(visFeild.get(loc).equals("*")){
			return true;
		}else{
			return false;
		}
	}
	public static void main (String args[]) throws Exception {
		ExploringAgent Rob = new ExploringAgent();
		Rob.registerWithGrid("localhost", MAEDENPORT);
		Rob.getSensoryInfo();	












	
}
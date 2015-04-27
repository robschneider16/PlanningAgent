import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;


public class Agent2{
	
	private static final int MAEDENPORT = 7237;
	public static boolean finished = false;
	private Socket gridSocket;				// socket for communicating w/ server
	private BufferedReader gridIn;
	private PrintWriter gridOut;
	private String myID;
	private String cheesedirection;
	public String temp;
	
	private LinkedList visField;                    // stores GOB's for painting the visual field
    private GridDisplay gd;
	
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
	
	
	public void findTheCheese(){
		switch(cheesedirection){
		case "f" : sendEffectorCommand("f"); break;
		case "b" : sendEffectorCommand("l"); break;
		case "l" : sendEffectorCommand("l"); break;
		case "r" : sendEffectorCommand("r"); break;
		case "h" : sendEffectorCommand("g"); finished = true; break;
		}
	}
	
	public void wallAhead(){
		sendEffectorCommand("r");
		getSensoryInfo();
		sendEffectorCommand("f");
		getSensoryInfo();
		if (temp.equalsIgnoreCase("fail")){
			wallAhead();
		} else {	
		sendEffectorCommand("l");
		getSensoryInfo();
		sendEffectorCommand("f");
		getSensoryInfo();
	}}

	
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
		   // System.out.println(info);
		    //System.out.println(info.indexOf("0"));
		    // 4: get ground contents
		    String ground = gridIn.readLine();
		    // 5: get messages
		    String message = gridIn.readLine(); //CHECKS MESSAGES ****CHANGE****
		    // 6: energy
		    String energy = gridIn.readLine();
		    // 7: lastActionStatus
		    String lastActionStatus = gridIn.readLine();
		    temp = lastActionStatus;
		    // 8: world time
		    String worldTime = gridIn.readLine();
		    

		    // store or update according to the data just read. . . .
		    gd.updateGDObjects(visField);
		   //db.updateLabels(heading, inventory, ground, energy, message, lastActionStatus, worldTime);
		    
		}catch(Exception e) {}
    }
	
	public void sendEffectorCommand(String command) {
		gridOut.println(command);
		}
	
	public static void main (String args[]) throws Exception {
		Agent2 Rob = new Agent2();
		Rob.registerWithGrid("localhost", MAEDENPORT);
		while (finished == false){
		Rob.getSensoryInfo();
		if (Rob.temp.equalsIgnoreCase("fail")){
			Rob.wallAhead();
	    	Rob.temp = "ok";
		}
	    	Rob.findTheCheese();
	    
		Thread.sleep(500);
		}
		Rob.sendEffectorCommand("k");	
		
	}}


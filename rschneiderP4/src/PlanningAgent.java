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
import java.util.Stack;
//import java.util.Random;


public class PlanningAgent{

	private static final int MAEDENPORT = 7237;
	public static boolean finished = false;
	private Socket gridSocket;				// socket for communicating w/ server
	private BufferedReader gridIn;
	private PrintWriter gridOut;
	private LinkedList visField;                    // stores GOB's for painting the visual field
    //
    //
    //Change these to larger numbers if you wish to run the agent on a larger map!
	public int gridXSize = 60;
	public int gridYSize = 40;


	public boolean wallhuh = false;
	private String myID;
	private String cheesedirection;
	public Posn phone;
	public boolean good;
	public boolean holdingHammer = false;
	public int stage = 1;
	private Vector<String> rowSeven = new Vector(5);
	private Vector<String> rowSix = new Vector(5);
	private Vector<String> rowFive = new Vector(5);
	private Vector<String> visFeild = new Vector(35);
	public Posn cheeseLocation = new Posn();
	public Vector<Posn> doorLocations = new Vector();
	public Vector<Posn> rockLocations = new Vector();
	public Vector<Posn> keyLocations = new Vector();
	public Vector<Posn> hammerLocations = new Vector();
	//these are just examples of new items, not used until new objects are introduced,
	//public Vector<Posn> robotLocations = new Vector();
	//public Vector<Posn> raygunrLocations = new Vector();
	private String [][] growingMemory = new String [gridXSize][gridYSize]; //   25/16. middle is 26, 16 

    public int xtemp = 0;
    public Posn startposn = new Posn(0, 0);
    public Posn startSearchposn = new Posn(0, 0);
    public Posn location = new Posn(27, 18);
    public int dir = 180;
    public int startIndexX, startIndexY;
    public int waiter = 0;
    public int reachableCheckDepth = 10;
    public int swaiter = 0;
    public int obsPriority = 0;
    public NodeSearchTwo test;


    //registers with the grid, so the agent can be put into the map
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

    //initializes the growing memory to be full of blanks
    //growing memory is a big multdimentional array of posns, or square to explore.
    public void initGrowingMemory(){
    	for(int yy = 0; yy < gridYSize; yy++){
    		for(int xx = 0; xx < gridXSize; xx++){
    			growingMemory[xx][yy] = "?";
    			System.out.print(growingMemory[xx][yy]);
    		}
    		System.out.println();
    	}
    	System.out.println("initGrowingMemory worked");
    }

    //displays the growing memory into the terminal
    //prints out current memmory on screen
    public void printGrowingMemory(){
    	for(int yy = 0; yy < gridYSize; yy++){
    		for(int xx = 0; xx < gridXSize; xx++){
    			if((location.x == xx) && (location.y == yy)){
    				System.out.print(0);
    			}else{
    			System.out.print(growingMemory[xx][yy]);
    		}}
    		System.out.println();
    	}
    	System.out.println("initGrowingMemory worked");
    }
    //orients the agent in the direction of the cheese
    public void followYourNose(){
		switch(cheesedirection){
		case "f" : sendEffectorCommand("f"); break;
		case "b" : sendEffectorCommand("l"); break;
		case "l" : sendEffectorCommand("l"); break;
		case "r" : sendEffectorCommand("r"); break;
		case "h" : sendEffectorCommand("g"); finished = true; break;
		}
	}

	//exploration portion of the agent. there are different stages.
	//stage 1 is walk till it hits a wall, then to stage 2
	//stage 2 is turn, and walk while hugging the wall. every 4 steps look to the left to get a better view, and stop once it returns to its start location.
	//stage 3 is to turn towards the center of the map, and walk untill the agent hits a wall, and will walk and hug that wall.
	public void explore(){
		//walks straight till it hits a wall, 
		//then switches to stage 2, and saves this location index as the stopping point.
		if(stage == 1){
			//turns left and stage 2
			if(wallAtHuh(12)){
				sendEffectorCommand("l");
				stage = 2;
				startIndexX = location.x;
				startIndexY = location.y;
			}else{
				sendEffectorCommand("f");
				//followYourNose();
			}
		}
		// walks while hugging the wall on its left 
		// untill it returns to its starting location, (saved from stage 1)
		if(stage == 2){

			waiter++;// just to help with small areas.
			if(wallAtHuh(12)){
				sendEffectorCommand("l");
			}else{
				if(wallAtHuh(8)){
					sendEffectorCommand("f");
					//uncomment the following out to make exploration alittle better, but less energy efficient
					//looks around to get a larger view.
					xtemp++;
					if(xtemp == 4){//spins around to help get more data
						sendEffectorCommand("l");//completedExploreing();
						sendEffectorCommand("r");//completedExploreing();
						//sendEffectorCommand("r");//completedExploreing();
						//sendEffectorCommand("r");//completedExploreing();
						xtemp = 0;
					}
				}
				if(!wallAtHuh(8)){
					sendEffectorCommand("r");
					sendEffectorCommand("f");
					}
				}
			//checks to see if the agent has returned to its starting location.
			//if it has, it moves to the next stage of exploring
			//next stage is to explore the middle ground. 
			//this initializes the agent to explore the middle of the map. will walk around 1 island in the middle.
			//this should be skipped if the whole map is already seen, but ill add that later maybe.	
			if((location.x == startIndexX) && (location.y == startIndexY) && (waiter > 10)){
				stage = 3;
				//turns left and walks to the middle island
				sendEffectorCommand("l");
				while(!wallAtHuh(12)){
					sendEffectorCommand("f");
					xtemp++;
					//spins around to get a larger view.
					//comment this out if you want to save energy, but wont really have a big effect.
					if(xtemp == 4){
						sendEffectorCommand("r");//completedExploreing();
						sendEffectorCommand("r");//completedExploreing();
						sendEffectorCommand("r");//completedExploreing();
						sendEffectorCommand("r");//completedExploreing();
						xtemp = 0;
					}
				}
				//stores this as the new start location, and resets the waiter int
				waiter = 0;
				startIndexX = location.x;
				startIndexY = location.y;
			}
		}

		// walks around the islands in the middle untill its back where it started when it got to the island
		if(stage == 3){
			waiter++;
			if(wallAtHuh(12)){
				sendEffectorCommand("l");
			}else{
				if(wallAtHuh(8)){
					sendEffectorCommand("f");
					//comment the following out to make exploration alittle better, but less energy efficient
					xtemp++;
					if(xtemp == 4){
					sendEffectorCommand("l");//completedExploreing();
					sendEffectorCommand("r");//completedExploreing();
					//sendEffectorCommand("r");//completedExploreing();
					//sendEffectorCommand("r");//completedExploreing();
					xtemp = 0;}}
				if(!wallAtHuh(8)){
					sendEffectorCommand("r");
					sendEffectorCommand("f");
				}
			}
			if((location.x == startIndexX) && (location.y == startIndexY) && (waiter > 10)){
				stage = 4;
				System.out.println("stage 4, time to search.");
			}
		}
		//this is the searching area. for search agent, dont worry about doors or anything. just the cheese.	
		if(stage == 4){
			//determine first goal here. // and reachable
			if(InMemoryhuh("+") == true){
				System.out.println("*** pursuing cheese ***");
				findInMemory("+");
				System.out.println(" cheese is at " + cheeseLocation.x + " " + cheeseLocation.y);
				System.out.println(" I am at " + location.x + " " + location.y);
				//int 1 = cheese
				//int represents what we are searching for.
				search(cheeseLocation, 1);
				execute(phone, false);
				sendEffectorCommand("g");
				System.out.println("I ate the cheese. heck yes");
			}else{
				//if cheese is unseen, or is not reachable, then explore again
				stage = 1;
				explore();
			}
		}else{
			if(InMemoryhuh("+") == true){
				findInMemory("+");
				System.out.println("cheese is at " + cheeseLocation.x + "  " + cheeseLocation.y);
				if(reachable(location, cheeseLocation, 1, true, growingMemory) == true){
					if(reachable(location, cheeseLocation, 1, false, growingMemory) == true){
						stage = 4;
					}else{
						stage = 1;

						//set obstical priority
					}
				}
			}
			if(InMemoryhuh("@") && InMemoryhuh("T")){
				findInMemory("T");
				findInMemory("@");
				//if((reachable(location, hammerLocations.get(0), 2, false, growingMemory)) && (reachable(location, rockLocations.get(0), 2, false, growingMemory))){
					search(hammerLocations.get(0), 3);
					execute(phone, false);
					phone = null;
					sendEffectorCommand("g");
					for(int j = 0; j < revmoves.size(); j++){
						goTo(revmoves.get(j));
					}
					revmoves = new Vector();
					phone = null;
					search(rockLocations.get(0), 6);
					execute(phone, true);
					phone = null;
					faceObj();
					holdingHammer = true;
					sendEffectorCommand("f");
					//sendEffectorCommand("d");
					hammerLocations = new Vector();
					findInMemory("T");
				//}
			}
			if((holdingHammer == true) && (visFeild.get(12).equals("@"))){
				faceObj();
			}
			if(InMemoryhuh("#") && InMemoryhuh("K")){
				findInMemory("K");
				findInMemory("#");
				//if((reachable(location, keyLocations.get(0), 2, false, growingMemory)) && (reachable(location, doorLocations.get(0), 2, false, growingMemory))){
					search(keyLocations.get(0), 1);
					execute(phone, false);
					sendEffectorCommand("d");
					phone = null;
					sendEffectorCommand("g");
					for(int j = 0; j < revmoves.size(); j++){
						goTo(revmoves.get(j));
					}
					revmoves = new Vector();
					phone = null;
					search(doorLocations.get(0), 4);
					execute(phone, true);
					phone = null;
					faceObj();
					sendEffectorCommand("f");
					//sendEffectorCommand("d");
				//}
			}
			/*
			//the following is to add new objects, replace the # and K with whatever the new object is.
			if(InMemoryhuh("#") && InMemoryhuh("K")){
				findInMemory("K");
				findInMemory("#");
				//need to allocate a new vector for the new objects
				//raygunlocations already exists.
				search(raygunLocations.get(0), 5);
				execute(phone, false);
				phone = null;
				sendEffectorCommand("g");
				for(int j = 0; j < revmoves.size() - 1; j++){
					goTo(revmoves.get(j));
				}
				revmoves = new Vector();
				phone = null;
				//need to allocate a new vector for the new objects
				//robotlocations already exists.
				search(robotLocations.get(0), 7);
				execute(phone, true);
				phone = null;
				faceObj();
				sendEffectorCommand("u");
				sendEffectorCommand("f");
				//sendEffectorCommand("d");
			}
			*/
			explore();
		}
	}
	
	//faces the object it is trying to open/break and breaks it cuz my search doesnt solve direciton, just posns.
	public void faceObj(){
		if((visFeild.get(12).equals("#")) || (visFeild.get(12).equals("@"))){
			sendEffectorCommand("u");
		}else{
			sendEffectorCommand("l");
			faceObj();
		}
	}
	
	//tells weather or not the object we are looking for, has been seen before.
	//returns true if the object has been seen, else false.
	public boolean InMemoryhuh(String chfa){
		boolean found = false;
		String temp = "clal";
		for(int yi = 0; yi < gridYSize; yi++){
			for(int xi = 0; xi < gridXSize; xi++){
				temp = growingMemory[xi][yi];
				if(temp.equals(chfa)){
					found = true;
				}
			}
		}
		return found;
	}

	//a function that puts the position of the objects its looking for into a vector of locations of that object.
	//call this before searching for the objects. 
	public void findInMemory(String chfa){
		for(int yi = 0; yi < gridYSize; yi++)
			for(int xi = 0; xi < gridXSize; xi++){
				String temp = growingMemory[xi][yi];
				if(temp.equals(chfa)){
					if(chfa.equals("#")){doorLocations.add(new Posn(xi, yi));}
					if(chfa.equals("K")){keyLocations.add(new Posn(xi, yi));}
					if(chfa.equals("T")){hammerLocations.add(new Posn(xi, yi));}
					if(chfa.equals("@")){rockLocations.add(new Posn(xi, yi));}
					if(chfa.equals("+")){cheeseLocation = new Posn(xi, yi);}
					/*
					//
					if(chfa.equals("R")){robotLocations.add(new Posn(xi, yi));}
					if(chfa.equals("G")){raygunLocations.add(new Posn(xi, yi));}
					*/
					
				}
			}
		}

	//stores the current visual feild in the designated position in the growing memory.
	//takes an initial position regarding the agents location, and a direction, signifying where the agent is looking.
	public void storeNewData(Posn spot, int dir){
		growingMemory[spot.x][spot.y] = visFeild.get(7);

		//looking left, store looking left
		if(dir == 180)
		{
			int vfv = 0;
			for(int xparse = 1; vfv < 35; xparse --){
				growingMemory[spot.x + xparse][spot.y + 2] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+xparse][spot.y + 1 ] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+xparse][spot.y] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+xparse][spot.y-1] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+xparse][spot.y-2] = visFeild.get(vfv);
				vfv += 1;
			}
		}
			
		//looking right
		if(dir == 0){
			int vfv = 0;
			for(int xparse = -1; vfv < 35; xparse ++){
				growingMemory[spot.x + xparse][spot.y - 2] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+xparse][spot.y-1] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+xparse][spot.y] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+xparse][spot.y+1] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+xparse][spot.y+2] = visFeild.get(vfv);
				vfv += 1;
			}
		}
		//looking up
		if(dir == 90){
			int vfv = 0;
			for(int yparse = 1; vfv < 35; yparse --){
				growingMemory[spot.x-2][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x-1][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+1][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+2][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
			}
		}
		//looking down
		if(dir == 270){
				int vfv = 0;
			for(int yparse = -1; vfv < 35; yparse ++){
				growingMemory[spot.x+2][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x+1][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x-1][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
				growingMemory[spot.x-2][spot.y+yparse] = visFeild.get(vfv);
				vfv += 1;
			}
		}

	}


	//fuction that determines if it is done exploring the growing feild
	//returns true if the agent has a memory of length 375, else returns false
	/*
	public boolean doneExploringHuh(){
		int x = 0;
		for(int p = 0; p == 375; p++){
			if(!growingMemory[p].equals("_")){
				x += 1;
			}
		}
		if(x < 375){
			return true;
		}else{
			return false;
		}
	}
*/
	//searches tt a certain depth to see if the item can be reach,returns a boolean value.
	public boolean reachable(Posn nodei, Posn lookingfori, int itemp, boolean considerObjects, String[][] feildp){
		//if(visfeild == full){return true;}else{
		// bottom might not work well
		boolean foo = false;
		test = new NodeSearchTwo(nodei, lookingfori, feildp, itemp, considerObjects, reachableCheckDepth);
		foo = test.reachableHuh();
		obsPriority = test.obstical;
		if(foo == true)
			System.out.println("I can reach item " + itemp);
		if(foo == false)
			System.out.println("I can not reach item " + itemp);
		return foo;

	
		
	}

	public void processRetinalField(String calc){
		try{
		//System.out.println(calc);
		StringReader blah = new StringReader(calc);
		//StringReader poop = new StringReader("K");
		//StringReader pooph = new StringReader("T");
		//StringReader poopg = new StringReader("@");

		//System.out.println(poop.read());
		//System.out.println(pooph.read());
		//System.out.println(poopg.read());
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
			/*
			//need int values of the string you wish to implement.
			if(temp == <enter_int_value_of_string>){visFeild.add("R"); prev = 0;}
			if(temp == <enter_int_value_of_string>){visFeild.add("G"); prev = 0;}
			*/
		}}}catch (IOException e){
			System.out.print("something went wrong in processRetinalField ");
		}

		//shows the partial retnal feilds in the terminal, used for debugging.
		for (int counter = 0;counter < visFeild.size(); counter++){
			System.out.print(visFeild.get(counter) + " ");
			if(counter == 4 || counter == 9 || counter == 14 || counter == 19 || counter == 24 || counter == 29){
				System.out.println();
		}
		}
		nitRows();
		System.out.println();
		System.out.print("size of the vis-feild is " + visFeild.size());
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
	//searches the memory for a path to an object.
	public void search(Posn whatspot, int item){
		NodeSearch dr_Iba_is_awesome = new NodeSearch(location, whatspot, growingMemory, item);
		dr_Iba_is_awesome.run();
		phone = dr_Iba_is_awesome.finalAnswer;
		startSearchposn = dr_Iba_is_awesome.start;

	}
	//used to sort moves in the right order.
	public Vector<Posn> backwardsMoves = new Vector();
	public void makeVectFromPosns(Posn par){
		Posn tempPosn = par;
		while(true){
			backwardsMoves.add(tempPosn);
			if((tempPosn.parent.x == -9) && (tempPosn.parent.y == -9)){
				break;
			}else{
				Posn temp = tempPosn.parent;
				tempPosn = temp;
			}
		}
	}
	public Vector<Posn> moves = new Vector();
	public Vector<Posn> revmoves = new Vector();
	//executes the plan of moves that search returns.
	public void execute(Posn destination, boolean doorOrRock){
		makeVectFromPosns(destination);
		//makes the moves vector from the backwardsMoves Vector
		revmoves = backwardsMoves;
		int size = backwardsMoves.size();
		for(int i = 1; i <= size; i++){
			moves.add(backwardsMoves.get(size-i));//makes a stack of posns or something.
		}
		//makes the agent move according to the Posns!!!
		if(doorOrRock == true){
			for(int j = 0; j < size-1; j++){
				goTo(moves.get(j));
			}
		}else{
			for(int j = 0; j < size; j++){
				goTo(moves.get(j));
			}
		}
		moves = new Vector();
		backwardsMoves = new Vector();

	}


	//a fuction that moves to an adjacent posn.
	// will only work if the posns are adjacent
	//consumes a posn to move to, and executes the according actions.
	public void goTo(Posn destinatio){
		int movex = location.x - destinatio.x;
		int movey = location.y - destinatio.y;
		System.out.println("destinationx = " + destinatio.x + " and destinationy = " + destinatio.y);
		System.out.println("location.x = " + location.x + " and locationy = " + location.y);
		System.out.println("startSearchposn.x = " + startSearchposn.x + " and startSearchposny = " + startposn.y);
		System.out.println("movex = " + movex + "  movey = " + movey);
		if(movex <= -1){
			while(dir != 0){
				if(dir == 90){
					sendEffectorCommand("r");
				}
				else{
					sendEffectorCommand("l");
				}
			}
			sendEffectorCommand("f");
			System.out.println("i moved right one posn");
		}
		else if(movex >= 1)
		{
			while(dir != 180){
				if(dir == 90){
					sendEffectorCommand("l");
				}
				else{
					sendEffectorCommand("r");
				}
			}
			sendEffectorCommand("f");
			System.out.println("i moved left one posn");
		}
		else if(movey <= -1)
		{
			while(dir != 270){
				if(dir == 0){
					sendEffectorCommand("r");
				}
				else{
					sendEffectorCommand("l");
				}
			}
			sendEffectorCommand("f");
			System.out.println("i moved down one posn");
		}
		else if(movey >= 1)
		{
			while(dir != 90){
				if(dir == 0){
					sendEffectorCommand("l");
				}
				else{
					sendEffectorCommand("r");
				}
			}
			sendEffectorCommand("f");
			System.out.println("i moved up one posn");
		}
	}


	
	//tells if there is a wall at the location in the visual feild.
	//only works when the agent moves, need to change it for when the agent is planning.
	public boolean wallAtHuh(int loc){
		if((visFeild.get(loc).equals("*"))||
			(visFeild.get(loc).equals("#"))||
			//uncomment the following, and add the correct string accordingly.
			//(visFeild.get(loc).equals("G"))||
			//(visFeild.get(loc).equals("R"))||
			(visFeild.get(loc).equals("@"))){
			return true;
		}else{
			return false;
		}
	}

	//stores incoming information into variables.
	//loads and prossesses the vidual feild,
	public void getSensoryInfo(){
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
		    //gd.updateGDObjects(visField);
		   //db.updateLabels(heading, inventory, ground, energy, message, lastActionStatus, worldTime);
		}catch(Exception e) {}
    }


	//sends movement command to grid
	//also updates the location of the agent.
	//recieves new visual feild.
	public void sendEffectorCommand(String command){
		gridOut.println(command);
		int dx = 0;
		int dy = 0;
		if(dir == 0){dx = 1; dy = 0;}
		if(dir == 180){dx = -1; dy = 0;}
		if(dir == 90){dx = 0; dy = -1;}
		if(dir == 270){dx = 0; dy = 1;}
		//make sure no failed moves occur.
		switch(command){
			case "f" :  {location.x += dx;
						location.y += dy;}break;
			case "b" :  {location.x -= dx;
						location.y -= dy;}break;
			case "l" :  if(dir == 270){
							dir = 0;
						}else{
							dir += 90;}break;
			case "r" :  if(dir == 0){
							dir = 270;
						}else{
							dir -= 90;}break;
		}
		getSensoryInfo();
		storeNewData(location, dir);
		printGrowingMemory();
		System.out.println(InMemoryhuh("+"));
		System.out.println("i just tried to move " + command );
		}
	
	
	public static void main(String args[]) throws Exception {
		PlanningAgent Rob = new PlanningAgent();
		Rob.registerWithGrid("localhost", MAEDENPORT);
		Rob.getSensoryInfo();
		Rob.initGrowingMemory();
		Rob.explore();
		Rob.sendEffectorCommand("d");
		Rob.sendEffectorCommand("k");	
		
	}
}


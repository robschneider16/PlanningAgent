import java.util.Vector;
import java.util.Stack;

public class NodeSearchTwo{

	public Posn target;
	public Posn start;
	public int item = 1;
	public int obstical = 4;
	//public Stack<Posn> nodestack = new Stack();
	public Posn answer = new Posn(-1, -1, null);
	public Posn finalAnswer;
	private String[][] memory;
	public int reachableCheckDepth = 10;
	public boolean ignoreObsticals = true;

	public NodeSearchTwo(Posn nodei, Posn lookingfori, String[][] feild, int itemIndex, boolean ignoredoors, int depthvar){
		start = new Posn(nodei.x, nodei.y, new Posn(-9, -9, null));
		target = lookingfori;
		item = itemIndex;
		reachableCheckDepth = depthvar + 1;
		memory = feild;
		ignoreObsticals = ignoredoors;
		//item index is used to determine weather or not we look through doors and rocks or not.
	}

	public NodeSearchTwo(Posn nodei, Posn lookingfori, String[][] feild, int depthvar){
		start = new Posn(nodei.x, nodei.y, new Posn(-9, -9, null));
		target = lookingfori;
		reachableCheckDepth = depthvar + 1;
		memory = feild;
		//item index is used to determine weather or not we look through doors and rocks or not.
	}

	public boolean reachableHuh(){
		run();
		if((finalAnswer.x <= -1) && (finalAnswer.y <= -1)){
			return false;
		}else{
			return true;
		}
	}


	public void run(){
		int iterativeTempDepth = 1;
		while((answer.x <= -1) && (answer.y <= -1) && (iterativeTempDepth <= reachableCheckDepth)){
			dls(start, target, iterativeTempDepth);
			iterativeTempDepth += 1;
			System.out.println("search depth for item ppp " + item + " = " + iterativeTempDepth);
		}
		finalAnswer = answer;
	}

	public boolean checkIfVisited(Posn currentNode, Posn nextmover){
		boolean inAnswer = false;
		//looks through all the parents, and checks if any of the parent nodes are the same as next move
		Posn tempp = currentNode.parent;
		while((tempp.x != -9) || (tempp.y != -9)){
			if((tempp.x == nextmover.x) && (tempp.y == nextmover.y)){
				inAnswer = true;
				break;
			}
			Posn lasttemp = tempp;
			tempp = lasttemp.parent;
		}
		return inAnswer;
	}

	public void dls(Posn node, Posn lookingfor, int depth){
		if(depth > 0){
			if((node.x == lookingfor.x) && (node.y == lookingfor.y)){
				answer = node;
			}else{
			//for each child in expand(node)
				//parent posn == next posn, and that its not a wall, and that its in the growing memory.
				//right
				if((node.x != node.parent.x-1) &&
				   (node.x != 0) && 
				   (node.x != 37) && 
				   (node.y != 55) &&
				   (node.y != 0) && 
				   ((checkIfVisited(node, new Posn(node.x+1,node.y, node))) == false) &&
				   (!memory[node.x+1][node.y].equals("*")) && 
				   (!memory[node.x+1][node.y].equals("?")))
				  {
				  	if(ignoreObsticals == false){
				  		if((!memory[node.x+1][node.y].equals("#")) && (!memory[node.x+1][node.y].equals("@"))){
						dls(new Posn(node.x+1,node.y, node), lookingfor, depth-1);
					}}else{
						dls(new Posn(node.x+1,node.y, node), lookingfor, depth-1);
					}
					if(memory[node.x+1][node.y].equals("#")){
						obstical = 2;
					}
				
				} 
				//up
				if((node.y != node.parent.y-1) &&
				   (node.y != 0) && 
				   (node.y != 55) && 
				   (node.x != 37) &&
				   (node.x != 0) && 
				   ((checkIfVisited(node, new Posn(node.x,node.y+1, node))) == false) &&
				   (!memory[node.x][node.y+1].equals("*")) && 
				   (!memory[node.x][node.y+1].equals("?")))
				  {
					if(ignoreObsticals == false){
				  		if((!memory[node.x][node.y+1].equals("#")) && (!memory[node.x][node.y+1].equals("@"))){
						dls(new Posn(node.x ,node.y+1, node), lookingfor, depth-1);
					}}else{
						dls(new Posn(node.x,node.y+1, node), lookingfor, depth-1);
					}
					if(memory[node.x][node.y+1].equals("#")){
						obstical = 2;
					}
				
				}
				//left
				if((node.x != node.parent.x+1) &&
				   (node.x != 0) && 
				   (node.x != 37) && 
				   (node.y != 55) &&
				   (node.y != 0) && 
				   ((checkIfVisited(node, new Posn(node.x-1,node.y, node))) == false) &&
				   (!memory[node.x-1][node.y].equals("*")) && 
				   (!memory[node.x-1][node.y].equals("?")))
				  {
					if(ignoreObsticals == false){
				  		if((!memory[node.x-1][node.y].equals("#")) && (!memory[node.x-1][node.y].equals("@"))){
						dls(new Posn(node.x-1,node.y, node), lookingfor, depth-1);
					}}else{
						dls(new Posn(node.x-1,node.y, node), lookingfor, depth-1);
					}
					if(memory[node.x-1][node.y].equals("#")){
						obstical = 2;
					
				}
				}
				//down
				if((node.y != node.parent.y+1) &&
				   (node.y != 0) && 
				   (node.y != 55) && 
				   (node.x != 37) &&
				   (node.x != 0) && 
				   ((checkIfVisited(node, new Posn(node.x,node.y-1, node))) == false) &&
				   (!memory[node.x][node.y-1].equals("*")) && 
				   (!memory[node.x][node.y-1].equals("?")))
				  {
					if(ignoreObsticals == false){
				  		if((!memory[node.x][node.y-1].equals("#")) && (!memory[node.x][node.y-1].equals("@"))){
							dls(new Posn(node.x ,node.y-1, node), lookingfor, depth-1);
						}
					}else{
						dls(new Posn(node.x,node.y-1, node), lookingfor, depth-1);
					}
					if(memory[node.x][node.y-1].equals("#")){
						obstical = 2;
					
					}
				}	
			}
		}
	}
}

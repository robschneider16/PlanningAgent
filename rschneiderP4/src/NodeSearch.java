import java.util.Vector;
import java.util.Stack;

public class NodeSearch{

	public Posn target;
	public Posn start;
	public int goal = 1;
	//public Stack<Posn> nodestack = new Stack();
	public Posn answer = new Posn(-1, -1, null);
	public Posn finalAnswer;
	private String[][] memory;

	public NodeSearch(Posn nodei, Posn lookingfori, String[][] feild, int itemIndex){
		start = new Posn(nodei.x, nodei.y, new Posn(-9, -9, null));
		target = lookingfori;
		memory = feild;
		goal = itemIndex;
		//item index is used to determine weather or not we look through doors and rocks or not.
	}

	public void run(){
		int iterativeTempDepth = 1;
		while((answer.x <= -1) && (answer.y <= -1)){
			dls(start, target, iterativeTempDepth);
			iterativeTempDepth += 1;
			System.out.println("Search depth for itme " + goal + " = " + iterativeTempDepth);
			System.out.println("Searching for " + target.x + " :: " + target.y);
			System.out.println("starting location is" + start.x + " :: " + start.y);
		}
		finalAnswer = answer;
	}

	public boolean checkIfVisited(Posn currentNode, Posn nextmover){
		boolean inAnswer = false;
		//looks through all the parents, and checks if any of the parent nodes are the same as next move
		Posn tempp = currentNode.parent;
		while((tempp.x != -9) && (tempp.y != -9)){
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
					dls(new Posn(node.x+1,node.y, node), lookingfor, depth-1);
				}
				//up
				if((node.y != node.parent.y-1) &&
				   (node.y != 0) && 
				   (node.y != 55) && 
				   (node.x != 37) &&
				   (node.x != 0) && 
				   ((checkIfVisited(node, new Posn(node.x, node.y+1, node))) == false) &&
				   (!memory[node.x][node.y+1].equals("*")) && 
				   (!memory[node.x][node.y+1].equals("?")))
				  {
					dls(new Posn(node.x, node.y+1, node), lookingfor, depth-1);
				}
				//left
				if((node.x != node.parent.x+1) &&
				   (node.x != 0) && 
				   (node.x != 37) && 
				   (node.y != 55) &&
				   (node.y != 0) && 
				   ((checkIfVisited(node, new Posn(node.x-1 ,node.y, node))) == false) &&
				   (!memory[node.x-1][node.y].equals("*")) && 
				   (!memory[node.x-1][node.y].equals("?")))
				  {
					dls(new Posn(node.x-1, node.y, node), lookingfor, depth-1);
				}
				//down
				if((node.y != node.parent.y+1) &&
				   (node.y != 0) && 
				   (node.y != 55) && 
				   (node.x != 37) &&
				   (node.x != 0) && 
				   ((checkIfVisited(node, new Posn(node.x ,node.y-1, node))) == false) &&
				   (!memory[node.x][node.y-1].equals("*")) && 
				   (!memory[node.x][node.y-1].equals("?")))
				  {
					dls(new Posn(node.x, node.y-1, node), lookingfor, depth-1);
				}
			}
		}
	}






}
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class AdjacentNodes{
	int rowPosition, colPosition;
	
	public AdjacentNodes(int rowPosition, int colPosition){
		this.rowPosition = rowPosition;
		this.colPosition = colPosition;
	}
}

class State implements Comparable<State>{
	
	int rowPosition, colPosition, value;
	String []nextState = null;
	List<AdjacentNodes> adjacentNodesList = null;
	
	public String[] getNextState() {
		return nextState;
	}
	
	public void setNextState(String[] nextState) {
		this.nextState = nextState;
	}

	public State(int rowPosition, int colPosition, int value, List<AdjacentNodes> adjacentNodesList){
		this.rowPosition = rowPosition;
		this.colPosition = colPosition;
		this.value = value;
		this.adjacentNodesList = adjacentNodesList;
	}

	@Override
	public int compareTo(State o) {		
		if(value < o.value){
			return 1;
		}
		else if(value > o.value){
			return -1;
		}
		return 0;
	}	
}

public class homework {

	public static boolean visitedFruits[][];
	public static int size;
	public static int noOfFruits;
	public static float time;
	public static final int INF = Integer.MAX_VALUE;
	
	public static HashMap<Integer, State> positionHashmap = new HashMap<>();
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		try {
			/* Reading the input */
			FileReader fr = new FileReader("input.txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			String lines[];
			
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line.trim() + " ");
			}
			lines = buffer.toString().split(" ");
			size = Integer.parseInt(lines[0]); 
			noOfFruits = Integer.parseInt(lines[1]);;
			time = Float.parseFloat(lines[2]);
			System.out.println(time);
			
			String gameBoard[] = new String[size];
			System.arraycopy(lines,3, gameBoard, 0, size);
			for(int i=0;i<size;i++){
				System.out.println(gameBoard[i]);
			}
			/* End of Reading the Input */
			
			int depth = calculateDepth();
			
			System.out.println("Depth : "+depth);
			
			FileWriter fw = new FileWriter("output.txt");
			PrintWriter pw = new PrintWriter(fw);
			if(!terminalTest(gameBoard)){
             	int v = maxValue(gameBoard,-INF, INF,depth);
				
				System.out.println("Final : "+v);
			
				State selectedState;
				
				if(positionHashmap.get(v)==null){
					System.out.println("NULL");
					selectedState = generateSuccessorStates(gameBoard).get(0);
					selectedState.setNextState(generateNewBoardState(gameBoard, selectedState));
				}
				else{
					selectedState = positionHashmap.get(v);
				}
				
				System.out.println(selectedState.value);
				
				String selectedBoard[] = generateNewBoardState(gameBoard, selectedState);//selectedState.getNextState();
				pw.println((char)(selectedState.colPosition+65)+""+(selectedState.rowPosition+1));
				System.out.println((char)(selectedState.colPosition+65)+""+(selectedState.rowPosition+1));
				for(int i=0;i<size;i++){
					System.out.println(selectedBoard[i]);
					pw.println(selectedBoard[i]);
				}
				pw.close();
			}
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
	private static int calculateDepth() {
		
		if(time<0.5){
			return 1;
		}
		if(size >= 15){
			if(time >= 150)
				return 3;
			else if(time >= 20)
				return 2;
			else
				return 1;
		}
		else if(size >= 11){
			if(time >= 30)
				return 4;
			else if(time >= 15)
				return 3;
			else if(time >= 5)
				return 2;
			else
				return 1;
		}
		else if(size >= 5){
			if(time >= 100)
				return 6;
			else if(time >= 15)
				return 5;
			else if(time >= 5)
				return 4;
			else
				return 3;
		}
		else{
			return 6;
		}
	}

	private static String[] generateNewBoardState(String[] state, State s) {
		String newBoardState[] = new String[size];
		System.arraycopy(state,0, newBoardState, 0, size);
		if(s.adjacentNodesList!=null)
			createBoardStateWithStars(newBoardState, s.adjacentNodesList);
		boardStateAfterGravity(newBoardState);
		return newBoardState;
	}
	
	private static boolean isPromising(List<State> states) {
		int count = 0;
		for(State s : states){
			if(s.adjacentNodesList.size() == 1){
				count++;
			}
		}
		if(count == states.size()){
			return false;
		}
		return true;
	}

	private static int maxValue(String[] state, int alpha, int beta, int depth) {	
		if(terminalTest(state) || depth == 0){
			return 0;
		}
		int v = -INF;
		
		List<State> states = generateSuccessorStates(state);
		if(!isPromising(states)){
			return 0;
		}
		
		for(State s : states){
			String newBoardState[] = generateNewBoardState(state, s);	
			s.setNextState(newBoardState);
			
			v = Math.max(v, (s.value*s.value)+minValue(newBoardState, alpha, beta, depth-1));
			if(v>=beta){
				return v;
			}
			if(alpha!=v){
				alpha = Math.max(alpha, v);
				if(!positionHashmap.containsKey(alpha) ||(alpha==v && positionHashmap.get(alpha).value < s.value)){
					positionHashmap.put(alpha, s);
				}
			}
		}
		return v;
	}

	private static int minValue(String[] state, int alpha, int beta, int depth) {
		if(terminalTest(state) || depth == 0){
			return 0;
		}
		int v = INF;
		
		List<State> states = generateSuccessorStates(state);
		if(!isPromising(states)){
			return 0;
		}
		
		for(State s : states){
			String newBoardState[] = generateNewBoardState(state, s);
			s.setNextState(newBoardState);
			
			v = Math.min(v, (-s.value*s.value)+maxValue(newBoardState, alpha, beta, depth-1));
			if(v<=alpha){
				return v;
			}
			beta = Math.min(beta, v);
		}
		return v;
	}
	
	private static boolean terminalTest(String[] state) {	
		for(String s : state){
			for(int col = 0; col<size; col++){
				if(s.charAt(col)!='*'){
					return false;
				}
			}
		}
		return true;
	}
	
	private static void boardStateAfterGravity(String[] newBoardState) {
		boolean flag = true;
		for(int col = 0; col<size;col++){
			flag = true;
			for(int j = 0;j<size && flag;j++){
				flag = false;
				for(int row = size-1; row>0; row--){
					if(newBoardState[row].charAt(col) == '*'){
						flag = true;
						String upperValue = newBoardState[row-1].charAt(col)+"";
						String currentValue = newBoardState[row].charAt(col)+"";
						newBoardState[row] = newBoardState[row].substring(0, col).concat(upperValue).concat(newBoardState[row].substring(col+1));
						newBoardState[row-1] = newBoardState[row-1].substring(0, col).concat(currentValue).concat(newBoardState[row-1].substring(col+1));
					}
				}
			}
		}
	}

	private static void createBoardStateWithStars(String[] newGameBoard, List<AdjacentNodes> adjacentNodesList) {
		for(AdjacentNodes adjacentNodes : adjacentNodesList){
			int row = adjacentNodes.rowPosition;
			int col = adjacentNodes.colPosition;
			newGameBoard[row] = newGameBoard[row].substring(0, col).concat("*").concat(newGameBoard[row].substring(col+1, size));
		}	
	}

	private static List<State> generateSuccessorStates(String gameBoard[]) {
		visitedFruits = new boolean[size][size];
		List<State> successorStatesList = new ArrayList<>();
				
		int count = 0;
		for(int row = 0;row<size;row++){
			for(int col = 0;col<size;col++){
				if(!visitedFruits[row][col] && gameBoard[row].charAt(col)!='*'){
					List<AdjacentNodes> adjacentNodesList = new ArrayList<>();
					count = countAdjacentFruitsOfSameType(gameBoard,row, col, gameBoard[row].charAt(col), adjacentNodesList);
					State state = new State(row, col, count, adjacentNodesList);
					successorStatesList.add(state);
				}
			}
		}
		Collections.sort(successorStatesList);
		return successorStatesList;
	}

	private static int countAdjacentFruitsOfSameType(String gameBoard[],int row, int col, char fruitType, List<AdjacentNodes> adjacentNodesList) {
		int above = 0, below = 0, left = 0, right = 0;
		if(gameBoard[row].charAt(col) == '*' || gameBoard[row].charAt(col) != fruitType){
			return 0;
		}
		else{
			visitedFruits[row][col] = true;
			adjacentNodesList.add(new AdjacentNodes(row, col));
			if(row>0 && !visitedFruits[row-1][col]){
				above = countAdjacentFruitsOfSameType(gameBoard, row-1, col, fruitType, adjacentNodesList); 
			}
			if(row < size-1 && !visitedFruits[row+1][col]){
				below = countAdjacentFruitsOfSameType(gameBoard, row+1, col, fruitType, adjacentNodesList); 
			}
			if(col>0 && !visitedFruits[row][col-1]){
				left = countAdjacentFruitsOfSameType(gameBoard, row, col-1, fruitType, adjacentNodesList); 
			}
			if(col < size-1 && !visitedFruits[row][col+1]){
				right = countAdjacentFruitsOfSameType(gameBoard, row, col+1, fruitType, adjacentNodesList); 
			}
			return above + below + left + right + 1;
		}	
	}
}
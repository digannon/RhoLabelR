import java.util.Hashtable;
import java.util.LinkedList;

public class GraphManager {
	LinkedList<String> nameList;
	Hashtable<String, int[][]> graphTable;
	int numGraphs;
	
	LinkedList labelList;
	Hashtable<String, int[][]> labelTable;
	int numLabels;
	
	public GraphManager(){
		graphTable = new Hashtable<String, int[][]>();
		nameList = new LinkedList<String>();
		labelList = new LinkedList<String>();
		labelTable = new Hashtable<String, int[][]>();
		
		int[][] blank = {{}};
		String string = toString(blank);
		labelTable.put(string, blank);
		labelList.add(string);
		numLabels = 1;
		
		String example1 = "UnD-M=1";
		int[][] exampleGraph1 = {{1, 4}, {0, 2, 5}, {1, 3, 4}, {2, 4}, {0, 2, 3, 7},{1, 6, 8}, {5, 7}, {4, 6, 8}, {5, 7}};
		nameList.add(example1);
		graphTable.put(example1, exampleGraph1);
		
		String example2 = "UnD-M=2";
		int[][] exampleGraph2 = {{1, 1, 4}, {0, 0, 2, 5}, {1, 3, 4}, {2, 4}, {0, 2, 3, 7}, {1, 6, 8}, {5, 7, 7}, {4, 6, 6, 8}, {5, 7}};
		nameList.add(example2);
		graphTable.put(example2, exampleGraph2);
		
		String example3 = "D-M=1";
		int[][] exampleGrap3 = {{1, 4}, {2, 5}, {3, 4}, {4}, {7},{6, 8}, {7}, {8}, {}};
		nameList.add(example3);
		graphTable.put(example3, exampleGrap3);
		
		String example4 = "TriDecomp";
		int[][] exampleGrap4 = {{2, 3, 4}, {3}, {0, 4}, {0, 1, 4}, {0, 2, 3}};
		nameList.add(example4);
		graphTable.put(example4, exampleGrap4);
		
		
		
		numGraphs = 4;
	}
	
	public static String toString(int[][] graph){
		int i, j;
		String string = "";
		for(i = 0; i < graph.length; i++){
			string = string + "{";
			if(graph[i].length > 0){
				string = string + graph[i][0];
				for(j = 1; j < graph[i].length; j++)
					string = string + ", " + graph[i][j];
			}
			string = string + "}";
		}
		return string;
	}
	
	public static int charToInt(char c){
		int i = (int)c - 48;
		if(i < 0 || i > 9)
			i = -1;
		return i;
	}
	
	private int[][] readInputString(String input){
		char[] array = input.toCharArray();
		LinkedList nodeList = new LinkedList();
		LinkedList neighborList = new LinkedList();
		int index = 0;
		int num, i, j;
		while(index < array.length){
			num = 0;
			while(index < array.length && array[index] != '{')
				index++;
			if(index < array.length){
				while(index < array.length && array[index] != '}'){
					while(index < array.length && charToInt(array[index]) < 0)
						index++;
					if(index < array.length){
						num = 0;
						while(index < array.length && charToInt(array[index]) >= 0){
							num = num*10 +charToInt(array[index]);
							index++;
						}
						neighborList.add(num);
					}
				}
				if(index < array.length){
					nodeList.add(neighborList);
					neighborList = new LinkedList();
					index++;
				}
			}
		}
		
		Object[] nodeArray = nodeList.toArray();
		Object[] neighborArray;
		int[][] graphArray = new int[nodeArray.length][];
		for(i = 0; i < nodeArray.length; i++){
			neighborArray = ((LinkedList)nodeArray[i]).toArray();
			graphArray[i] = new int[neighborArray.length];
			for(j = 0; j < neighborArray.length; j++)
				graphArray[i][j] = (int)neighborArray[j];
		}
		nodeList = null;
		neighborList = null;
		return graphArray;
	}
	
	//empty return indicates everything went well
	//string return means it didn't work
	public String makeGraphArray(String input){
		int newLineIndex = input.indexOf("\n");
		if(newLineIndex >= 0){
			String name = input.substring(0, input.indexOf("\n"));
			if(name.length() > 0){
				if(!graphTable.containsKey(name)){
					input = input.substring(newLineIndex);
					int[][] graphArray = readInputString(input);
					graphTable.put(name, graphArray);
					nameList.add(name);
					numGraphs++;
					return "";
				}
				else return "Graph " + name + " already exists!";
			}
			return " ";
		}
		return "Bad Graph Input.";
	}
	
	public String makeLabelArray(String input){
		int[][] labelArray = readInputString(input);
		String string = toString(labelArray);
		if(string.length() > 0){
			if(!labelTable.containsKey(string)){
				labelTable.put(string,  labelArray);
				labelList.add(string);
				numLabels++;
				return "";
			}
			return "Label sequence already exists!";
		}
		return "Bad Label Input";
	}
	
	public boolean removeGraph(int index){
		try{
			String name = (String)nameList.get(index);
			graphTable.remove(name);
			nameList.remove(index);
			numGraphs--;
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public boolean removeLabel(int index){
		try{
			String name = (String)labelList.get(index);
			if(name.equals("{}"))
				return false;
			labelTable.remove(name);
			labelList.remove(index);
			numLabels--;
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public int[][] get(int index){
		try{
			String name = (String)nameList.get(index);
			return (int[][])graphTable.get(name);
		}
		catch(Exception e){
			return null;
		}
	}
	
	public int[][] getLabel(int index){
		try{
			String name = (String)labelList.get(index);
			return(int[][])labelTable.get(name);
		}
		catch(Exception e){
			return null;
		}
	}
}


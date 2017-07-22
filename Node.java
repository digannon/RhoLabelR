import java.util.LinkedList;

public class Node {
	private int label;
	private boolean[] option;
	private int K;
	private int free;
	private Node[] downStream;
	private Node[] upStream;
	private int minOption;
	private int humanIndex;
	private int heapIndex;
	
	public int label(){               return label;}
	public boolean option(int label){ return option[label];}
	public int K(){                   return K;}
	public int free(){                return free;}
	public int numDownStream(){       return downStream.length;}
	public int minOption(){           return minOption;}
	public int humanIndex(){          return humanIndex;}
	public Node downStream(int index){  return downStream[index];}
	public Node upStream(int index){   return upStream[index];}
	public int heapIndex(){           return heapIndex;}
	public int numUpStream(){         return upStream.length;}
	
	public void setLabel(int label){                           this.label = label; }
	public void setHumanIndex(int index){                      this.humanIndex = index;}
	public void setHeapIndex(int index){                       this.heapIndex = index;}
	
	public Node(int K, int numDownStream, int numUpStream){
		label = -1;
		option = new boolean[K];
		for(int i = 0; i < K; i++)
			option[i] = true;
		minOption = 0;
		
		this.K = K;
		free = K;
		
		downStream = new Node[numDownStream];
		upStream = new Node[numUpStream];
	}
	
	public Node(Node node){
		label = node.label();
		K = node.K();
		option = new boolean[K];
		for(int i = 0; i < K; i++)
			option[i] = node.option(i);
		minOption = node.minOption();
		
		free = node.free();

		downStream = new Node[node.numDownStream()];
		upStream = new Node[node.numUpStream()];
		heapIndex = node.heapIndex();
		humanIndex = node.humanIndex();
	}
	
	public void off(int label){
		if( option[label] ){
			option[label] = false;
			free--;
			while(minOption < K && !option[minOption])
				minOption++;
		}
	}
	
	public void on(int label){
		if( ! option[label] ){
			option[label] = true;
			free++;
			if(label < minOption)
				minOption = label;
		}
	}
	
	public void setNeighbor(int neighborIndex, Node neighbor){ 
		downStream[neighborIndex] = neighbor;
		neighbor.setUpStream(this);
	}
	
	public void setUpStream(Node upNode){
		for(int i = 0; i < numUpStream(); i++)
			if(upStream[i] == null){
				upStream[i] = upNode;
				return;
			}
	}
	
	public String optionString(){
		String string = "";
		for(int i = 0; i < K; i++)
			if(option[i])
				string = string + i + " ";
		return string;
	}
	
}


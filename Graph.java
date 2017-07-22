import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Graph {
	private int N;           //number of nodes
	private int U;           //number of unlabeled nodes
	private int F;           //numbers of labeled nodes whos neighbors are all labeled
	private int O;       //the number of times each edge label should appear
	private Node[] que;
	private int K; //the Complete graph to be decomposed
	private boolean directed;
	public LinkedList usedEdgeLabels; //edges labels with edgeOption(label) == 0
	private int[] edgeOption;
	
	public int N(){						        return N;}
	public int U(){					            return U;}
	public int F(){                             return F;}
	public Node que(int index){					return que[index];}
	public int K(){                             return K;}
	public boolean directed(){                  return directed;}
	public int O(){                             return O;}
	public int edgeOption(int label){           return edgeOption[label-1];}
	public void decEdgeOption(int label){       edgeOption[label-1] = edgeOption[label-1] -1;}
	public int[] edgeOption(){                  return edgeOption;}
	
	
	public Graph(int[][] edgeAdjacency, int K, boolean directed, int O){
		int i, j;
		if(K < 0){
			this.K = 0;
			for(i = 0; i < edgeAdjacency.length; i++)
				this.K = this.K + edgeAdjacency[i].length;
			if(K == -1){
				this.K = this.K/O;
				this.K++;
			}
			else
				this.K = this.K*O/2;
		}
		else this.K = K;
		this.directed = directed;
		this.O = O;
		N = edgeAdjacency.length;
		if(K == -2){
			O = 1;
		}
		if(!directed){
			edgeOption = new int[this.K/2];
			for(i = 0; i < this.K/2; i++)
				edgeOption[i] = O;
		}
		else{
			edgeOption = new int[this.K-1];
			for(i = 0; i < this.K-1; i++)
				edgeOption[i] = O;
		}
		U = N;
		F = 0;
		usedEdgeLabels = new LinkedList();

		int[] upStreamCount = new int[N];
		for(i = 0; i < N; i++)
			upStreamCount[i] = 0;
		
		for(i = 0; i < N; i++)
			for(j = 0; j < edgeAdjacency[i].length; j++)
				upStreamCount[edgeAdjacency[i][j]]++;
			
		
		
		//Create Nodes
		que = new Node[N];
		for(i = 0; i < N; i++){
			que[i] = new Node(this.K, edgeAdjacency[i].length, upStreamCount[i]);
			que[i].setHumanIndex(i);
			que[i].setHeapIndex(i);
		}

		//Set Neighbors
		for(i = 0; i < N; i++)
			for(j = 0; j < edgeAdjacency[i].length; j++)
				que[i].setNeighbor(j, que[edgeAdjacency[i][j]]);
	}
	
	public Graph(Graph graph){
		int i, j;
		K = graph.K();
		directed = graph.directed();
		O = graph.O();
		N = graph.N();
		edgeOption = graph.edgeOption().clone();
		U = graph.U();
		F = graph.F();
		usedEdgeLabels = (LinkedList)graph.usedEdgeLabels.clone();
			
		//Create Nodes
		que = new Node[N];
		for(i = 0; i < N; i++)
			que[i] = new Node(graph.que(i));
		
		//Set Neighbors
		for(i = 0; i < N; i++)
			for(j = 0; j < que[i].numDownStream(); j++)
				que[i].setNeighbor(j, que[graph.que[i].downStream(j).heapIndex()]);
	}
	
	public void swap(int index1, int index2){
		Node temp = que[index1];
		que[index1] = que[index2];
		que[index2] = temp;
		
		que[index1].setHeapIndex(index1);
		que[index2].setHeapIndex(index2);
	}
	
	public void off(int index, int label){
		que[index].off(label);
		if(index < U){
			while(que[index].free() < que[index/2].free()){
				swap(index, index/2);
				index = index/2;
			}
		}
	}
	
	public boolean setHumanLabel(int humanIndex, int label){
		for(int i = 0; i < N; i++)
			if(que[i].humanIndex() == humanIndex)
				return setLabel(i, label);
		return false;
	}
	
	public boolean setLabel(int index, int label){
		int i, j, num, edgeLabel;
		//Set Label
		que[index].setLabel(label);
		//Remove From Heap
		U--;
		swap(index, U);
		//Take away option
		for(i = 0; i < U; i++)
			off(i, label);
		
		//Get newly acquired edge labels 
		num = que[U].numUpStream();
		LinkedList newEdgesList = new LinkedList();
		for(i = 0; i < num; i++){
			if(que[U].upStream(i).label() >= 0){
				edgeLabel = getEdgeLabel(que[U].upStream(i).label(), label);
				decEdgeOption(edgeLabel);
			//(make don't set any options below 0)
				if(edgeOption(edgeLabel) < 0)
					return false;
				else if(edgeOption(edgeLabel) == 0)
					newEdgesList.add(edgeLabel);
				
			}
		}
		if(directed){
			num = que[U].numDownStream();
			for(i = 0; i < num; i++){
				if(que[U].downStream(i).label() >= 0){
					edgeLabel = getEdgeLabel(label, que[U].downStream(i).label());
					decEdgeOption(edgeLabel);
					if(edgeOption(edgeLabel) < 0)
						return false;
					else if(edgeOption(edgeLabel) == 0)
						newEdgesList.add(edgeLabel);
				}
			}
			
		}
		   
		
		   //Turn off options downStream from newly labeled node corresponding to previously acquired edge labels
		if(!usedEdgeLabels.isEmpty()){
			Object[] oldEdgeLabel = usedEdgeLabels.toArray();
			edgeOff(U, oldEdgeLabel);
		}
		   //Turn off options downStream from labeled unfinished nodes corresponding to newly acquired edge labels
		if(!newEdgesList.isEmpty()){
			Object[] newEdgeLabel = newEdgesList.toArray();
			for(i = U; i < N-F; i++){
				if(edgeOff(i, newEdgeLabel))
					i--;
			}
		}
		//add new edge labels to old list
		while(!newEdgesList.isEmpty())
			usedEdgeLabels.add(newEdgesList.poll());

		return true;
	}
	
	//Turns off all options of neighbors of que[index] given que[index].label and edgeLabel[]
	//returns true if this cases que[index] to finish
	public boolean edgeOff(int index, Object[] edgeLabel){
		int i, j, num;
		LinkedList offLimitNodeList = new LinkedList();
		Object[] offLimitNodeLabel;
		for(i = 0; i < edgeLabel.length; i++){
			offLimitNodeList.add(getClockWise(que[index].label(), (int)edgeLabel[i]));
			if(!directed)
				offLimitNodeList.add(getCounterClockWise(que[index].label(), (int)edgeLabel[i]));
		}
		offLimitNodeLabel = offLimitNodeList.toArray();
		num = que[index].numDownStream();
		boolean finished = true;
		Node node;
		for(i = 0; i < num; i++){
			node = que[index].downStream(i);
			if(node.label() < 0){
				finished = false;
				for(j = 0; j < offLimitNodeLabel.length; j++)
					off(node.heapIndex(), (int)offLimitNodeLabel[j]);
			}
		}
		//If (directed), then also turn off options upStream
		if(directed){
			offLimitNodeList = new LinkedList();
			for(i = 0; i < edgeLabel.length; i++)
				offLimitNodeList.add(getCounterClockWise(que[index].label(), (int)edgeLabel[i]));
			offLimitNodeLabel = offLimitNodeList.toArray();
			num = que[index].numUpStream();
			for(i = 0; i < num; i++){
				node = que[index].upStream(i);
				if(node.label() < 0)
					for(j = 0; j < offLimitNodeLabel.length; j++)
						off(node.heapIndex(), (int)offLimitNodeLabel[j]);
			}
		}
		node = null;
		offLimitNodeList = null;
		offLimitNodeLabel = null;
		if(finished){
			F++;
			swap(index, N-F);
			return true;
		}
		return false;
	}
	
	public int getEdgeLabel(int start, int destination){
		int def;
		if(!directed){
			def = Math.abs(start-destination);
			return Math.min(def, K - def);
		}
		def = destination - start;
		if(def < 0)
			def = def + K;
		return def;
	}
	
	public int getClockWise(int start, int edgeLength){
		int dest = start + edgeLength;
		if(dest >= K)
			dest = dest - K;
		return dest;
	}
	
	public int getCounterClockWise(int start, int edgeLength){
		int dest = start - edgeLength;
		if(dest < 0)
			dest = dest + K;
		return dest;
	}
	
	private void sortByHumanIndex(){
		int i;
		int heapSize = N;
		
		//Make max heap
		for(i = heapSize/2; i >= 0; i--)
			maxHeapifyByHumanIndex(i, heapSize);
		
		
		for(i = 0; i < N; i++){
			heapSize--;
			swap(0, heapSize);
			maxHeapifyByHumanIndex(0, heapSize);
		}
	}
	
	private void maxHeapifyByHumanIndex(int index, int heapSize){
		int childIndex = index * 2;
		if(childIndex < heapSize){
			if(childIndex + 1 < heapSize){
				if( que[childIndex].humanIndex() >= que[childIndex+1].humanIndex() ){
					if( que[childIndex].humanIndex() > que[index].humanIndex() ){
						swap(index, childIndex);
						maxHeapifyByHumanIndex(childIndex, heapSize);
					}
				}
				else if( que[childIndex+1].humanIndex() > que[index].humanIndex() ){
					swap(index, childIndex+1);
					maxHeapifyByHumanIndex(childIndex+1, heapSize);
				}
			}
			else if( que[childIndex].humanIndex() > que[index].humanIndex() ){
				swap(index, childIndex);
				maxHeapifyByHumanIndex(childIndex, heapSize);
			}
		}
	}

	public void killEdgeOption(int label){
		edgeOption[label-1] = 0;
		usedEdgeLabels.add(label);
	}
	
	public String toString(){
		Graph graph = new Graph(this);
		graph.sortByHumanIndex();
		int i, j;

		String string = " K = " + K + "\n";
		for(i = 0; i < N; i++){
			string = string + "\n  Node " + i + ": \tLabel: " + graph.que[i].label() + "\tNeighbors: ";
			for(j = 0; j < graph.que[i].numDownStream(); j++)
				string = string + graph.que[i].downStream(j).humanIndex() + " ";
		}
		string = string + "\n\n  https://www.linkedin.com/in/danigannon/";
		return string;
	}
	
	public void print(){
		int i, j;
		String string = "";
		for(i = 0; i < N; i++){
			string = string + "\nNode " + que[i].humanIndex() + ": \tLabel: " + que[i].label() + "\tOption: " + que[i].optionString() + "\tNeighbors: ";
			for(j = 0; j < que[i].numDownStream(); j++)
				string = string + que[i].downStream(j).humanIndex() + " ";
		}
		string = string + "\n";
		System.out.print(string);
	}
	
	
	public boolean solve(){
		if(que[0].label() >= 0){
			JFrame window = new JFrame("Found One!");
			window.setSize(400, 200);
			JTextArea text = new JTextArea(this.toString());
			JScrollPane scroll = new JScrollPane(text);
			window.add(scroll);
			window.setVisible(true);
			return true;
		}
		if(que[0].free() <= 0){
			return false;
		}
		
		Graph graph;
		while(que[0].minOption() < K){
			graph = new Graph(this);
			if(graph.setLabel(0, graph.que[0].minOption())){
				if(graph.solve()){
					return true;
				}
				else{
					off(0, que[0].minOption());
				}
			}
			else{
				off(0, que[0].minOption());
			}
		}
		return false;
	}
	
}


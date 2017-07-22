import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RhoLabelR {
	public static final double version = 1.0;
	public static final int RESET = -1;
	public static final int HOME = 0;
	public static final int SET = 1;
	
	boolean directed;
	int multiplicity;
	boolean komplete;
	int K;
	GraphManager GM;
	private boolean flip;
	
	public RhoLabelR(){
		directed = false;
		multiplicity = 1;
		komplete = true;
		GM = new GraphManager();
		flip = true;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		RhoLabelR RLR = new RhoLabelR();
		RLR.launch();
	}
	
	public void launch(){
		JFrame window = new JFrame("RhoLabelR " + version);
		
		int i;
		window.setSize(900, 900);
		window.setLayout(new GridLayout(3, 3));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Help Container
        final DefaultListModel helpListModelgraph = new DefaultListModel();
        setHelpModel(helpListModelgraph, 0);
        final JList helpListgraph = new JList(helpListModelgraph);
        JScrollPane helpScrollgraph = new JScrollPane(helpListgraph);
        
        final DefaultListModel helpListModellabel = new DefaultListModel();
        setHelpModel(helpListModellabel, 1);
        final JList helpListlabel = new JList(helpListModellabel);
        JScrollPane helpScrolllist = new JScrollPane(helpListlabel);
        
        final DefaultListModel instructionModel = new DefaultListModel();
        setHelpModel(instructionModel, 2);
        final JList instructionList = new JList(instructionModel);
        JScrollPane instructionScroll = new JScrollPane(instructionList);
        
        //Error Container
        JLabel badGraphText = new JLabel("", JLabel.CENTER);
        JLabel badLabelText = new JLabel("", JLabel.CENTER);
        JLabel badSolve = new JLabel("Something is wrong...", JLabel.CENTER);
        badGraphText.setVisible(false);
        badLabelText.setVisible(false);
        badSolve.setVisible(false);
        
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new GridLayout(3, 1));

        errorPanel.add(badGraphText);
        errorPanel.add(badLabelText);
        errorPanel.add(badSolve);
        
        JPanel errorHolderPanel = new JPanel();
        errorHolderPanel.setLayout(new GridLayout(2, 1));
        errorHolderPanel.add(errorPanel);
        errorHolderPanel.add(instructionScroll);

        //Graph List Container
        final DefaultListModel graphListModel = new DefaultListModel();
        if(GM.numGraphs > 0){
        	 Object[] graphName = GM.nameList.toArray();
             for(i = 0; i < GM.numGraphs; i++)
             	graphListModel.addElement(graphName[i] + ": " + GraphManager.toString((int[][])GM.graphTable.get(graphName[i])));
        }
        else graphListModel.addElement("Graph Pool is Empty.");
        final JList graphList = new JList(graphListModel);
        JScrollPane graphScroll = new JScrollPane(graphList);
        
      //Graph List Container
        final DefaultListModel labelListModel = new DefaultListModel();
        if(GM.numLabels > 0){
        	 Object[] labelName = GM.labelList.toArray();
             for(i = 0; i < GM.numLabels; i++)
            	 labelListModel.addElement(GraphManager.toString((int[][])GM.labelTable.get(labelName[i])));
             
        }
        else labelListModel.addElement("Label Pool is Empty.");
        final JList labelList = new JList(labelListModel);
        JScrollPane labelScroll = new JScrollPane(labelList);
        
        
       
        //Graph Input Container
        final JTextArea graphTextBox = new JTextArea();
        JScrollPane graphTextScroll = new JScrollPane(graphTextBox);
        
        //Label Input Container
        final JTextArea labelTextBox = new JTextArea();
        JScrollPane labelTextScroll = new JScrollPane(labelTextBox);
        
        
        //Settings Container 
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(3, 1));
        JButton directedButton = new JButton();							//<--
        if(directed)	directedButton.setText("Directed");
        else			directedButton.setText("Undirected");
        
        directedButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		directed = !directed;
        		if(directed)	directedButton.setText("Directed");
                else			directedButton.setText("Undirected");
        	}
        });
        JPanel mSettingPanel = new JPanel();							//<--
        mSettingPanel.setLayout(new GridLayout(1, 3));
        JLabel multiplicityLabel = new JLabel("M = " + multiplicity, JLabel.CENTER);
        JButton plusButton = new JButton("+");
        JButton minusButton = new JButton("-");
        
        plusButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		multiplicity++;
        		multiplicityLabel.setText("M = " + multiplicity);
        	}
        });
        minusButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		if(multiplicity > 1){
            		multiplicity--;
            		multiplicityLabel.setText("M = " + multiplicity);
        		}
        	}
        });
        mSettingPanel.add(minusButton);
        mSettingPanel.add(multiplicityLabel);
        mSettingPanel.add(plusButton);
        
        JButton kompleteButton = new JButton();							//<--
        if(komplete)	kompleteButton.setText("Komplete Decomposition");
        else			kompleteButton.setText("Tripartite Decomposition");
        
        kompleteButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		komplete = !komplete;
        		if(komplete)	kompleteButton.setText("Komplete Decomposition");
                else			kompleteButton.setText("Tripartite Decomposition");
        	}
        });
        
        settingsPanel.add(directedButton);
        settingsPanel.add(mSettingPanel);
        settingsPanel.add(kompleteButton);	
        
        //Solve
        JButton solve = new JButton("Solve!");
        solve.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		int selected = graphList.getSelectedIndex();
        		int[][] graphArray = GM.get(selected);
        		if(graphArray != null){
        			try{
        				badSolve.setVisible(false);
        				Graph G;
            			if(komplete)
            				G = new Graph(graphArray, -1, directed, multiplicity);
            			else{
            				G = new Graph(graphArray, -2, false, 3);
            				for(int i = 3; i < G.K()/2; i = i + 3)
            					G.killEdgeOption(i);
            			}
            			int labelSelection = labelList.getSelectedIndex();
            			if(labelSelection > 0){
            				int[][] labelArray = GM.getLabel(labelSelection);
            				if(labelArray != null && labelArray.length != 0)
            					for(int i = 0; i < labelArray.length; i++)
            						G.setHumanLabel(labelArray[i][0], labelArray[i][1]);
            			}
            			if(G.que(G.N()-1).label() < 0){ //Nothing has yet been labeled
            				//Find node with max degree and place it in front
            				int maxIndex = 0;
            				int max = G.que(0).numDownStream() + G.que(0).numUpStream();
            				int test;
            				for(int i = 1; i < G.N(); i++){
            					test = G.que(i).numDownStream() + G.que(i).numUpStream();
            					if(test > max){
            						max = test;
            						maxIndex = i;
            					}
            				}
            				G.swap(0, maxIndex);
            				
            				//Swap a neighbor of Node with the last place in the que
            				if(G.que(0).numDownStream() > 0)
            					G.swap(G.N()-1, G.que(0).downStream(0).heapIndex());
            				else if(G.que(0).numUpStream() > 0)
            					G.swap(G.N()-1, G.que(0).upStream(0).heapIndex());
            				
            				//Force the first label
            				G.setLabel(0, 0);
            			}
            			G.solve();
        			}
        			catch(Exception o){
        				badSolve.setVisible(true);
        			}
        		}
        	}
        });
        
        //Graph Button Container
        JPanel graphButtonPanel = new JPanel();
        graphButtonPanel.setLayout(new GridLayout(3, 1));
        JButton addGraphButton = new JButton("Add Graph");
        JButton removeGraphButton = new JButton("Remove Graph");
        
        addGraphButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		badGraphText.setText(GM.makeGraphArray(graphTextBox.getText()));
        		if(badGraphText.getText().length() == 0){
        			window.dispose();
        			launch();
        		}
        		else{
        			badGraphText.setVisible(true);
        		}
        	}
        });
        removeGraphButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		int selected = graphList.getSelectedIndex();
        		if(GM.removeGraph(graphList.getSelectedIndex())){
        			window.dispose();
        			launch();
        		}
        	}
        });
        graphButtonPanel.add(helpScrollgraph);
        graphButtonPanel.add(addGraphButton);
        graphButtonPanel.add(removeGraphButton);
        
        //Label Button Container
        JPanel labelButtonPanel = new JPanel();
        labelButtonPanel.setLayout(new GridLayout(3, 1));
        JButton addLabelButton = new JButton("Add Label");
        JButton removeLabelButton = new JButton("Remove Label");
        
        addLabelButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		badLabelText.setText(GM.makeLabelArray(labelTextBox.getText()));
        		if(badLabelText.getText().length() == 0){
        			window.dispose();
        			launch();
        		}
        		else
        			badLabelText.setVisible(true);
        	}
        });
        removeLabelButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		int selected = labelList.getSelectedIndex();
        		if(GM.removeLabel(labelList.getSelectedIndex())){
        			window.dispose();
        			launch();
        		}
        	}
        });
        labelButtonPanel.add(helpScrolllist);
        labelButtonPanel.add(addLabelButton);
        labelButtonPanel.add(removeLabelButton);
        //Insert Containers
        window.add(graphScroll);
        window.add(graphButtonPanel);
        window.add(graphTextScroll);
        window.add(labelScroll);
        window.add(labelButtonPanel);
        window.add(labelTextScroll);
        window.add(settingsPanel);
        window.add(errorHolderPanel);
        window.add(solve);
        
        window.setVisible(true);
	}
	
	
	public void setHelpModel(DefaultListModel helpListModel, int num){
		if(num == 0){
			helpListModel.addElement("To input a graph, input a graph name");
			helpListModel.addElement("on the first line on the right. Then, list");
			helpListModel.addElement("the neighbors of each node in turn.");
	        helpListModel.addElement("All neighbors for a single node must be");
	        helpListModel.addElement("contained in { }.");
	        helpListModel.addElement("For example C5 could be inputted as:");
	        helpListModel.addElement("    graphNameA");
	        helpListModel.addElement("    {4, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 0}");
	        helpListModel.addElement("or");
	        helpListModel.addElement("    graphNameB");
			helpListModel.addElement("    {1, 4}, {0, 2}, {1, 3}, {2, 4}, {0, 3}");
	        helpListModel.addElement("In general:");
	        helpListModel.addElement("    graphNameX");
	        helpListModel.addElement("    {Node 0 Nbors}, {Node 1 Nbors}, ...");
	        helpListModel.addElement("In directed graphs, Nbors are");
	        helpListModel.addElement("downstream.");
		}
		else if(num == 1){
			helpListModel.addElement("You can set node labels before solving:");
			helpListModel.addElement("To input a set of labels, input pairs:");
	        helpListModel.addElement("    {NodeA, label}, {NodeB, label}, ...");
	        helpListModel.addElement("For example, the list:");
	        helpListModel.addElement("    {0, 4}, {7, 3}, {3, 0}");
	        helpListModel.addElement("would label Node 0 as 4, Node 7 as 3,");
	        helpListModel.addElement("and Node 3 as 0");
	        helpListModel.addElement("To leave the graph unlabeled, select {}");
		}
		else{
			helpListModel.addElement("Select a graph from the list to solve.");
			helpListModel.addElement("Provided are 4 example graphs.");
			helpListModel.addElement("Make sure to set the settings!");
			helpListModel.addElement("M = number edges between two nodes.");
			helpListModel.addElement("If 'Tripartite Decomposition' is set,");
			helpListModel.addElement("then labeling will automatically be");
			helpListModel.addElement("UNdirected with M=1.");
			helpListModel.addElement("'Tripartite Decomposition' only works");
			helpListModel.addElement("if the number of edges in G is even!");
		}
	}
	
	public static int getInt() throws IOException{
		int num = -1;
		while( true ){
			BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
			String input = in.readLine();
			if(input.equalsIgnoreCase("quit"))
				System.exit(0);
			try{
				num = Integer.parseInt(input);
				return num; 
			}
			catch(Exception e){
				System.out.println("   Must be number or 'quit'.");
			}
		}
	}

}


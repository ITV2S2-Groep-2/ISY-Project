package com.isy.game.othello;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import javax.swing.*;
import java.awt.*;

public class OthelloDebugger implements Runnable{
    private JFrame window;
    private int index = 1;
    private Forest<String, Integer> graph;

    @Override
    public void run() {
        this.window = new JFrame("Debugger");

        // 1. Create the Graph (Forest is best for trees)
        graph = new DelegateForest<>();

        // 2. Add data: addEdge(edge_id, parent, child)
        graph.addEdge(index++, "Root", "Child 1");
        graph.addEdge(index++, "Root", "Child 2");
        graph.addEdge(index++, "Child 1", "Grandchild 1");
        graph.addEdge(index++, "Child 1", "Grandchild 2");

        // 3. Create the Layout (The '50, 50' defines spacing)
        TreeLayout<String, Integer> layout = new TreeLayout<>(graph, 50, 50);

        // 4. Create the Swing Component
        VisualizationViewer<String, Integer> vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(400, 400));

//         Add labels to the nodes
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        window.add(vv);

        window.setSize(800, 800);
        window.show();
    }

    public void removeAll(){
        for (int i = 1; i < this.index; i++) {
            this.graph.removeEdge(i);
        }
    }


}

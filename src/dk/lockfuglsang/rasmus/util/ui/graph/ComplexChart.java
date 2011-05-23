package dk.lockfuglsang.rasmus.util.ui.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import dk.lockfuglsang.rasmus.util.resources.Resource;
import dk.lockfuglsang.rasmus.util.ui.graph.data.GraphData;

/** Enables the swapping between multiple charts. */
public class ComplexChart extends JPanel {
  public enum GraphType {
    BAR, LINE
  };
  
  private Resource res;
  private GraphData<String, Number> graphData;
  private GraphType graphType;
  private ChartCanvas chart;
  
  public ComplexChart(GraphData<String, Number> graphData) {
    this(graphData, new Resource("ComplexChart"));
  }
  public ComplexChart(GraphData<String, Number> graphData, Resource res) {
    super(new BorderLayout());
    this.graphData = graphData;
    this.res = res;
    setGraphType(GraphType.BAR);
    setupPopupMenu();
    this.setOpaque(true);
    this.setBackground(Color.WHITE);
    ToolTipManager.sharedInstance().registerComponent(this);
  }
  
  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    ToolTipManager.sharedInstance().unregisterComponent(this);
  }
  
  public void setGraphType(GraphType type) {
    this.graphType = type;
    switch (graphType) {
      case BAR:
        chart = new BarChart(graphData);
        break;
      case LINE:
        chart = new LineChart(graphData);
        break;
      default:
        throw new IllegalArgumentException("Graph type " + type
            + " is not supported!");
    }
    this.removeAll();
    this.add(chart, BorderLayout.CENTER);
    chart.invalidate();
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        validate();
        repaint();
      }
    });
  }
  
  private void setupPopupMenu() {
    JPopupMenu popMenu = new JPopupMenu();
    JMenu typeMenu = new JMenu(res.getString(
        "ComplexChart.Popup.Title", "Chart Type"));
    popMenu.add(typeMenu);
    
    JMenuItem mi = res.createJMenuItem("ComplexChart.Popup.Bar");
    mi.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setGraphType(GraphType.BAR);
      }
    });
    typeMenu.add(mi);
    mi = res.createJMenuItem("ComplexChart.Popup.Line");
    mi.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setGraphType(GraphType.LINE);
      }
    });
    typeMenu.add(mi);
    
    KeyStroke ks = KeyStroke.getKeyStroke(res.getString(
        "ComplexChart.Popup.KeyStroke", "shift F10"));
    GraphPopupListener listener = new GraphPopupListener(popMenu, ks);
    listener.register(this);
  }
  @Override
  public String getToolTipText(MouseEvent event) {
    return chart.getToolTipText(event);
  }
}

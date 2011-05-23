package dk.lockfuglsang.rasmus.util.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import dk.lockfuglsang.rasmus.util.ui.graph.data.GraphData;

public class BarChart extends ChartCanvas {
  /** Whether or not to recalculate the various values. */
  protected int barSpacing;
  protected int barWidth;
  
  /** The number of pixels per value in the value. */
  protected float pixelHeightRatio;
  
  public BarChart(GraphData<String,Number> graphData) {
    super(graphData);
    barSpacing = 5;
    barWidth = 7;
    pixelHeightRatio = 1.0f;
  }
  
  @Override
  public void doPaint(Graphics g) {
    Dimension pref = getPreferredSize();
    float yScale = (1.0f * d.height) / pref.height;
    float xScale = (1.0f * d.width) / pref.width;
    // Calculated based on ratio...
    int actualBarWidth = (int)Math.floor(xScale * barWidth);
    int actualBarSpacing = Math.round(xScale * barSpacing);
    if (actualBarWidth == 0) {
      actualBarWidth = 1;
    }
    // Draw graph
    int zero = (int)Math.round(maxY*yScale) - g.getFontMetrics().getHeight();
    
    drawGrid(g, zero, yScale);
    int x = maxGridLength + 15;
    List<String> xlabels = graphData.getXValues();
    for (int xindex = 0; xindex < graphData.getNumValues(); xindex++) {
      List<Number> values = graphData.getValueAt(xindex);
      int w = 0;
      for (int yindex = 0; yindex < values.size(); yindex++) {
        float value = values.get(yindex).floatValue();
        String label = graphData.getLabels().get(yindex);
        g.setColor(getColor(label));
        int h = (int)Math.floor(value*yScale*pixelHeightRatio);
        if (h == 0) {
          h = 1;
        } else if (h >= zero) {
          h = zero - 1;
        } else if (h <= (zero - d.height)) {
          h = zero - d.height + 1;
        }
        Rectangle r = null;
        if (h > 0) {
          r = new Rectangle(x, zero-h, actualBarWidth, h);
        } else {
          r = new Rectangle(x, zero, actualBarWidth, -h);
        }
        g.fillRect(r.x, r.y, r.width, r.height);
        addToolTip(r, label + " = " + value);
        x += actualBarWidth;
        w += actualBarWidth;
      }
      // Draw x-value
      g.setColor(Color.BLACK);
      String xlabel = xlabels.get(xindex);
      g.drawString(xlabel, x - (w+g.getFontMetrics().stringWidth(xlabel))/2, d.height - LEGEND_BORDER/2);
      x += actualBarSpacing/2;
      g.drawLine(x, 0, x, d.height-1);
      x += actualBarSpacing/2;
    }
    drawLegends(g, actualBarWidth, actualBarWidth);
  }

  @Override
  public Dimension getPreferredSize() {
    Graphics g = this.getGraphics();
    calcMinMaxValues(g);
    int w = 0, h = 0;
    w = (graphData.getNumValues() * (barWidth*graphData.getNumSeries() + barSpacing)) - barSpacing;
    w += maxGridLength + 15;
    h = (int)Math.round((maxY - minY) * pixelHeightRatio);
    
    if (g != null) {
      h += g.getFontMetrics().getHeight() + LEGEND_BORDER;
    } else {
      h += 10 + LEGEND_BORDER;
    }
    setDirty();
    return new Dimension(w, h);
  }
  
  @Override
  public Dimension getMinimumSize() {
    int w = 0, h = 0;
    w = graphData.getNumValues()*graphData.getNumSeries();
    h = 50;
    return new Dimension(w, h);
  }
}

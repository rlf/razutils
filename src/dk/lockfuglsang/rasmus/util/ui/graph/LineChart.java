package dk.lockfuglsang.rasmus.util.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import dk.lockfuglsang.rasmus.util.ui.graph.data.GraphData;

public class LineChart extends ChartCanvas {
  
  protected int lineWidth;
  float pixelHeightRatio;
  
  public LineChart(GraphData<String, Number> graphData) {
    super(graphData);
    this.lineWidth = 5;
    this.pixelHeightRatio = 1.0f;
  }

  @Override
  public void doPaint(Graphics g) {
    Dimension pref = getPreferredSize();
    float yScale = (1.0f * d.height) / pref.height;
    
    // Draw graph
    int zero = (int)Math.round(maxY*yScale) - g.getFontMetrics().getHeight();
    
    drawGrid(g, zero, yScale);
    int xstart = maxGridLength + 15;
    int xWidth = (d.width - xstart) / graphData.getNumValues();
    List<String> xlabels = graphData.getXValues();
    for (int yindex = 0; yindex < graphData.getNumSeries(); yindex++) {
      int x = xstart + xWidth/2;
      String label = graphData.getLabels().get(yindex);
      g.setColor(getColor(label));
      float value = graphData.getValueAt(0, yindex).floatValue();
      int ox = x;
      int oy = getYPos(value, zero, yScale);
      x += xWidth;
      Rectangle r = new Rectangle(ox - lineWidth, oy - lineWidth, lineWidth*2, lineWidth*2);
      addToolTip(r, label + " = " + value);
      for (int xindex = 1; xindex < graphData.getNumValues(); xindex++) {
        value = graphData.getValueAt(xindex, yindex).floatValue();
        int y = getYPos(value, zero, yScale);
        drawLine(g, ox, oy, x, y, lineWidth);
        ox = x;
        oy = y;
        x += xWidth;
        // The last value...
        r = new Rectangle(ox - lineWidth, oy - lineWidth, lineWidth*2, lineWidth*2);
        addToolTip(r, label + " = " + value);
      }
    }
    g.setColor(Color.BLACK);
    int x = xstart + xWidth/2;
    for (int xindex = 0; xindex < graphData.getNumValues(); xindex++) {
      String xlabel = xlabels.get(xindex);
      g.drawString(xlabel, x - (g.getFontMetrics().stringWidth(xlabel) / 2), d.height - LEGEND_BORDER / 2);
      x += xWidth;
    }
    drawLegends(g, g.getFontMetrics().getHeight(), lineWidth);
  }
  private void drawLine(Graphics g, int x1, int y1, int x2, int y2, int width) {
    int yoffset = - width/2;
    for (int cnt = 0; cnt < width; cnt++) {
      g.drawLine(x1, y1 + yoffset + cnt, x2, y2 + yoffset + cnt);
    }
  }
  /** Calculates the y-pos of the value.
   * 
   * @param value The actual value to be displayed in the graph.
   * @param zero The baseline indicating the pixelposition of zero.
   * @param yScale The scaling factor for the values.
   * @return The pixel position of the y-coordinate based on the value.
   */ 
  private int getYPos(float value, int zero, float yScale) {
    int h = (int)Math.floor(value*yScale*pixelHeightRatio);
    if (h == 0) {
      h = 1;
    } else if (h >= zero) {
      h = zero - 1;
    } else if (h <= (zero - d.height)) {
      h = zero - d.height + 1;
    }
    h = zero - h;
    return h;
  }
  @Override
  public Dimension getPreferredSize() {
    calcMinMaxValues(this.getGraphics());
    int w = 0, h = 0;
    w = graphData.getNumValues() * lineWidth;
    w += maxGridLength + 15;
    h = (int)Math.round((maxY - minY) * pixelHeightRatio);
    h += this.getGraphics().getFontMetrics().getHeight() + LEGEND_BORDER;
    setDirty();
    return new Dimension(w, h);
  }
  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }
}

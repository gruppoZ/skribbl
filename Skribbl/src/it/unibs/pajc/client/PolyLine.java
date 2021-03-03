package it.unibs.pajc.client;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.Serializable;
import java.util.*;

/*
 * The PolyLine class model a line made up of many points
 */
public class PolyLine implements Serializable {
   private static final long serialVersionUID = 1L;
   
   private List<Integer> xList;  // List of x-coord
   private List<Integer> yList;  // List of y-coord
   private Color colore;
   private float strokeSize;
   
   public PolyLine(Color colore) {
      xList = new ArrayList<Integer>();
      yList = new ArrayList<Integer>();
      this.colore = colore;
   }
   
   protected float getStrokeSize() {
	   return strokeSize;
   }
   
   protected void setStrokeSize(float size) {
	   this.strokeSize = size;
   }
   
   protected Color getColore() {
	   return this.colore;
   }

   // Add a point to this PolyLine
   protected void addPoint(int x, int y) {
      xList.add(x);
      yList.add(y);
   }
 
   // This PolyLine paints itself given the Graphics context
   protected void draw(Graphics2D g) { // draw itself
      for (int i = 0; i < xList.size() - 1; ++i) {
         g.drawLine((int)xList.get(i), (int)yList.get(i), (int)xList.get(i + 1),
               (int)yList.get(i + 1));
      }
   }
}
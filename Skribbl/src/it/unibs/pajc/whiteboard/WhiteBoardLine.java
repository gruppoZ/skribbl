package it.unibs.pajc.whiteboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class WhiteBoardLine implements Serializable {
	//che sarebbe il nostro polyline
	
//	private PolyLine line;
//	private Color color;
//	
//	public WhiteBoardLine(int x, int y, Color color) {
//		this.line.addPoint(x, y);
//		this.color = color;
//	}
//
//	public PolyLine getLine() {
//		return line;
//	}
	
	private List<Integer> xList;  // List of x-coord
	private List<Integer> yList;  // List of y-coord
	private Color color;
	private float strokeSize;
	
	public WhiteBoardLine(Color color, float strokeSize) {
		xList = new ArrayList<Integer>();
		yList = new ArrayList<Integer>();
		this.color = color;
		this.strokeSize = strokeSize;
	}
 
	// Add a point to this PolyLine
	public void addPoint(int x, int y) {
		xList.add(x);
		yList.add(y);
	}
 
	// This PolyLine paints itself given the Graphics context
	public void draw(Graphics2D g) { // draw itself
		for (int i = 0; i < xList.size() - 1; ++i) {
			g.drawLine((int)xList.get(i), (int)yList.get(i), (int)xList.get(i + 1),
					(int)yList.get(i + 1));
		}
	}
	
	public Color getColor() {
		   return color;
	}
	
	public float getStrokeSize() {
		return strokeSize;
	}
	
	
	
}

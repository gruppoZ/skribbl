//package it.unibs.pajc.whiteboard;
//
//import java.awt.Canvas;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.util.LinkedList;
//
//
//@SuppressWarnings("serial")
//public class WhiteBoardCanvas extends Canvas	//canvas for client
//{
//	private LinkedList<WhiteBoardLine> shapes;	//shapes in whiteboard
//	private WhiteBoardLine currentShape;	//shape currently being drawn
//	
//	public WhiteBoardCanvas()
//	{
//		super();
//		shapes = new LinkedList<WhiteBoardLine>();
//		currentShape = null;
//	}
//	public void paint(Graphics g)	
//	{
//		this.setBackground(Color.white);
//		for(WhiteBoardLine s: shapes)
//		{
//			s.draw(g);
//		}
//		if(currentShape!=null)
//		{
//			currentShape.draw(g);	//paints temp shape for drag draw effect
//			currentShape=null;
//		}
//	}
//	public void add(WhiteBoardLine s)	//shape added
//	{
//		shapes.add(s);
//		repaint();
//	}
//	public void addTemp(WhiteBoardLine s) //temp shape when mouse is being dragged
//	{
//		currentShape = s;
//		repaint();
//	}
//	public void clear()	//clears the board
//	{
//		shapes.clear();
//		repaint();
//	}
//}
package com.pavelurusov.squaregrid;

import java.io.Serializable;

import javafx.scene.paint.Color;

/**
 * @author Pavel Urusov, me@pavelurusov.com
 * This class implements the internal data structure used by the SquareGrid class.
 *
 */

public class GridData implements Serializable {
	
	private Color[][] cells; // this 2-dimensional array holds the information
							 // about the colour of the cells
	
	private Color defaultColor; // this colour is used if the cell colour is not explicitly set
	
	private Color gridColor; // the colour used to draw the grid between the squares
	
	private boolean alwaysDrawGrid; // if this field is set to false, there will be no
									// grid lines between the empty cells
	private int rows;
	private int columns;
	
	private static final long serialVersionUID = 2021;
	
	public GridData(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		// number of rows/columns can't be negative
		if (rows <= 0 || columns <= 0)
			throw new IllegalArgumentException("The number of rows and columns can't be less than zero.");
		
		// initialise the main array
		cells = new Color[rows][columns];
		
		// set fields to their default values
		defaultColor = Color.BLACK;
		gridColor = Color.GRAY;
		alwaysDrawGrid = false;
	}
	
	public void setDefaultColor(Color color) {
		defaultColor = color;
	}
	
	public Color getDefaultColor() {
		return defaultColor;
	}
	
	public void setGridColor(Color color) {
		gridColor = color;
	}
	
	public Color getGridColor() {
		return gridColor;
	}
	
	public void setAlwaysDrawGrid(boolean b) {
		alwaysDrawGrid = b;
	}
	
	public boolean getAlwaysDrawGrid() {
		return alwaysDrawGrid;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	// if the colour is set successfully, this method returns true.
	// this is used in GridData.setCellColor() to trigger a redraw
	public boolean setCellColor(int row, int column, Color color) {
		if (row >= 0 && row < rows && column >= 0 && column < columns) {
			cells[row][column] = color;
			return true;
		} else return false;
	}
	
	public Color getCellColor(int row, int column) {
		if (row >= 0 && row < rows && column >= 0 && column < columns) {
			return cells[row][column];
		}
		else return null;
	}
	
	public Color[][] getCells() {
		return cells;
	}
	
}

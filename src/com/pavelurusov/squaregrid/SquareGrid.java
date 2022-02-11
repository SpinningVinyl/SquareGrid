package com.pavelurusov.squaregrid;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;


/**
 * @author Pavel Urusov, me@pavelurusov.com
 * This class implements a grid of squares that can have different colours.
 * It can export and import the state of the grid as a GridData object (see the GridData class).
 * It can also save a bitmap containing the image currently on the canvas.
 *
 */

public class SquareGrid extends Canvas {
	
	// === PRIVATE FIELDS ===
	
	private final int squareSize; // size of individual squares in the grid
	
	private final static int minSize = 5; // minimum size of individual squares in the grid
	private final static int defaultSize = 10; // default size of individual squares in the grid
	private final static int defaultRows = 50; // default number of rows...
	private final static int defaultColumns = 50; // ...and columns
	
	// this object contains the information about the state of the grid
	// see also: the GridData class
	private GridData data; 
	
	private boolean automaticRedraw = true; // If this is set to false, 
											// the cells are not automatically redrawn
											// when their colour is changed.
											// In this case, redraw() is used to redraw
											// the whole grid at once.
	
	private GraphicsContext gc; // the graphics context used for drawing on the canvas

	/**
	* === CONSTRUCTORS ===
	*/
	public SquareGrid(int rows, int columns, int squareSize, 
			Color defaultColor, Color gridColor, boolean alwaysDrawGrid, boolean automaticRedraw) {
		this(rows, columns, squareSize);
		setDefaultColor(defaultColor);
		setGridColor(gridColor);
		setAlwaysDrawGrid(alwaysDrawGrid);
		setAutomaticRedraw(automaticRedraw);
	}
	
	public SquareGrid(int rows, int columns, int squareSize) {
		data = new GridData(rows, columns);
		this.squareSize = Math.max(squareSize, minSize); // the size of the square can't be less than minSize
		setWidth(this.squareSize*columns);
		setHeight(this.squareSize*rows);
		gc = getGraphicsContext2D();	
	}
	
	public SquareGrid(int rows, int columns) {
		this(rows, columns, defaultSize);
	}
	
	public SquareGrid() {
		this(defaultRows, defaultColumns, defaultSize);
	}
	
	// === SETTERS AND GETTERS ===
	
	// sets the default colour to the specified colour
	public void setDefaultColor(Color color) {
		// if the specified colour is null, set the default colour to black
		if (color == null) {
			color = Color.BLACK;
		}
		// if the new default colour is not the same as the old default colour,
		// make the change and refresh the canvas 
		if (! color.equals(data.getDefaultColor())) {
			data.setDefaultColor(color);
			redraw();
		}
	}
	
	// sets the default colour to the colour specified by the given RGB values
	public void setDefaultColor(double red, double green, double blue) {
		setDefaultColor(colorFromRGB(red, green, blue));
	}
	
	public Color getDefaultColor() {
		return data.getDefaultColor();
	}
	
	// sets the colour of the grid to the specified colour	
	public void setGridColor(Color color) {
		if (color == null || ! color.equals(data.getGridColor())) {
			data.setGridColor(color);
			redraw();
		}
	}
	
	// sets the colour of the grid to the colour specified by the given RGB values
	public void setGridColor(double red, double green, double blue) {
		setGridColor(colorFromRGB(red, green, blue));
	}
	
	public void setAlwaysDrawGrid(boolean b) {
		if (data.getAlwaysDrawGrid() != b) {
			data.setAlwaysDrawGrid(b);
			redraw();
		}
	}
	
	public boolean getAlwaysDrawGrid() {
		return data.getAlwaysDrawGrid();
	}
	
	public void setAutomaticRedraw(boolean b) {
		if (automaticRedraw != b) {
			automaticRedraw = b;
		}
		if (automaticRedraw) {
			redraw();
		}
	}
	
	public int getRows() {
		return data.getRows();
	}
	
	public int getColumns() {
		return data.getColumns();
	}
	
	
	public Color getCellColor(int row, int column) {
		return data.getCellColor(row, column);
	}
	
	// === METHODS FOR DRAWING ON THE CANVAS ===
	
	// fill the cell with the specified coordinates with the specified colour	
	public void setCellColor(int row, int column, Color color) {
		if (data.setCellColor(row, column, color)) {
			drawCell(row, column);
		}
	}
	
	// fills the cell with the specified coordinates with the colour specified by the given RGB values
	public void setCellColor(int row, int column, double red, double green, double blue) {
		setCellColor(row, column, colorFromRGB(red, green, blue));
	}
	
	
	// set all cells to the specified colour
	public void fill(Color color) {
		for (int row = 0; row < data.getRows(); row ++) {
			for (int column = 0; column < data.getColumns(); column++) {
				data.setCellColor(row, column, color);
			}
		}
		redraw();
	}
	
	// set all cells to the colour specified by the given RGB values
	public void fill(double red, double green, double blue) {
		fill(colorFromRGB(red, green, blue));
	}
	
	// clears the grid by filling all cells with the default colour	
	public void clearGrid() {
		fill(null);
	}
	
	final public void redraw() {
		drawCells();
	}
	
	// === OTHER USEFUL PUBLIC METHODS ===
	
	// this method takes the x coordinate of a pixel in the grid 
	// and returns the column number of the square which contains the pixel
	public int xToColumn(double x) {
		if (x < 0) {
			return -1;
		}
		double columnWidth = getWidth() / data.getColumns();
		int column = (int) (x / columnWidth);
		return Math.min(column, data.getColumns());
	}
	
	// this method takes the y coordinate of a pixel in the grid 
	// and returns the row number of the square which contains the pixel
	public int yToRow(double y) {
		if (y < 0) {
			return -1;
		}
		double rowHeight = getHeight() / data.getRows();
		int row = (int) (y / rowHeight);
		return Math.min(row, data.getRows());
	}
	
	// returns the GridData object
	public GridData exportGridData() {
		// create a new GridData object and copy existing information to it
		GridData copy = new GridData(data.getRows(), data.getColumns());
		copy.setDefaultColor(data.getDefaultColor());
		copy.setGridColor(data.getGridColor());
		copy.setAlwaysDrawGrid(data.getAlwaysDrawGrid());
		for(int row = 0; row < data.getRows(); row++) {
			for(int column = 0; column < data.getColumns(); column++) {
				copy.setCellColor(row, column, data.getCellColor(row, column));
			}
		}
		return copy;
	}
	
	// this method will set the grid according to the data in the GridData object
	// if the import is not successful, the method returns false
	public boolean importGridData(GridData gd) {
		if (gd == null) { // don't do anything if the object is null
			return false;
		}
		if (gd.getRows() == 0 || gd.getColumns() == 0) { // don't do anything if the object's grid is empty
			return false;
		}
		// if the current number of rows and columns doesn't match the data being imported,
		// reinitialise the internal data object
		if(gd.getRows() != data.getRows() || gd.getColumns() != data.getColumns()) { 
			data = new GridData(gd.getRows(), gd.getColumns());
			setWidth(data.getColumns()*squareSize);
			setHeight(data.getRows()*squareSize);
		}
		// copy the data
		data.setDefaultColor(gd.getDefaultColor());
		data.setGridColor(gd.getGridColor());
		data.setAlwaysDrawGrid(gd.getAlwaysDrawGrid());
		for(int row = 0; row < data.getRows(); row++) {
			for(int column = 0; column < data.getColumns(); column++) {
				data.setCellColor(row, column, gd.getCellColor(row, column));
			}
		}
		redraw();
		return true;
	}
	
	// saves the image as a PNG file
	public void saveBitmap(File file) {
		if (file != null) {
			WritableImage image = new WritableImage((int) Math.round(getWidth()), 
					(int) Math.round(getHeight()));
			this.snapshot(null, image);
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// === PRIVATE METHODS ===
	
	// draws one individual cell	
	private void drawCell(int row, int column) {
		if (automaticRedraw) { // this method executes only if automaticRedraw is set to true
			// otherwise call redraw() to refresh the whole grid at once
			if (Platform.isFxApplicationThread()) {
				drawSquare(row, column);
			} else {
				Platform.runLater(() -> drawSquare(row, column));
			}
			try { // this is not strictly necessary but it helps to avoid overwhelming the application thread
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// redraws all cells in the grid	
	private void drawCells() {
		if (!data.getAlwaysDrawGrid()) {
			gc.setFill(data.getDefaultColor());
			gc.fillRect(0, 0, getWidth(), getHeight());
		}
		if (Platform.isFxApplicationThread()) {
			for(int row = 0; row < data.getRows(); row++) {
				for(int column = 0; column < data.getColumns(); column++) {
					if (data.getAlwaysDrawGrid()) {
						drawSquare(row, column);
					} else if (data.getCellColor(row, column) != null) {
						drawSquare(row, column);
					}
				}
			}
		} else {
			Platform.runLater(() -> {
				for(int row = 0; row < data.getRows(); row++) {
					for (int column = 0; column < data.getColumns(); column++) {
						if (data.getAlwaysDrawGrid()) {
							drawSquare(row, column);
						} else if (data.getCellColor(row, column) != null) {
							drawSquare(row, column);
						}
					}
				}
			});
		}
		try { // this is not strictly necessary but it helps to avoid overwhelming the application thread
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// draw a square
	private void drawSquare(int row, int column) {
		double rowHeight = getHeight() / data.getRows();
		double columnWidth = getWidth() / data.getColumns();
		int y = (int) Math.round(rowHeight*row); // set the y coordinate
		int x = (int) Math.round(columnWidth*column); // set the x coordinate
		int height = Math.max(1, (int) Math.round(rowHeight*(row+1)) - y); // set the height of the square
		int width = Math.max(1, (int) Math.round(columnWidth*(column+1)) - x); // set the width of the square
		Color color = data.getCellColor(row, column);
		if (color == null) {
			gc.setFill(data.getDefaultColor());
		} else gc.setFill(color);
		if (data.getGridColor() == null || (color == null && !data.getAlwaysDrawGrid())) {
			gc.fillRect(x, y, width, height);
		} else {
			gc.fillRect(x + 1, y + 1, width - 2, height - 2);
			gc.setStroke(data.getGridColor());
			gc.strokeRect(x + 0.5, y + 0.5, width - 1, height - 1);
		}
	}
	
	// returns the colour specified by the given RGB values
	private Color colorFromRGB(double red, double green, double blue) {
		// boundary checks...
		if (red < 0) red = 0;
		if (red > 1 ) red = 1;
		if (green < 0) green = 0;
		if (green > 1) green = 1;
		if (blue < 0) blue = 0;
		if (blue > 1) blue = 1;
		return Color.color(red, green, blue);
	}
}

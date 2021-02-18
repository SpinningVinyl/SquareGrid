package com.pavelurusov.squaregrid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class Demo extends Application {

    private SquareGrid demoBoard;

    @Override
    public void start(Stage stage) throws Exception {
        demoBoard = new SquareGrid(15, 15, 20);
        demoBoard.setAlwaysDrawGrid(true);

        demoBoard.setOnMouseClicked(e -> MouseClicked(e));

        Label hint = new Label("Left click: colour a cell\nRight click: reset the grid");

        HBox footer = new HBox(20, hint);
        footer.setStyle("-fx-padding: 6px");

        BorderPane root = new BorderPane();
        root.setCenter(demoBoard);
        root.setBottom(footer);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("SquareGrid Demo App");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void MouseClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            demoBoard.clearGrid();
        } else {
            int row = demoBoard.yToRow(e.getY());
            int column = demoBoard.xToColumn(e.getX());
            demoBoard.setCellColor(row, column, Math.random(), Math.random(), Math.random());
        }
    }

}

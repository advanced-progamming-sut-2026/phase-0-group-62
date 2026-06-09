package view;

import model.Board;

public class GameView extends View {
    public void showBoard(Board board) {
        showMessage("Board: " + board.getRows() + "x" + board.getColumns());
    }
}


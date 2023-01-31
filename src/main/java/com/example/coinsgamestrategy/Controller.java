package com.example.coinsgamestrategy;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class Controller {

    private final Alert error = new Alert(Alert.AlertType.ERROR);
    @FXML
    private HBox box;
    @FXML
    private TextField coins;
    @FXML
    private TextField list;
    @FXML
    private TextField result;

    @FXML
    private GridPane table;

    private OptimalGameStrategy play;

    @FXML
    private void solution() {
        table.getRowConstraints().clear();              // قيود الصف لانشاء الطول يجب مسحها
        table.getChildren().clear();

        try {

            play = new OptimalGameStrategy(list.getText());

            table.getChildren().addAll(play.getPaneChildren());

            result.setText(play.getResult());

            coins.setText(play.getCoins());

            play.startAnimation(box);

        } catch (NumberFormatException e) {
            clear();
            displayFormatError();
        } catch (RuntimeException e) {
            clear();
            displayOddError();
        }

    }

    private void displayOddError() {
        error.setContentText("You must have even number of coins to play game !!");
        error.setHeaderText("You can't play the game");
        error.setTitle("you have odd number of coins");
        error.show();
    }

    private void displayFormatError() {
        error.setContentText("You must have just Integers numbers and commas !!");
        error.setHeaderText("You can't play the game");
        error.setTitle("you have character that not integer or comma");
        error.show();
    }

    @FXML
    private void restart() {
        if (play != null)
            play.startAnimation(box);
        else {
            error.setContentText("You must enter coins list splittings by commas, \nthen click calculate to play animation !!");
            error.setHeaderText("You can't show animation");
            error.setTitle("Play The game first");
            error.show();
            box.getChildren().clear();
            coins.clear();
            table.getChildren().clear();
            result.clear();
        }

    }

    @FXML
    private void clearAll() {
        box.getChildren().clear();
        result.clear();
        coins.clear();
        table.getChildren().clear();
        list.clear();
    }

    private void clear() {
        box.getChildren().clear();
        result.clear();
        coins.clear();
        table.getChildren().clear();
    }

}

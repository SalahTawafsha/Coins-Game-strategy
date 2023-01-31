package com.example.coinsgamestrategy;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class OptimalGameStrategy {
    private final int[][] dpTable;
    private final byte[][] flags;
    private final GridPane pane;
    private final int[] arr;

    public OptimalGameStrategy(String integers) throws RuntimeException {

        String[] strings = integers.split(",");
        arr = new int[strings.length];
        for (int i = arr.length - 1; i >= 0; i--)
            arr[i] = Integer.parseInt(strings[i].trim());


        // we must have even numbers to play game !
        if (arr.length % 2 == 1)
            throw new RuntimeException();

        dpTable = new int[arr.length][arr.length];
        flags = new byte[arr.length][arr.length];
        pane = new GridPane();
        fillTables();
    }


    private void fillTables() {                                 // it will fill dp Table, flags and GridPane

        /*
         * base case is when I have just one coins so get it, or we have just two coins then I will take max
         * other it will be:
         *        max between:
         *              1st: min(left + solution for left + 2 to right, left + solution for left + 1 to right - 1)
         *              2nd: min(right + solution for left to right - 2, right + solution for left + 1 to right - 1)
         *
         *        1st is that when I take left so the user will take next left so get solution for left + 2 to right,
         *                               or I take left, and he took right then get solution for left + 1 to right - 1
         *                                              and get min because user will not let me take max
         *
         *        2nd is that when I take right so the user will take previous right so get solution for left to right - 2,
         *                               or I take right, and he took left then get solution for left + 1 to right - 1
         *                                              and get min because user will not let me take max
         *
         *        and the max of them because I can choose if I take left or right
         *
         *
         * this methode will fill the two tables that I used it to get solution
         * Dynamic programming table -> have optimal solution from arr[i] to arr[j]
         * flags -> may be 5 values:
         *          0 -> if me take left and user take left
         *          1 -> if me take left user right
         *          2 -> if me take right user right
         *          3 -> if me take right user left
         *         -1 -> then we have two vales and can make it by max
         *
         * and this method will fill Grid Pane that have Dynamic programming table to display it
         *
         */
        for (int left = dpTable.length - 1; left >= 0; left--) {
            for (int right = left; right < dpTable.length; right++) {
                if (left < right)                               // if left < right then we above diagonal so WORK !
                    if (right - left == 1)                      // when have two item we can calculate solution by MAX
                        if (arr[left] > arr[right]) {
                            dpTable[left][right] = arr[left];
                            flags[left][right] = -1;
                        } else {
                            dpTable[left][right] = arr[right];
                            flags[left][right] = -1;
                        }
                    else {

                        int maxL;
                        boolean userLeft;   // if true then user take right

                        if (dpTable[left + 2][right] > dpTable[left + 1][right - 1]) {
                            userLeft = true;
                            maxL = dpTable[left + 1][right - 1] + arr[left];
                        } else if (dpTable[left + 2][right] == dpTable[left + 1][right - 1]) { // when it equals we can use MAX
                            userLeft = arr[left + 1] < arr[right];
                            maxL = dpTable[left + 1][right - 1] + arr[left];

                        } else {
                            userLeft = false;
                            maxL = dpTable[left + 2][right] + arr[left];
                        }

                        int maxR;
                        boolean userRight; // if true then user take right

                        if (dpTable[left][right - 2] < dpTable[left + 1][right - 1]) {
                            userRight = true;
                            maxR = dpTable[left][right - 2] + arr[right];
                        } else if (dpTable[left][right - 2] == dpTable[left + 1][right - 1]) { // when it equals we can use MAX
                            userRight = arr[left] < arr[right - 1];
                            maxR = dpTable[left][right - 2] + arr[right];
                        } else {
                            userRight = false;
                            maxR = dpTable[left + 1][right - 1] + arr[right];
                        }


                        if (maxL > maxR) {
                            dpTable[left][right] = maxL;
                            if (userLeft)
                                flags[left][right] = 1; // me left user right
                            else
                                flags[left][right] = 0; // me left user left

                        } else {
                            dpTable[left][right] = maxR;
                            if (userRight)
                                flags[left][right] = 2; // me right user right
                            else
                                flags[left][right] = 3; // me right user left
                        }

                    }
                else
                    dpTable[left][right] = arr[left];


                Label t = new Label(dpTable[left][right] + "");
                t.setMinSize(50, 50);
                t.setAlignment(Pos.CENTER);

                if (right == arr.length - 1)            // when on end I must add border to right
                    t.setStyle("-fx-font-size: 12;" +
                            "-fx-border-color: black;" +
                            "-fx-border-width: 0 1 1 1;"); // top right bottom left
                else
                    t.setStyle("-fx-font-size: 12;" +
                            "-fx-border-color: black;" +
                            "-fx-border-width: 0 0 1 1;");
                pane.add(t, right, left);
            }

        }
    }

    public void startAnimation(HBox box) {
        box.getChildren().clear();
        for (int k : arr)
            box.getChildren().add(new Label(k + ""));

        TranslateTransition[] transitions = new TranslateTransition[arr.length];

        /*
         * I have two diminution array called flags
         * this array has flags that represent as bytes since it have 5 values
         * 0 -> if me take left and user take left
         * 1 -> if me take left user right
         * 2 -> if me take right user right
         * 3 -> if me take right user left
         * -1 -> then we have two vales and can make it by max
         * */
        for (int i = 0, j = arr.length - 1, count = 0; true; ) {
            if (flags[i][j] == 0) {       // me left user left
                take(transitions, (Label) box.getChildren().get(i), ++count);
                doNotTake(transitions, (Label) box.getChildren().get(i + 1), ++count);
                i += 2;

            } else if (flags[i][j] == 1) {       // me left user right
                take(transitions, (Label) box.getChildren().get(i), ++count);
                doNotTake(transitions, (Label) box.getChildren().get(j), ++count);
                i++;
                j--;

            } else if (flags[i][j] == 2) {       // me right user right
                take(transitions, (Label) box.getChildren().get(j), ++count);
                doNotTake(transitions, (Label) box.getChildren().get(j - 1), ++count);
                j -= 2;

            } else if (flags[i][j] == 3) {       // me right user left
                take(transitions, (Label) box.getChildren().get(j), ++count);
                doNotTake(transitions, (Label) box.getChildren().get(i), ++count);
                i++;
                j--;

            } else {                          // when go to -1 then we have two vales
                if (arr[i] > arr[j]) {
                    take(transitions, (Label) box.getChildren().get(i), ++count);
                    doNotTake(transitions, (Label) box.getChildren().get(j), ++count);
                } else {
                    take(transitions, (Label) box.getChildren().get(j), ++count);
                    doNotTake(transitions, (Label) box.getChildren().get(i), ++count);
                }
                break;
            }

        }

        // SequentialTransition to run all transitions sequential
        SequentialTransition sequentialTransitions = new SequentialTransition(transitions);
        sequentialTransitions.play();
    }

    public String getCoins() {
        StringBuilder s = new StringBuilder();

        /*
         * I have two diminution array called flags
         * this array has flags that represent as bytes since it have 5 values
         * 0 -> if me take left and user take left
         * 1 -> if me take left user right
         * 2 -> if me take right user right
         * 3 -> if me take right user left
         * -1 -> then we have two vales and can make it by max
         * */

        for (int i = 0, j = arr.length - 1; true; ) {
            if (flags[i][j] == 0) {       // me left user left
                s.append(arr[i]).append(", ");
                i += 2;

            } else if (flags[i][j] == 1) {       // me left user right
                s.append(arr[i]).append(", ");
                i++;
                j--;
            } else if (flags[i][j] == 2) {       // me right user right
                s.append(arr[j]).append(", ");
                j -= 2;
            } else if (flags[i][j] == 3) {       // me right user left
                s.append(arr[j]).append(", ");
                i++;
                j--;
            } else {                          // when go to -1 then we have two vales
                if (arr[i] > arr[j])
                    s.append(arr[i]).append(", ");
                else
                    s.append(arr[j]).append(", ");

                break;
            }

        }

        // since I have ", " in end
        return s.substring(0, s.length() - 2);
    }

    private void take(TranslateTransition[] transitions, Label c, int count) {
        /*
         * take label by:
         * make color green
         * custom label
         * make animation and set it to go up by 50 px (-50 since the fx UI start from up)
         * add animation to array of TranslateTransition that for run it sequential
         * */

        if (!c.getText().contains("/"))
            c.setText(getOrdinal(count) + c.getText());
        c.setAlignment(Pos.CENTER);
        c.setMinSize(c.getText().length() * 8, c.getText().length() * 8);
        c.setFont(new Font("Arial Black", 10));
        c.setStyle(
                "-fx-text-fill: black;-fx-background-color: #90EE90;-fx-background-radius: 100;-fx-border-color: black;-fx-border-radius: 100;");
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1));
        transition.setNode(c);
        transition.setToY(-50);

        transitions[count - 1] = transition;
    }

    private String getOrdinal(int count) {
        // get ordinal number >> 1st 2nd 3rd 4th 5th ...
        if (count % 10 == 1)
            return count + "st / ";
        else if (count % 10 == 2)
            return count + "nd / ";
        else if (count % 10 == 3)
            return count + "rd / ";
        else
            return count + "th / ";

    }

    private void doNotTake(TranslateTransition[] transitions, Label c, int count) {
        /*
         * take label by:
         * make color green
         * custom label
         * make animation and set it to go down by 50 px
         * add animation to array of TranslateTransition that for run it sequential
         * */
        if (!c.getText().contains("/"))
            c.setText(getOrdinal(count) + c.getText());
        c.setAlignment(Pos.CENTER);
        c.setMinSize(c.getText().length() * 8, c.getText().length() * 8);
        c.setFont(new Font("Arial Black", 10));
        c.setStyle(
                "-fx-text-fill: black;-fx-background-color: #dc143c ;-fx-background-radius: 100;-fx-border-color: black;-fx-border-radius: 100;");

        TranslateTransition transition = new TranslateTransition(Duration.seconds(1));
        transition.setNode(c);
        transition.setToY(+50);

        transitions[count - 1] = transition;
    }

    public String getResult() {
        return dpTable[0][dpTable.length - 1] + ""; // the result will be on top right
    }

    public ObservableList<Node> getPaneChildren() {
        // return it in observableArrayList to can't change from out of class
        return FXCollections.observableArrayList(pane.getChildren());
    }
}

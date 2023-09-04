/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import com.google.inject.Inject;
import commons.Question;
import commons.SearchResult;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.net.URL;
import java.util.*;


public class AdminCtrl implements Ctrl, Initializable {

    @FXML
    private TextField name;

    @FXML
    private TextField consumption;

    @FXML
    private Label added;

    @FXML
    Label generated;

    @FXML
    TextField idSearchBar;
    @FXML
    TextArea questionDisplay;

    @FXML
    TextField activitySearchBar;
    @FXML
    TableColumn<LinkedHashMap, Number> colId;
    @FXML
    TableColumn<LinkedHashMap, String> colActivity;
    @FXML
    TableColumn<LinkedHashMap, String> colType;
    @FXML
    TableView<SearchResult> questionTable;
    @FXML
    ObservableList<SearchResult> data;

    public MainCtrl mainCtrl;
    private Timer addedTimer;
    private Timer generatedTimer;

    /**
     * Constructor for the ctrl
     *
     * @param mainCtrl the main ctrl it is connected to
     */
    @Inject
    public AdminCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.addedTimer = new Timer();
        this.generatedTimer = new Timer();
    }

    /**
     * Will add an activity to the database.
     * It will check if the input makes sense, and if it doesn't,
     * then it will show a popup. It also has a small label popup
     * where you can see that u added an activity.
     */
    public void addActivity() {
        long amount;
        try {
            amount = Long.parseLong(consumption.getText());
        } catch (NumberFormatException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setHeaderText(consumption.getText() + " is not a number.");
            alert.setContentText("Please enter an integer.");
            alert.setTitle("Invalid input");
            alert.showAndWait();
            return;
        }
        mainCtrl.getServer().addActivity(name.getText(), amount);
        name.clear();
        consumption.clear();

        added.setVisible(true);

        addedTimer.cancel();
        addedTimer = new Timer();
        addedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                added.setVisible(false);
            }
        }, 3000L);

    }

    /**
     * Cancel calls the cancel in mainCtrl which closes the window
     */
    public void cancel() {
        addedTimer.cancel();
        addedTimer = new Timer();
        mainCtrl.showScene(0);
    }

    /**
     * Load the screen, set up a loading bar with 5 seconds and rotate the blades
     *
     * @param scene The scene that is loaded
     */
    @Override
    public void onLoad(Scene scene) {
        added.setVisible(false);
        generated.setVisible(false);
        questionTable.setItems(null);
        questionDisplay.setText("");
    }

    /**
     * Search the server's database for questions based on their id
     */
    public void searchById() {
        Question q;
        try {
            q = mainCtrl.getServer().getQuestion(Long.parseLong(idSearchBar.getText()));
        } catch (IllegalArgumentException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setHeaderText("No question");
            alert.setContentText("There is no question by that id.");
            alert.setTitle("Invalid input");
            alert.showAndWait();
            return;
        }
        questionDisplay.setText(q.pprint());
    }

    /**
     * Search the server's question database for activities with the search term in their title.
     */
    public void searchByActivity() {
        List<SearchResult> results;
        try {
            results = mainCtrl.getServer().search(activitySearchBar.getText());
        } catch (IllegalArgumentException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setHeaderText("Too many results");
            alert.setContentText("Please choose a more specific search term.");
            alert.setTitle("Invalid input");
            alert.showAndWait();
            return;
        }
        data = FXCollections.observableList(results);
        System.out.println(data);
        questionTable.setItems(data);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(x ->
                new SimpleLongProperty((Integer) x.getValue().get("questionId")));
        colActivity.setCellValueFactory(x ->
                new SimpleStringProperty((String) x.getValue().get("activityTitle")));
        colType.setCellValueFactory(x ->
                new SimpleStringProperty((String) x.getValue().get("questionType")));
    }

    /**
     * Sends a request for the server to generate a new set of questions.
     */
    public void generateQuestions() {
        mainCtrl.getServer().generateQuestions();
        generated.setVisible(true);
        generatedTimer.cancel();
        generatedTimer = new Timer();
        generatedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                generated.setVisible(false);
            }
        }, 3000L);
    }

    /**
     * Shows the scene that displays all questions.
     */
    public void showAllQuestions() { mainCtrl.showScene(16); }
}


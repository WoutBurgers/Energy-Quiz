package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;

public class ShowQuestionsCtrl implements Ctrl {

    public MainCtrl mainCtrl;
    public ServerUtils server;

    @FXML
    TextArea displayArea;

    /**
     * Constructor for the ctrl
     *
     * @param mainCtrl the main ctrl it is connected to
     * @param server   the server
     */
    @Inject
    public ShowQuestionsCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void onLoad(Scene scene) {
        String questions = server.getAllQuestions();
        displayArea.setText(questions);
    }

    /**
     * Cancel calls the cancel in mainCtrl which closes the window.
     */
    public void cancel() {
        mainCtrl.showScene(0);
    }


    /**
     * Shows the main admin panel.
     */
    public void returnToAdmin() {
        mainCtrl.showScene(14);
    }
}

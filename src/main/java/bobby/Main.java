package bobby;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** A GUI for Bobby using FXML. */
public class Main extends Application {

    /** Chatbot instance used to generate responses for GUI input. */
    private final Bobby bobby = new Bobby();

    /**
     * Loads the main FXML view and displays the JavaFX stage.
     *
     * @param stage primary JavaFX stage
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setBobby(bobby);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

package bobby;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Controller for the main GUI. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    /** Chatbot used to generate responses for user input. */
    private Bobby bobby;

    /** Image used for user dialog boxes. */
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));

    /** Image used for Bobby dialog boxes. */
    private final Image bobbyImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /** Binds the scroll position to the height of the dialog container. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Bobby instance used by this controller. */
    public void setBobby(Bobby bobby) {
        this.bobby = bobby;
    }

    /** Creates user and chatbot dialog boxes for the submitted input. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = bobby.getResponse(input);
        String commandType = bobby.getCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBobbyDialog(response, bobbyImage, commandType)
        );
        userInput.clear();
        if ("ExitCommand".equals(commandType)) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        }
    }

}

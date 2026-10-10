import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Launch the initial Login View screen
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }

    public static void main(String[] args) {
        // Launch JavaFX Application
        launch(args);
    }
}

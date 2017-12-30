package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     *
     * @param stage Stage
     * @throws Exception
     *
     * Metoda inicjalizująca i wyświetlająca całą scenę graficzną
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     *
     * @param args
     *
     * Główna metoda uruchamiająca cały program
     */
    public static void main(String[] args) {
        launch(args);
    }

    // kompilacja aplikacji do pliku wykonywalnego za pomocą maven'a
    // mvn clean compile assembly:single

}
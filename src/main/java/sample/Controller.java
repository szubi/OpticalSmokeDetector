package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Controller {

    /**
     * Składowa osi X wykresu
     */
    @FXML
    private NumberAxis xAxis;

    /**
     * Przycisk uruchamiający cały program
     */
    @FXML
    private Button run;

    /**
     * Wykres liniowy, na który nanoszone są wartości
     */
    @FXML
    private LineChart<Number, Number> lineChart;

    /**
     * Wartości średniej z fotorezystora
     */
    @FXML
    private Label average;

    /**
     * Obecna wartość na podstawie odczytu z fotorezystora
     */
    @FXML
    private Label result;

    /**
     * Etykieta informująca, czy urządzenie jest podłączone
     */
    @FXML
    private Label is_connected;

    /**
     * Metoda inicjalizująca i uruchamiająca cały program/tworząca wątek główny
     */
    @FXML
    @SuppressWarnings("unchecked")
    public void handleButtonAction() {
        PartialResultsTask task = new PartialResultsTask(xAxis, average, result, is_connected, run, lineChart);

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

}

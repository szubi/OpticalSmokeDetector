package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Controller {

    @FXML
    private NumberAxis xAxis;

    @FXML
    private Button run;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private Label average;

    @FXML
    private Label result;

    @FXML
    private Label is_connected;

    @FXML
    @SuppressWarnings("unchecked")
    public void handleButtonAction() {
        PartialResultsTask task = new PartialResultsTask(xAxis, average, result, is_connected, run, lineChart);

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

}

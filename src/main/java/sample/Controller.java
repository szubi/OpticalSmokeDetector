package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    private  Label result;

    @FXML
    private Label is_connected;

    @FXML
    @SuppressWarnings("unchecked")
    public void handleButtonAction() {
        PartialResultsTask task = new PartialResultsTask(xAxis, average, result, is_connected);
        XYChart.Series data = new XYChart.Series(task.getPartialResults());
        data.setName("Fotorezystor");

        XYChart.Series data2 = new XYChart.Series(task.getPartialResults2());
        data2.setName("Fototranzystor");

        XYChart.Series data3 = new XYChart.Series(task.getPartialResults3());
        data3.setName("Odbiciowy");

        run.setDisable(true);
        lineChart.getData().addAll(data, data2, data3);

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

}

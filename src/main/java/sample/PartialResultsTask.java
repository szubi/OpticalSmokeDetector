package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import static sample.Device.checkPattern;

public class PartialResultsTask extends Task<ObservableList> {

    private static int MAX_DATA_POINTS = 25;

    private NumberAxis xAxis;
    private Label label;
    private Label resultLabel;
    private Label is_connected;
    private Button run;

    private LineChart<Number, Number> lineChart;
    private XYChart.Series data;
    private XYChart.Series data2;
    private XYChart.Series data3;

    private XYChart.Data<Integer, Integer> chart;
    private XYChart.Data<Integer, Integer> chart2;
    private XYChart.Data<Integer, Integer> chart3;

    private double photoresistor_sum;
    private double phototransistor_sum;
    private double reflective_sum;

    private int nextRaportNumber;
    private int i;

    private Raport raport;
    private Runnable task;


    public PartialResultsTask(NumberAxis xAxis, Label average, Label result, Label is_connected, Button run, LineChart<Number, Number> lineChart) {
        this.xAxis = xAxis;
        this.label = average;
        this.resultLabel = result;
        this.is_connected = is_connected;
        this.run = run;
        this.lineChart = lineChart;
    }

    // Uses Java 7 diamond operator
    @SuppressWarnings("unchecked")
    public ReadOnlyObjectWrapper<ObservableList> partialResults
            = new ReadOnlyObjectWrapper<>(this, "partialResults",
            FXCollections.observableArrayList(new ArrayList()));

    // Uses Java 7 diamond operator
    @SuppressWarnings("unchecked")
    public ReadOnlyObjectWrapper<ObservableList> partialResults2
            = new ReadOnlyObjectWrapper<>(this, "partialResults",
            FXCollections.observableArrayList(new ArrayList()));

    // Uses Java 7 diamond operator
    @SuppressWarnings("unchecked")
    public ReadOnlyObjectWrapper<ObservableList> partialResults3
            = new ReadOnlyObjectWrapper<>(this, "partialResults",
            FXCollections.observableArrayList(new ArrayList()));

    public final ObservableList getPartialResults() {
        return partialResults.get();
    }

    public final ObservableList getPartialResults2() {
        return partialResults2.get();
    }

    public final ObservableList getPartialResults3() {
        return partialResults3.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ObservableList call() throws Exception {

        if (checkPattern()) {

            task = () -> {
                data = new XYChart.Series(getPartialResults());
                data.setName("Fotorezystor");

                data2 = new XYChart.Series(getPartialResults2());
                data2.setName("Fototranzystor");

                data3 = new XYChart.Series(getPartialResults3());
                data3.setName("Odbiciowy");

                lineChart.getData().addAll(data, data2, data3);
                lineChart.setLegendVisible(true);

                run.setDisable(true);
            };
            Platform.runLater(task);

            raport = new Raport("raports", "raports_quantity");
            nextRaportNumber = Integer.parseInt(raport.readFromFile("raports_quantity"));
            raport.delete(nextRaportNumber);
            String data = "Natężenie dymu Fotorezystor;\tWartość średnia;\tNatężenie dymu Fototranzystor;\tWartość średnia;\tNatężenie dymu Odbiciowy;\tWartość średnia;";
            raport.createFileAndWrite("raport-" + nextRaportNumber, data, true, true);

            while (true) {
                if (isCancelled()) {
                    break;
                }

                if (!Device.isOpened) {
                    checkPattern();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PartialResultsTask.class.getName()).log(Level.SEVERE, null, ex);
                }

                int count = i;
                task = () -> {

                    if (!Device.isOpened) {
                        is_connected.setText("Urządzenie odłączone");
                    } else {
                        is_connected.setText("Urządzenie podłączone");
                    }

                    List<Integer> latency = Device.get();

                    chart = new XYChart.Data<>(count, latency.get(1));
                    chart2 = new XYChart.Data<>(count, latency.get(2));
                    chart3 = new XYChart.Data<>(count, latency.get(3));

                    partialResults.get().add(chart);
                    partialResults2.get().add(chart2);
                    partialResults3.get().add(chart3);

                    if (count > MAX_DATA_POINTS) {
                        partialResults.get().remove(0);
                        partialResults2.get().remove(0);
                        partialResults3.get().remove(0);
                    }

                    if (count > MAX_DATA_POINTS - 1) {
                        xAxis.setLowerBound(xAxis.getLowerBound() + 1);
                        xAxis.setUpperBound(xAxis.getUpperBound() + 1);
                    }

                    photoresistor_sum += latency.get(1);
                    double average_1 = photoresistor_sum / i;
                    average_1 *= 10000;
                    average_1 = Math.round(average_1);
                    average_1 /= 10000;

                    phototransistor_sum += latency.get(2);
                    double average_2 = phototransistor_sum / i;
                    average_2 *= 10000;
                    average_2 = Math.round(average_2);
                    average_2 /= 10000;

                    reflective_sum += latency.get(3);
                    double average_3 = reflective_sum / i;
                    average_3 *= 10000;
                    average_3 = Math.round(average_3);
                    average_3 /= 10000;

                    resultLabel.setText(""+latency.get(1));
                    label.setText(""+average_1);

                    String rap = String.valueOf(
                            latency.get(1)) + ";\t\t\t\t" + String.valueOf(average_1) +
                            ";\t\t\t" + latency.get(2) + ";\t\t\t\t" + String.valueOf(average_2)  +
                            ";\t\t\t" + latency.get(3) + ";\t\t\t\t" + String.valueOf(average_3) + ";";

                    raport.createFileAndWrite("raport-" + nextRaportNumber, rap, true, true);
                };
                Platform.runLater(task);

                updateProgress(i, 1000);
                i++;
            }
        } else {
            task = () -> {
                is_connected.setText("Urządzenie odłączone");
                AlertBox.display("", "Brak połączenia z czujnikiem");
            };
            Platform.runLater(task);
        }

        return partialResults.get();
    }
}

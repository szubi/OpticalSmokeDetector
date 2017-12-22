package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import static sample.Device.checkPattern;

public class PartialResultsTask extends Task<ObservableList> {

    private static int MAX_DATA_POINTS = 25;

    private NumberAxis xAxis;
    private Label label;
    private Label resultLabel;
    private Label is_connected;

    private XYChart.Data<Integer, Integer> chart;
    private XYChart.Data<Integer, Integer> chart2;
    private XYChart.Data<Integer, Integer> chart3;

    private double result;
    private int nextRaportNumber;
    private int i;

    private Raport raport;
    private Runnable task;


    public PartialResultsTask(NumberAxis xAxis, Label average, Label result, Label is_connected) {
        this.xAxis = xAxis;
        this.label = average;
        this.resultLabel = result;
        this.is_connected = is_connected;
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

    public final ReadOnlyObjectProperty partialResultsProperty() {
        return partialResults.getReadOnlyProperty();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ObservableList call() throws Exception {

        System.out.println("Is it working?");

        System.out.flush();

        /*try {
            SerialPort serialPort = new SerialPort("/dev/tty.usbmodem1413");
            System.out.println("cos");
            serialPort.openPort();
        } catch (SerialPortException e) {
            System.out.println("błąd: " + e);
        }*/

        if (checkPattern()) {

            System.out.println("Doesn't work here after checkPattern method");
            //Device device = new Device();

            raport = new Raport("raports", "raports_quantity");
            nextRaportNumber = Integer.parseInt(raport.readFromFile("raports_quantity"));
            raport.createFileAndWrite("raport-" + nextRaportNumber, "Natężenie dymu Fotorezystor;\t\tWartość średnia;\t\tNatężenie dymu Demo-1;\t\tWartość średnia;", true, true);

            //System.out.println("Urworzone pliki");

            while (true) {

                if (isCancelled()) {
                    break;
                }

                if (!Device.isOpened) {
                    //System.out.println("Uruchamiam port???????????????\n\n");
                    checkPattern();
                }

                try {
                    Thread.sleep(1000);
                    //System.out.println("WYKONUJE SIĘ");
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

                    //int latency = device.get();

                    List<Integer> latency = Device.get();

                    //System.out.println("wartosc latency: " + latency);

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

                    result += latency.get(1);
                    double average = result / i;
                    average *= 10000;
                    average = Math.round(average);
                    average /= 10000;

                    //System.out.println("srednia: " + srednia);

                    resultLabel.setText(""+latency.get(1));
                    label.setText(""+average);

                    String rap = String.valueOf(latency.get(1)) + ";\t\t\t\t\t\t\t\t\t" + String.valueOf(average) + ";\t\t\t\t\t" + latency.get(2) + ";\t\t\t\t\t\t\t" + 1 + ";";

                    raport.createFileAndWrite("raport-" + nextRaportNumber, rap, true, true);
                };

                Platform.runLater(task);
                updateProgress(i, 1000);
                i++;
            }
        } else {
            System.out.println("Something 2");
            //czy_podlaczone.setText("Urządzenie odłączone");
            //AlertBox.display("", "Brak połączenia z czujnikiem");
        }

        return partialResults.get();
    }

}

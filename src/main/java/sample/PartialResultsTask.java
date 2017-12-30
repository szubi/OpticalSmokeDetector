package sample;

import java.util.List;
import javafx.application.Platform;
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

    /**
     * Długość osi X
     */
    private static int MAX_DATA_POINTS = 25;

    /**
     * Składowa osi X wykresu
     */
    private NumberAxis xAxis;

    /**
     * Wartości średniej z fotorezystora
     */
    private Label average;

    /**
     * Obecna wartość na podstawie odczytu z fotorezystora
     */
    private Label resultLabel;

    /**
     * Etykieta informująca, czy urządzenie jest podłączone
     */
    private Label is_connected;

    /**
     * Przycisk uruchamiający cały program
     */
    private Button run;

    /**
     * Wykres liniowy, na który nanoszone są wartości
     */
    private LineChart<Number, Number> lineChart;

    /**
     * Suma kolejnych wartości odczytywanych z urządzenia - fotorezystor
     */
    private double photoresistor_sum;

    /**
     * Suma kolejnych wartości odczytywanych z urządzenia - fototranzystor
     */
    private double phototransistor_sum;

    /**
     * Suma kolejnych wartości odczytywanych z urządzenia - czujnik odbiciowy
     */
    private double reflective_sum;

    /**
     * Numer kolejnego raportu - zapisywany do pliku
     */
    private int nextRaportNumber;

    /**
     * Pole przechowujące ilość inkrementacji głównej pętli programu oraz dzielnik dla średniej
     */
    private int i;

    /**
     * Pole przechowujące instancję klasy Raport odpowiedzialną za utworzenie
     * pliku raports_quantity, katalogu dla raportów oraz raportów
     */
    private Raport raport;

    /**
     * Pole przechowujące instancję wątku wykonującego się w tle
     */
    private Runnable task;

    /**
     * Lista obiektów pozwalająca na śledzenie/monitorowanie zmian w momencie ich wystąpienia
     * Inicjalizacja poprzez pustą listę tablic.
     *
     * Odpowiednio dla Fotorezystora, Fototranzystora, Czujnika Odbiciowego
     */
    private ObservableList partialResults_photoresistor = FXCollections.observableArrayList();
    private ObservableList partialResults_phototransistor = FXCollections.observableArrayList();
    private ObservableList partialResults_reflective = FXCollections.observableArrayList();

    /**
     *
     * @param xAxis NumberAxis
     * @param average Label
     * @param result Label
     * @param is_connected Label
     * @param run Button
     * @param lineChart LineChart<Number, Number>
     *
     * Konstruktor przyjmujący Obiekty sceny JavaFX odpowiedzialny za inicjalizację pól
     */
    public PartialResultsTask(NumberAxis xAxis, Label average, Label result, Label is_connected, Button run, LineChart<Number, Number> lineChart) {
        this.xAxis = xAxis;
        this.average = average;
        this.resultLabel = result;
        this.is_connected = is_connected;
        this.run = run;
        this.lineChart = lineChart;
    }

    /**
     *
     * @return ObservableList
     * @throws Exception
     *
     * Główny algorytm programu
     * Metoda sprawdza, czy urzdzenie jest podłączone,
     * Jeżeli tak, inicjalizuje graficzną scenę, tworzy katalog raportów, plik zliczający raporty
     * oraz plik raportu(z nagłówkiem),
     *
     * Program przechodzi do głównej pętli programu, gdzie wykonywany jest wątek działający w tle @task
     * Ma on za zadanie utworzyć serię wartości(punktów), które zostaną naniesione na wykres
     * oraz obliczyć średnią z każdego pomiaru natężenia
     * Kolejne wartości natężenia oraz jej średnie wartości zostają zapisane do pliku jako raport
     *
     * Jeżeli urządzenie jest odłączone lub zostanie odłączone w trakcie pracy, program poinformuje o tym wyświetlając
     * odpowiedni komunikat
     */
    @Override
    @SuppressWarnings("unchecked")
    protected ObservableList call() throws Exception {

        if (checkPattern()) {

            task = () -> {
                XYChart.Series data_photoresistor = new XYChart.Series(partialResults_photoresistor);
                data_photoresistor.setName("Fotorezystor");

                XYChart.Series data_phototransistor = new XYChart.Series(partialResults_phototransistor);
                data_phototransistor.setName("Fototranzystor");

                XYChart.Series data_reflective = new XYChart.Series(partialResults_reflective);
                data_reflective.setName("Odbiciowy");

                lineChart.getData().addAll(data_photoresistor, data_phototransistor, data_reflective);
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
                    System.out.println("Wyjątek: " + ex);
                }

                int count = i;
                task = () -> {

                    if (!Device.isOpened) {
                        is_connected.setText("Urządzenie odłączone");
                    } else {
                        is_connected.setText("Urządzenie podłączone");
                    }

                    List<Integer> latency = Device.get();
                    XYChart.Data<Integer, Integer> point_photoresistor = new XYChart.Data<>(count, latency.get(1));
                    XYChart.Data<Integer, Integer> point_phototransistor = new XYChart.Data<>(count, latency.get(2));
                    XYChart.Data<Integer, Integer> point_reflective = new XYChart.Data<>(count, latency.get(3));

                    partialResults_photoresistor.add(point_photoresistor);
                    partialResults_phototransistor.add(point_phototransistor);
                    partialResults_reflective.add(point_reflective);

                    if (count > MAX_DATA_POINTS) {
                        partialResults_photoresistor.remove(0);
                        partialResults_phototransistor.remove(0);
                        partialResults_reflective.remove(0);
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
                    average.setText(""+average_1);

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

        return partialResults_photoresistor;
    }
}

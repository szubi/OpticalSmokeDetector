package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;


public class Device {

    /**
     * Pole przechowujące instancję/uchwyt do portu
     */
    private static SerialPort serialPort;

    /**
     * Wzorzec, na podstawie którego wyszukiwany jest port urządzenia
     */
    private final static String PATTERN = "([0-9]{1,3};){5}";

    /**
     * Flaga mówiąca o tym, czy port jest otwarty
     */
    public static boolean isOpened;


    /**
     * Metoda odpowiedzialna za odczyt danych z urządzenia
     * Jeżeli port jest zamknięty zwraca listę o wartościach 0
     * Jeżeli port jest otwarty odczytuje dane z urządzenia w postaci String'u,
     * parsuje je i rzutuje na Integer dodając do listy, którą ma za zadanie zwrócić
     *
     * @return List<Integer>
     */
    public static List<Integer> get() {

        if (serialPort.isOpened()) {

            try {

                String buffer = serialPort.readString();
                System.out.println("buffor: " + buffer);

                if (buffer != null && !buffer.isEmpty()) {

                    String array[] = buffer.split(";");

                    List<Integer> lista = new ArrayList<>();

                    lista.add(Integer.parseInt(array[0].trim()));
                    lista.add(Integer.parseInt(array[1].trim()));
                    lista.add(Integer.parseInt(array[2].trim()));
                    lista.add(Integer.parseInt(array[3].trim()));

                    if (lista.get(0) > 10) {
                        lista.add(0, 10);
                    }

                    if (lista.get(1) > 10) {
                        lista.add(1, 10);
                    }

                    if (lista.get(2) > 10) {
                        lista.add(2, 10);
                    }

                    if (lista.get(3) > 10) {
                        lista.add(3, 10);
                    }

                    return lista;
                } else {
                    serialPort.closePort();
                    isOpened = false;

                    AlertBox.display("", "Brak połączenia z czujnikiem");
                }

            } catch (SerialPortException e) {
                e.printStackTrace();
            }

        } else {
            isOpened = false;
        }

        List<Integer> lista = new ArrayList<>();
        lista.add(0);
        lista.add(0);
        lista.add(0);
        lista.add(0);

        return lista;
    }


    /**
     * Metoda sprawdzająca na podstawie wzorca,
     * czy na danym porcie komputera jest podłączone urządzenie
     * Jeżeli tak, zwraca wartość true
     * W przeciwnym wypadku sprawdza inny port.
     * Jeżeli sprawdzi każdy port i nie znajdzie urządzenia,
     * zwróci wartość false
     *
     * @return Boolean
     */
    public static boolean checkPattern()  {

        String portNames[] = SerialPortList.getPortNames();

        for (int i = 0; i < portNames.length; i++) {

            try {
                serialPort = new SerialPort(portNames[i]);
                serialPort.openPort();
                serialPort.setParams(115200, 8, 1, 0);

                Thread.sleep(1000);

                String firstValue = serialPort.readString();

                if (firstValue == null) {
                    serialPort.closePort();
                    continue;
                }

                Thread.sleep(1000);

                String wartosc = (serialPort.readString()).trim();

                if (Pattern.matches(PATTERN, wartosc)) {
                    isOpened = true;
                    return true;
                }
                //serialPort.closePort();
            } catch (SerialPortException e) {
                System.out.println(e);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        return false;
    }
}


package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;


public class Device {

    private static SerialPort serialPort;
    private final static String PATTERN = "([0-9]{1,3};){5}";

    public static boolean isOpened;


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


    public static boolean checkPattern()  {

        System.out.println("Doesn't work in jar");

        String portsArray[] = SerialPortList.getPortNames();

        for (String port : portsArray) {
            try {
                serialPort = new SerialPort(port);
                serialPort.openPort();
                serialPort.setParams(115200, 8, 1, 0);
                serialPort.readString();

                Thread.sleep(1000);

                if (Pattern.matches(PATTERN, (serialPort.readString()).trim())) {

                    System.out.println("nazwa portu: " + port);
                    isOpened = true;
                    return true;
                }
                serialPort.closePort();
            } catch (SerialPortException e) {
                System.out.println(e);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        return false;
    }
}


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

        String portNames[] = SerialPortList.getPortNames();

        /*String[] portNames = SerialPortList.getPortNames();
        for(int i = 0; i < portNames.length; i++){
            System.out.println(portNames[i]);
        }*/

        //String [] portNames = {"COM1", "COM2", "/dev/tty.usbmodem1413", "/dev/tty.usbmodem1423"};

        for (int i = 0; i < portNames.length; i++) {

            System.out.println("Twój port: " + portNames[i]);

            try {
                serialPort = new SerialPort(portNames[i]);
                serialPort.openPort();
                serialPort.setParams(115200, 8, 1, 0);

                Thread.sleep(1000);

                String firstValue = serialPort.readString();

                if (firstValue == null) {
                    System.out.println("Port pominięty - wartość: " + firstValue);
                    serialPort.closePort();
                    continue;
                }

                System.out.println("Pierwszy odczyt: " + firstValue);

                Thread.sleep(1000);

                String wartosc = (serialPort.readString()).trim();

                System.out.println("wartość: " + wartosc);

                if (Pattern.matches(PATTERN, wartosc)) {

                    System.out.println("nazwa portu: " + portNames[i]);
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


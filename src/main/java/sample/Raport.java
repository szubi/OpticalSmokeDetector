package sample;

import java.io.*;

public class Raport extends Thread {

    /**
     * Nazwa katalogu, w którym są umieszczane raporty
     */
    private static String DIRECTORY_NAME;

    /**
     * Nazwa pliku zliczającego kolejno utworzone raporty
     */
    private static String RAPORTS_QUANTITY_FILE;

    /**
     *
     * @param raportsDirectoryName String
     * @param raportsQuantityFile String
     *
     * konstruktor inicjalizujący składowe DIRECTORY_NAME oraz RAPORTS_QUANTITY_FILE
     * Ma za zadanie utworzyć folder dla plików raportów oraz plik zliczający wszystkie raporty
     */
    public Raport(String raportsDirectoryName, String raportsQuantityFile) {
        DIRECTORY_NAME = raportsDirectoryName;
        RAPORTS_QUANTITY_FILE = raportsQuantityFile;

        createRaportsFolderIfDoesntExist();
        createRaportsQuantityFile();
    }

    /**
     * Metoda tworząca folder dla raportów, jeśli ten nie został wcześniej utworzony.
     */
    private void createRaportsFolderIfDoesntExist() {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    /**
     *
     * @param fileName String
     *
     * Metoda odczytująca dane z pliku
     * Przyjmuje nazwę pliku/ścieżkę jako String
     *
     * Zwraca odczytaną wartość znajdującą się w pliku jako String
     * @return String
     */
    public String readFromFile(String fileName) {
        String data = "";

        try {
            FileReader reader = new FileReader(fileName + ".txt");
            BufferedReader buffer = new BufferedReader(reader);
            data = buffer.readLine().trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     *
     * @param fileName String
     * @param data String
     * @param append Boolean
     * @param raport Boolean
     *
     * W przypadku tego programu jest to uniwersalna metoda do tworzenia plików
     * lub też dodawania wartości do istniejącego pliku
     *
     * Przyjmuje nazwe pliku, ciąg znaków, który ma zostać do niego zapisany,
     * flagę append służacą do rozróżnienia, czy plik ma zostać nadpisany(utworzony na nowo),
     * czy wartości mają zostać dodane do już istniejącego
     * flagę która mówi o tym czy plik jest raportem i ma zostać utworzony w katalogu
     * z DIRECTORY_NAME, czy poza nim jako plik RAPORTS_QUANTITY_FILE
     */
    public void createFileAndWrite(String fileName, String data, Boolean append, Boolean raport) {
        try {

            File file = null;
            if (raport) {
                file = new File(DIRECTORY_NAME + "/" + fileName + ".txt");
            } else {
                file = new File(fileName + ".txt");
            }

            FileWriter writer = (append) ? new FileWriter(file, true) : new FileWriter(file);

            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Funckja tworząca plik RAPORTS_QUANTITY_FILE z użyciem funkcji createFileAndWrite
     * Jeżeli plik nie istnieje, tworzony jest nowy plik z domyślną wartością 1
     * Jeżeli plik istnieje, zostaje odczytana z niego wartość do której zostaje dodana 1 - kolejny numer raportu
     */
    private void createRaportsQuantityFile() {
        if (isFileExists(RAPORTS_QUANTITY_FILE + ".txt")) {

            String quantity = readFromFile(RAPORTS_QUANTITY_FILE);

            int raports_quantity;

            try {
                raports_quantity = Integer.parseInt(quantity);
            } catch (NumberFormatException e) {
                System.out.println("wyjątek: " + e);

                raports_quantity = 0;
            }

            int i = raports_quantity+1;

            createFileAndWrite(RAPORTS_QUANTITY_FILE, String.valueOf(i), false, false);
        } else {
            createFileAndWrite(RAPORTS_QUANTITY_FILE, "1", false, false);
        }
    }

    /**
     *
     * @param number integer
     * @return boolean
     *
     * Usuwanie pliku raportu o danym numerze jeśli istnieje
     */
    public boolean delete(int number) {
        File file = new File(DIRECTORY_NAME + "/" + "raport-" + number + ".txt");

        return file.delete();
    }

    /**
     *
     * @param fileName String
     *
     * Sprawdza czy plik o podanej nazwie istnieje
     *
     * @return Boolean
     */
    public boolean isFileExists(String fileName) {
        File file = new File(fileName);

        return file.exists();
    }
}

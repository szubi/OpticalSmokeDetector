package sample;

import java.io.*;

public class Raport extends Thread {

    private static String DIRECTORY_NAME;
    private static String RAPORTS_QUANTITY_FILE;

    public Raport(String raportsDirectoryName, String raportsQuantityFile) {
        DIRECTORY_NAME = raportsDirectoryName;
        RAPORTS_QUANTITY_FILE = raportsQuantityFile;

        createRaportsFolderIfDoesntExist();
        createRaportsQuantityFile();
    }

    private void createRaportsFolderIfDoesntExist() {
        File directory = new File(DIRECTORY_NAME);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

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

    public boolean delete(int number) {
        File file = new File(DIRECTORY_NAME + "/" + "raport-" + number + ".txt");

        return file.delete();
    }

    public boolean isFileExists(String fileName) {
        File file = new File(fileName);

        return file.exists();
    }


}

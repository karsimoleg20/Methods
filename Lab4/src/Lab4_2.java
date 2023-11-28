import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

class Garden {
    private int[][] plants;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    public Garden(int rows, int cols) {
        this.plants = new int[rows][cols];
    }

    public void waterPlant(int row, int col) {
        writeLock.lock();
        try {
            plants[row][col] = 1; // 1 відповідає за полив
        } finally {
            writeLock.unlock();
        }
    }

    public void changePlantState(int row, int col, int newState) {
        writeLock.lock();
        try {
            plants[row][col] = newState;
        } finally {
            writeLock.unlock();
        }
    }

    public int getPlantState(int row, int col) {
        readLock.lock();
        try {
            return plants[row][col];
        } finally {
            readLock.unlock();
        }
    }

    public int getRows() {
        return plants.length;
    }

    public int getCols() {
        return plants[0].length;
    }
}

class Gardener implements Runnable {
    private Garden garden;
    private volatile boolean running = true;

    public Gardener(Garden garden) {
        this.garden = garden;
    }

    public void run() {
        while (running) {
            int row = getRandomRow();
            int col = getRandomCol();
            garden.waterPlant(row, col);
            System.out.println("Садівник поливає рослину в рядку " + row + ", колонці " + col);
            sleepForRandomTime();
        }
    }

    public void stop() {
        running = false;
    }

    private int getRandomRow() {
        return new Random().nextInt(garden.getRows());
    }

    private int getRandomCol() {
        return new Random().nextInt(garden.getCols());
    }

    private void sleepForRandomTime() {
        try {
            Thread.sleep(new Random().nextInt(3000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Nature implements Runnable {
    private Garden garden;
    private volatile boolean running = true;

    public Nature(Garden garden) {
        this.garden = garden;
    }

    public void run() {
        while (running) {
            int row = getRandomRow();
            int col = getRandomCol();
            int newState = getRandomState();
            garden.changePlantState(row, col, newState);
            System.out.println("Природа змінює стан рослини в рядку " + row + ", колонці " + col + " на " + newState);
            sleepForRandomTime();
        }
    }

    public void stop() {
        running = false;
    }

    private int getRandomRow() {
        return new Random().nextInt(garden.getRows());
    }

    private int getRandomCol() {
        return new Random().nextInt(garden.getCols());
    }

    private int getRandomState() {
        return new Random().nextInt(4) + 1; // 1 - healthy, 2 - wilted, 3 - dead, 4 - blooming
    }

    private void sleepForRandomTime() {
        try {
            Thread.sleep(new Random().nextInt(3000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Monitor1 implements Runnable {
    private Garden garden;
    private volatile boolean running = true;

    public Monitor1(Garden garden) {
        this.garden = garden;
    }

    public void run() {
        try (FileWriter writer = new FileWriter("garden_state.txt", true)) {
            while (running) {
                writeGardenStateToFile(writer);
                sleepForRandomTime();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }

    private void writeGardenStateToFile(FileWriter writer) throws IOException {
        StringBuilder state = new StringBuilder();
        state.append("Стан саду:\n");
        for (int i = 0; i < garden.getRows(); i++) {
            for (int j = 0; j < garden.getCols(); j++) {
                state.append(garden.getPlantState(i, j)).append(" ");
            }
            state.append("\n");
        }
        state.append("\n");

        writer.write(state.toString());
        writer.flush();
    }

    private void sleepForRandomTime() {
        try {
            Thread.sleep(new Random().nextInt(5000) + 2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Monitor2 implements Runnable {
    private Garden garden;
    private volatile boolean running = true;

    public Monitor2(Garden garden) {
        this.garden = garden;
    }

    public void run() {
        while (running) {
            printGardenState();
            sleepForRandomTime();
        }
    }

    public void stop() {
        running = false;
    }

    private void printGardenState() {
        System.out.println("Стан саду:");
        for (int i = 0; i < garden.getRows(); i++) {
            for (int j = 0; j < garden.getCols(); j++) {
                System.out.print(garden.getPlantState(i, j) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void sleepForRandomTime() {
        try {
            Thread.sleep(new Random().nextInt(5000) + 2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Lab4_2 {
    public static void main(String[] args) {
        int rows = 5;
        int cols = 5;
        Garden garden = new Garden(rows, cols);

        Gardener gardener = new Gardener(garden);
        Thread gardenerThread = new Thread(gardener);
        Nature nature = new Nature(garden);
        Thread natureThread = new Thread(nature);
        Monitor1 monitor1 = new Monitor1(garden);
        Thread monitor1Thread = new Thread(monitor1);
        Monitor2 monitor2 = new Monitor2(garden);
        Thread monitor2Thread = new Thread(monitor2);

        gardenerThread.start();
        natureThread.start();
        monitor1Thread.start();
        monitor2Thread.start();

        try {
            Thread.sleep(20000);
            gardener.stop();
            nature.stop();
            monitor1.stop();
            monitor2.stop();
            gardenerThread.join();
            natureThread.join();
            monitor1Thread.join();
            monitor2Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
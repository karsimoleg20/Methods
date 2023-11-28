import java.util.concurrent.locks.*;

class Database {
    private String data;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    public String read() {
        readLock.lock();
        try {
            return data;
        } finally {
            readLock.unlock();
        }
    }

    public void write(String newData) {
        writeLock.lock();
        try {
            data = newData;
        } finally {
            writeLock.unlock();
        }
    }
}

class Reader implements Runnable {
    private Database database;
    private String query;

    public Reader(Database database, String query) {
        this.database = database;
        this.query = query;
    }

    public void run() {
        String data = database.read();

        System.out.println("Reader: Запит - " + query + ", Результат - " + data);
    }
}

class Writer implements Runnable {
    private Database database;
    private String newData;

    public Writer(Database database, String newData) {
        this.database = database;
        this.newData = newData;
    }

    public void run() {
        database.write(newData);

        System.out.println("Writer: Оновлення - " + newData);
    }
}

public class Lab4_1 {
    public static void main(String[] args) {
        Database database = new Database();

        Thread readerThread1 = new Thread(new Reader(database, "Запит на ім'я"));
        Thread readerThread2 = new Thread(new Reader(database, "Запит на телефон"));
        Thread writerThread1 = new Thread(new Writer(database, "Нові дані: Ім'я - John, Телефон - 123456789"));

        readerThread1.start();
        readerThread2.start();
        writerThread1.start();

        try {
            readerThread1.join();
            readerThread2.join();
            writerThread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



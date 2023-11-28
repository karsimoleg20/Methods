import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class Item {
    private String name;
    private int value;

    public Item(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}

class Storage {
    private BlockingQueue<Item> items;

    public Storage(int capacity) {
        items = new ArrayBlockingQueue<>(capacity);
    }

    public void put(Item item) throws InterruptedException {
        items.put(item);
    }

    public Item take() throws InterruptedException {
        return items.take();
    }
}

class Ivanov implements Runnable {
    private Storage storage;

    public Ivanov(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        int total = 0;

        try {
            while (true) {
                total++;
                Item item = new Item("Item", 100);
                storage.put(item);
                System.out.println("Іванов виніс предмет " + total);
                Thread.sleep(1000); // Симуляція часу на вивід
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Petrov implements Runnable {
    private Storage storage;

    public Petrov(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        int total = 0;

        try {
            while (true) {
                total++;
                Item item = storage.take();
                System.out.println("Петров завантажив предмет " + total);
                Thread.sleep(1500); // Симуляція часу на завантаження
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Nechiporchuk implements Runnable {
    private Storage storage;

    public Nechiporchuk(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        int total = 0; // Змінна для зберігання загальної вартості

        try {
            while (true) {
                Item item = storage.take();
                total += item.getValue(); // Додавання вартості нового предмета
                System.out.println("Нечипорчук підраховує загальну вартість: " + total);
                Thread.sleep(2000); // Симуляція часу на підрахунок вартості
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Prapor {
    public static void main(String[] args) {
        int items = 10;
        Storage storage = new Storage(items);

        Thread ivanovThread = new Thread(new Ivanov(storage));
        Thread petrovThread = new Thread(new Petrov(storage));
        Thread nechiporchukThread = new Thread(new Nechiporchuk(storage));

        ivanovThread.start();
        petrovThread.start();
        nechiporchukThread.start();
    }
}

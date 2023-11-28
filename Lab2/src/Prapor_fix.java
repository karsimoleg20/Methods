import java.util.concurrent.*;

// Клас, що представляє предмети майна
class Item_fix {
    private int id;
    private int value;


    public Item_fix(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }
}

// Клас для прапорщика Іванова, який виносить предмети
class Ivanovv implements Runnable {
    private BlockingQueue<Item_fix> fromStorage;  // Черга для отримання предметів зі складу
    private BlockingQueue<Item_fix> toPetrov;    // Черга для передачі предметів Петрову

    public Ivanovv(BlockingQueue<Item_fix> fromStorage, BlockingQueue<Item_fix> toPetrov) {
        this.fromStorage = fromStorage;
        this.toPetrov = toPetrov;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Item_fix item = fromStorage.poll(1, TimeUnit.SECONDS); // Отримуємо предмет зі складу
                if (item == null) {
                    System.out.println("Іванов закінчив роботу.");
                    break;
                }

                System.out.println("Іванов виніс предмет " + item.getId());
                toPetrov.put(item); // Передаємо предмет Петрову
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Клас для прапорщика Петрова, який завантажує предмети
class Petrovv implements Runnable {
    private BlockingQueue<Item_fix> fromIvanov;     // Черга для отримання предметів від Іванова
    private BlockingQueue<Integer> resultQueue;  // Черга для передачі результатів (вартості майна) Нечипорчуку

    public Petrovv(BlockingQueue<Item_fix> fromIvanov, BlockingQueue<Integer> resultQueue) {
        this.fromIvanov = fromIvanov;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Item_fix item = fromIvanov.poll(1, TimeUnit.SECONDS); // Отримуємо предмет від Іванова
                if (item == null) {
                    System.out.println("Петров закінчив роботу.");
                    break;
                }

                System.out.println("Петров завантажив предмет " + item.getId());

                // Затримка для імітації роботи
                Thread.sleep(100);

                resultQueue.put(item.getValue()); // Передаємо вартість предмета Нечипорчуку
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Клас для прапорщика Нечипорчука, який рахує загальну вартість майна
class Nechiporchukk implements Runnable {
    private BlockingQueue<Integer> resultQueue; // Черга для отримання вартості предметів від Петрова

    public Nechiporchukk(BlockingQueue<Integer> resultQueue) {
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        int totalValue = 0;
        try {
            while (true) {
                Integer value = resultQueue.poll(1, TimeUnit.SECONDS); // Отримуємо вартість предмета від Петрова
                if (value == null) {
                    break;
                }
                totalValue += value;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Загальна вартість майна: " + totalValue); // Виводимо загальну вартість майна
    }
}

public class Prapor_fix {
    public static void main(String[] args) {
        int itemCount = 10; // Кількість предметів
        int itemValue = 100; // Вартість кожного предмета

        BlockingQueue<Item_fix> storage = new LinkedBlockingQueue<>(); // Склад з предметами майна
        BlockingQueue<Item_fix> ivanovToPetrov = new LinkedBlockingQueue<>(); // Черга для передачі предметів від Іванова до Петрова
        BlockingQueue<Integer> resultQueue = new LinkedBlockingQueue<>(); // Черга для передачі результатів Нечипорчуку

        for (int i = 1; i <= itemCount; i++) {
            storage.add(new Item_fix(i, itemValue)); // Додаємо предмети майна на склад
        }

        Thread ivanovThread = new Thread(new Ivanovv(storage, ivanovToPetrov));
        Thread petrovThread = new Thread(new Petrovv(ivanovToPetrov, resultQueue));
        Thread nechiporchukThread = new Thread(new Nechiporchukk(resultQueue));

        ivanovThread.start();
        petrovThread.start();
        nechiporchukThread.start();

        try {
            ivanovThread.join();
            petrovThread.join();
            nechiporchukThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

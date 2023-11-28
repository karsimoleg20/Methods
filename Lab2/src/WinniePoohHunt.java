import java.util.concurrent.*;

class Winnie {
    public static void main(String[] args) {
        int beesCount = 5; // Кількість зграї бджіл
        int size = 20; // Розмір лісу
        int location = ThreadLocalRandom.current().nextInt(0, size); // Вінні-Пух розташований десь у лісі

        ExecutorService executor = Executors.newFixedThreadPool(beesCount); // Створюємо пул потоків для бджіл
        BlockingQueue<Integer> tasks = new LinkedBlockingQueue<>(); // Створюємо чергу завдань для бджіл

        // Розподіл завдань між зграями бджіл
        for (int i = 0; i < size; i++) {
            tasks.add(i); // Додаємо ділянку лісу в чергу завдань
        }

        for (int i = 0; i < beesCount; i++) {
            executor.submit(new Bee(i, tasks, location)); // Створюємо і запускаємо бджілу
        }

        executor.shutdown(); // Завершуємо виконання всіх завдань в пулі потоків
    }
}

class Bee implements Runnable {
    private final int ID;
    private final BlockingQueue<Integer> tasks;
    private final int location;

    public Bee(int ID, BlockingQueue<Integer> tasks, int location) {
        this.ID = ID; // Ідентифікатор бджоли
        this.tasks = tasks; // Черга завдань для бджіл
        this.location = location; // Місцезнаходження Вінні-Пуха
    }

    @Override
    public void run() {
        try {
            while (true) {
                Integer task = tasks.poll(500, TimeUnit.MILLISECONDS); // Взяти завдання з черги з обмеженням часу

                if (task == null) {
                    System.out.println("Зграя " + ID + " закінчила пошук Вінні-Пуха.");
                    break; // Завершуємо роботу бджоли, якщо черга пуста
                }

                if (task == location) {
                    System.out.println("Зграя " + ID + " знайшла Вінні-Пуха на ділянці " + location + " і провела покарання");
                    break; // Завершуємо роботу бджоли, якщо знайдено Вінні-Пуха
                }

                System.out.println("Зграя " + ID + " перевіряє ділянку " + task);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Second {

    public static void main(String[] args) {
        SharedResource sharedResource = new SharedResource();


        Thread t1 = new Thread(new WorkerThread("AAAABBBB", sharedResource));
        Thread t2 = new Thread(new WorkerThread("CCCCDDDD", sharedResource));
        Thread t3 = new Thread(new WorkerThread("ABCDABCD", sharedResource));
        Thread t4 = new Thread(new WorkerThread("DDDDCCCC", sharedResource));

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}

class SharedResource {
    private final Lock lock = new ReentrantLock();


    public boolean checkCondition(String s) {
        lock.lock();
        try {
            long count = s.chars()
                    .mapToObj(c -> (char) c)
                    .filter(ch -> ch == 'A' || ch == 'B')
                    .count();
            return count == s.length() / 2;
        } finally {
            lock.unlock();
        }
    }
}

class WorkerThread implements Runnable {
    private String s;
    private final SharedResource sharedResource;
    private final Random random = new Random();

    public WorkerThread(String s, SharedResource sharedResource) {
        this.s = s;
        this.sharedResource = sharedResource;
    }

    @Override
    public void run() {
        while (!sharedResource.checkCondition(s)) {
            s = modifyString(s);
        }
        System.out.println(Thread.currentThread().getName() + " завершив роботу з рядком: " + s);
    }


    private String modifyString(String s) {
        int idx = random.nextInt(s.length());
        char c = s.charAt(idx);
        if (c == 'A') {
            s = replaceCharAt(s, idx, 'C');
        } else if (c == 'B') {
            s = replaceCharAt(s, idx, 'D');
        } else if (c == 'C') {
            s = replaceCharAt(s, idx, 'A');
        } else if (c == 'D') {
            s = replaceCharAt(s, idx, 'B');
        }
        return s;
    }


    private static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }
}

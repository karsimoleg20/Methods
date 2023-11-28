import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class First {
    static char[] recruits;
    static ReentrantLock lock = new ReentrantLock();
    static CyclicBarrier barrier; 

    public static void main(String[] args) throws Exception {
        int numRecruits = 100;
        int numThreads = 2;
        recruits = new char[numRecruits];


        for (int i = 0; i < numRecruits; i++) {
            recruits[i] = Math.random() > 0.5 ? 'L' : 'R';
        }

        barrier = new CyclicBarrier(numThreads, () -> {

            printRecruits();
            if (isSteadyState()) {
                System.out.println("Досягнуто стабільного стану");
                System.exit(0);
            }
        });

        int recruitsPerThread = numRecruits / numThreads;
        for (int i = 0; i < numThreads; i++) {
            new Thread(new RecruitHandler(i * recruitsPerThread, (i + 1) * recruitsPerThread)).start();
        }
    }

    static class RecruitHandler implements Runnable {
        int start, end;

        RecruitHandler(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    lock.lock();
                    try {
                        for (int i = start; i < end; i++) {

                            if (i > 0 && recruits[i] != recruits[i - 1]) {
                                recruits[i] = (recruits[i] == 'L') ? 'R' : 'L';
                            }
                            if (i < recruits.length - 1 && recruits[i] != recruits[i + 1]) {
                                recruits[i] = (recruits[i] == 'L') ? 'R' : 'L';
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                    barrier.await();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static boolean isSteadyState() {

        for (int i = 1; i < recruits.length; i++) {
            if (recruits[i] != recruits[i - 1]) {
                return false;
            }
        }
        return true;
    }

    static void printRecruits() {

        for (char recruit : recruits) {
            System.out.print(recruit + " ");
        }
        System.out.println();
    }
}

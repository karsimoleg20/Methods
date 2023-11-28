import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

public class SynchronizedSemaphoreThreadsExample {
    private static Semaphore semaphore = new Semaphore(1);
    private static int sharedResource = 0;
    private static JSlider slider;
    private static Thread thread1;
    private static Thread thread2;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Semaphore Threads Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        slider.setPreferredSize(new Dimension(400, 50));
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        panel.add(slider);

        JButton startButton1 = new JButton("ПУСК 1");
        JButton stopButton1 = new JButton("СТОП 1");
        JButton startButton2 = new JButton("ПУСК 2");
        JButton stopButton2 = new JButton("СТОП 2");

        startButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (semaphore.tryAcquire()) {
                    thread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runThread(1);
                        }
                    });
                    thread1.setPriority(Thread.MIN_PRIORITY);
                    thread1.start();
                    startButton1.setEnabled(false);
                    stopButton1.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Зайнято потоком");
                }
            }
        });

        stopButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (thread1 != null && thread1.isAlive()) {
                    thread1.interrupt();
                }
                semaphore.release();
                stopButton1.setEnabled(false);
                startButton1.setEnabled(true);
            }
        });

        startButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (semaphore.tryAcquire()) {
                    thread2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runThread(90);
                        }
                    });
                    thread2.setPriority(Thread.MAX_PRIORITY);
                    thread2.start();
                    startButton2.setEnabled(false);
                    stopButton2.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Зайнято потоком");
                }
            }
        });

        stopButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (thread2 != null && thread2.isAlive()) {
                    thread2.interrupt();
                }
                semaphore.release();
                stopButton2.setEnabled(false);
                startButton2.setEnabled(true);
            }
        });

        panel.add(startButton1);
        panel.add(stopButton1);
        panel.add(startButton2);
        panel.add(stopButton2);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void runThread(int targetPosition) {
        Thread currentThread = Thread.currentThread();

        System.out.println("Потік " + currentThread.getName() + " почав роботу.");

        int currentPosition = sharedResource;

        try {
            while (!Thread.interrupted() && currentPosition != targetPosition) {
                System.out.println("Потік " + currentThread.getName() + " рухає бігунок в позицію " + currentPosition);
                if (currentPosition < targetPosition) {
                    currentPosition++;
                } else {
                    currentPosition--;
                }

                sharedResource = currentPosition;
                slider.setValue(currentPosition);

                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Потік " + currentThread.getName() + " було зупинено.");
        }

        System.out.println("Потік " + currentThread.getName() + " завершив роботу.");
        semaphore.release();
    }
}
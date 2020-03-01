

public class Lesson4 {
    private static int count = 5;
    private final Object mon = new Object();
    private volatile String string = "A";

    public static void main(String[] args) {

        Lesson4 les = new Lesson4();

        Thread thread_1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (les.mon) {
                    for (int i = 0; i < count; i++) {
                        while (les.string != "A") {
                            try {
                                les.mon.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.print(les.string);
                        les.string = "B";
                        les.mon.notifyAll();
                    }

                }
            }
        });

        Thread thread_2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (les.mon) {
                    for (int i = 0; i < count; i++) {
                        while (les.string != "B") {
                            try {
                                les.mon.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.print(les.string);
                        les.string = "C";
                        les.mon.notifyAll();

                    }
                }
            }
        });

        Thread thread_3 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (les.mon) {
                    for (int i = 0; i < count; i++) {
                        while (les.string != "C") {
                            try {
                                les.mon.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        System.out.print(les.string);
                        les.string = "A";
                        les.mon.notifyAll();

                    }
                }
            }
        });


        thread_1.start();
        thread_2.start();
        thread_3.start();
    }


}

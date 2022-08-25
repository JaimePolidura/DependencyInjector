package es.jaimetruman.utils;

public final class Utils {
    public static void runCheckedOrTerminate(CheckedRunnable checkedRunnable) {
        try {
            checkedRunnable.run();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

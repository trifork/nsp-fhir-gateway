package dk.medcom.edelivery;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;

public class Wait {

    public static void sleep(int seconds, String task) {
        try (ProgressBar progressBar = new ProgressBarBuilder().setInitialMax(seconds).setTaskName(task).build()) {
            for (int i = 0; i < seconds; i++) {
                try {
                    Thread.sleep(1_000L);
                    progressBar.step();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
                }
            }
        }
    }

}

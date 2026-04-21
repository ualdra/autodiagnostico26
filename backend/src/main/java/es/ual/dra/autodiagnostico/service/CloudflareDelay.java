import java.util.concurrent.ThreadLocalRandom;

public class CloudflareDelay {

    public static void waitRandomTime() {
        // Generate a random number between 1 and 10 (seconds)
        int randomSeconds = ThreadLocalRandom.current().nextInt(1, 11);
        System.out.println("Waiting for " + randomSeconds + " seconds to avoid Cloudflare 520 error...");

        try {
            // Convert seconds to milliseconds
            Thread.sleep(randomSeconds * 1000L);
        } catch (InterruptedException e) {
            // Restore interrupted state
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted during sleep.");
        }
    }
}
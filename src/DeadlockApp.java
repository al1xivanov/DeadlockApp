// author: Aleksei Ivanov

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockApp extends JFrame {

    private final JTextArea textArea; // text area to display the values
    private boolean deadlockEnabled = false; // flag to enable/disable deadlock

    private static final ReentrantLock firstLock = new ReentrantLock();
    private static final ReentrantLock secondLock = new ReentrantLock();

    /**
     * Setting up the GUI with text area, checkbox and display button.
     */
    public DeadlockApp() {

        setTitle("Deadlock App");
        setSize(1000, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton displayButton = new JButton("Display Values");
        displayButton.addActionListener(e -> displayValues()); // display values method when the button is clicked
        add(displayButton, BorderLayout.SOUTH);

        JButton clearButton = new JButton("Reset All");
        clearButton.addActionListener(e -> {
            try {
                resetApp();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }); // clear the text area
        add(clearButton, BorderLayout.EAST);

        JCheckBox deadlockCheckBox = new JCheckBox("Enable Deadlock", false);
        deadlockCheckBox.addItemListener(e -> deadlockEnabled = deadlockCheckBox.isSelected()); // on/off deadlock
        add(deadlockCheckBox, BorderLayout.NORTH);

    }


    /**
     * Displays the values of the authors and books in a separate thread.
     */
    private void displayValues() {
        Map<String, String> authorsAndBooks = getAuthorsAndBooks();
        Map<String, String> authorsAndFilms = getAuthorsAndFilms();

        String currentThreadName = Thread.currentThread() + ": ";

        Thread firstThread = new Thread(() -> { // create a first thread
            for (Map.Entry<String, String> entry : authorsAndBooks.entrySet()) { // iterate over the authors and books
                String result = currentThreadName + entry.getKey() + " - " + entry.getValue();
                if (deadlockEnabled) {// check if deadlock is enabled
                    firstLock.lock();
                    textArea.append(result + " (deadlock)\n");
                    secondLock.lock();
                    try {
                        Thread.sleep(1500); // sleep for 1 second
                    } catch (RuntimeException | InterruptedException ignored) { // handle exceptions
                    }
                    secondLock.unlock();
                    firstLock.unlock();
                } else {
                    textArea.append(result + "\n");
                    try {
                        Thread.sleep(1500); // sleep for 1 second
                    } catch (RuntimeException | InterruptedException ignored) { // handle exceptions
                    }
                }
            }
        });

        Thread secondThread = new Thread(() -> { // create a second thread
            for (Map.Entry<String, String> entry : authorsAndFilms.entrySet()) { // iterate over the authors and films
                String result = currentThreadName + entry.getKey() + " - " + entry.getValue();
                if (deadlockEnabled) { // check if deadlock is enabled
                    secondLock.lock();
                    textArea.append(result + " (deadlock)\n");
                    firstLock.lock();
                    try {
                        Thread.sleep(1500); // sleep for 1 second
                    } catch (RuntimeException | InterruptedException ignored) { // handle exceptions
                    }
                    firstLock.unlock();
                    secondLock.unlock();
                } else {
                    textArea.append(result + "\n");
                    try {
                        Thread.sleep(1500); // sleep for 1 second
                    } catch (RuntimeException | InterruptedException ignored) { // handle exceptions
                    }
                }
            }
        });

        firstThread.start();
        secondThread.start();
    }


    /**
     * Resets the application.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     */

    private void resetApp() throws InterruptedException {
        // Get the current process ID
        String processId = getProcessId();

        // Print the current process ID
        System.out.println("Current process ID: " + processId);

        // Restart the application
        textArea.setText("Restarting App...");
        System.out.println("Restarting App...");
        Thread.sleep(1500);
        deadlockEnabled = false;
        Thread.getAllStackTraces().keySet().forEach(Thread::interrupt);
        SwingUtilities.invokeLater(this::dispose);

        // Restart the process with the same process ID
        restartProcess();
        killProcess(processId);

    }

    private static String getProcessId() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        return processName.split("@")[0];
    }


    private static void restartProcess() {
        String javaBin = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("java.class.path");
        String className = DeadlockApp.class.getName();

        try {
            ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
            Process process = builder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void killProcess(String processId) {
        try {
            Process process = new ProcessBuilder("kill", processId).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns a map of authors and their corresponding books.
     *
     * @return a map with author names as keys and book titles as values
     */
    private static Map<String, String> getAuthorsAndBooks() {
        Map<String, String> authorsAndBooks = new HashMap<>(); // create a map of authors and books
        authorsAndBooks.put("Leo Tolstoy", "War and Peace");
        authorsAndBooks.put("Jane Austen", "Pride and Prejudice");
        authorsAndBooks.put("Fyodor Dostoevsky", "Crime and Punishment");
        authorsAndBooks.put("Charlotte Bronte", "Jane Eyre");
        authorsAndBooks.put("Mark Twain", "The Adventures of Tom Sawyer");
        authorsAndBooks.put("Agatha Christie", "Murder on the Orient Express");
        authorsAndBooks.put("Ernest Hemingway", "The Old Man and the Sea");
        authorsAndBooks.put("William Shakespeare", "Romeo and Juliet");
        authorsAndBooks.put("J.K. Rowling", "Harry Potter and the Philosopher's Stone");
        authorsAndBooks.put("George Orwell", "1984");
        authorsAndBooks.put("Harper Lee", "To Kill a Mockingbird");
        return authorsAndBooks; // return the map of authors and books
    }

    /**
     * Retrieves a map containing the names of authors as keys and the titles of their films as values.
     *
     * @return a map of authors and their films
     */
    private static Map<String, String> getAuthorsAndFilms() {
        Map<String, String> authorsAndFilms = new HashMap<>(); // create a map of authors and films
        authorsAndFilms.put("Christopher Nolan", "Inception");
        authorsAndFilms.put("Quentin Tarantino", "Pulp Fiction");
        authorsAndFilms.put("Steven Spielberg", "Jurassic Park");
        authorsAndFilms.put("Hayao Miyazaki", "Spirited Away");
        authorsAndFilms.put("Martin Scorsese", "The Departed");
        authorsAndFilms.put("Stanley Kubrick", "The Shining");
        authorsAndFilms.put("Alfred Hitchcock", "Psycho");
        authorsAndFilms.put("Francis Ford Coppola", "The Godfather");
        authorsAndFilms.put("James Cameron", "Avatar");
        authorsAndFilms.put("Tim Burton", "Edward Scissorhands");
        return authorsAndFilms; // return the map of authors and films
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // create a Swing GUI thread
            DeadlockApp app = new DeadlockApp(); // create an instance of the DeadlockApp class
            app.setVisible(true); // show the GUI
        });
    }
}



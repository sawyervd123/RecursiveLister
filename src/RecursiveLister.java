import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveLister extends JFrame {
    private JButton startButton;
    private JButton quitButton;
    private JTextArea fileTextArea;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private JDialog loadingDialog;

    public RecursiveLister() {
        super("Recursive File Lister");
        createComponents();
        layoutComponents();
        registerListeners();
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createComponents() {
        startButton = new JButton("Start");
        quitButton = new JButton("Quit");
        fileTextArea = new JTextArea();
        fileTextArea.setEditable(false);
        scrollPane = new JScrollPane(fileTextArea);
        titleLabel = new JLabel("Recursive File Lister", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(quitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void registerListeners() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(RecursiveLister.this);

                if (option == JFileChooser.APPROVE_OPTION) {
                    fileTextArea.setText("");
                    Path selectedPath = fileChooser.getSelectedFile().toPath();
                    showLoadingDialog();
                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() {
                            listFilesRecursive(selectedPath);
                            return null;
                        }

                        @Override
                        protected void done() {
                            hideLoadingDialog();
                        }
                    };
                    worker.execute();
                }
            }
        });

        quitButton.addActionListener(e -> System.exit(0));
    }

    private void listFilesRecursive(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    fileTextArea.append(file.toString() + "\n");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    fileTextArea.append(dir.toString() + "\n");
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading files: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLoadingDialog() {
        loadingDialog = new JDialog(this, "Loading", true);
        JLabel loadingLabel = new JLabel("Loading files, please wait...", SwingConstants.CENTER);
        loadingDialog.add(loadingLabel);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);
        SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RecursiveLister::new);
    }
}
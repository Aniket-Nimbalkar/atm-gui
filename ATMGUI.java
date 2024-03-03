import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


/*
    The ATM interface program will allow users to perform various banking operations
    such as checking balance, withdrawing money, and depositing money.
    • Users will be prompted to enter their user ID and PIN upon startup for authentication.
    • Upon successful authentication, users will gain access to the ATM functionalities.
    • Error handling mechanisms will be implemented to handle invalid user input,
    insufficient funds, etc.
    • The program will provide informative messages to guide users through the ATM
    interface.
*/

class User {
    private String userID;
    private String userPIN;
    private double accountBalance;

    public User(String userID, String userPIN, double accountBalance) {
        this.userID = userID;
        this.userPIN = userPIN;
        this.accountBalance = accountBalance;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserPIN() {
        return userPIN;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }
}

// adding action listner to the buttons

class ATM extends JFrame implements ActionListener {
    private Map<String, User> users;
    private String currentUserID;
    private JTextField userIDField;
    private JPasswordField pinField;
    private JLabel balanceLabel;
    private final String DATA_FILE = "userdata.txt";

    private JPanel loginPanel;
    private JPanel menuPanel;

    public ATM() {
        super("ATM Machine");
        this.users = new HashMap<>();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 400); // Increased size

        createLoginPanel();
        createMenuPanel();

        add(loginPanel);
        loadUserData(); // Load user data from file on startup
    }

//    different UI for login and menu.
//    after login the menu screen will be displayed.
//    after done menu user will be logged out.
    private void createLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel userLabel = new JLabel("User ID:");
        inputPanel.add(userLabel);

        userIDField = new JTextField();
        inputPanel.add(userIDField);

        JLabel pinLabel = new JLabel("PIN:");
        inputPanel.add(pinLabel);

        pinField = new JPasswordField();
        inputPanel.add(pinField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        inputPanel.add(loginButton);

        loginPanel.add(inputPanel, BorderLayout.CENTER);
    }

    private void createMenuPanel() {
        menuPanel = new JPanel(new BorderLayout());

        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balancePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        balanceLabel = new JLabel("");
        balancePanel.add(balanceLabel);

        JButton checkBalanceButton = new JButton("Check Balance");
        checkBalanceButton.addActionListener(e -> checkBalance(users.get(currentUserID)));
        JButton withdrawButton = new JButton("Withdraw Money");
        withdrawButton.addActionListener(e -> withdrawMoney(users.get(currentUserID)));
        JButton depositButton = new JButton("Deposit Money");
        depositButton.addActionListener(e -> depositMoney(users.get(currentUserID)));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        buttonPanel.add(checkBalanceButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(depositButton);

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(e -> showLoginPanel());

        JPanel donePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        donePanel.add(doneButton);

        menuPanel.add(balancePanel, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        menuPanel.add(donePanel, BorderLayout.SOUTH);
    }

    private void showLoginPanel() {
        remove(menuPanel);
        add(loginPanel);
        revalidate();
        repaint();
    }

    public void addUser(String userID, String userPIN, double initialBalance) {
        users.put(userID, new User(userID, userPIN, initialBalance));
    }

    public void actionPerformed(ActionEvent e) {
        String userID = userIDField.getText();
        String userPIN = new String(pinField.getPassword());

        if (users.containsKey(userID)) {
            User user = users.get(userID);
            if (user.getUserPIN().equals(userPIN)) {
                currentUserID = userID;
                remove(loginPanel);
                add(menuPanel);
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid PIN", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkBalance(User user) {
        JOptionPane.showMessageDialog(this, "Your current balance: $" + user.getAccountBalance(), "Balance", JOptionPane.INFORMATION_MESSAGE);
    }

    private void withdrawMoney(User user) {
        String input = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        try {
            double amount = Double.parseDouble(input);
            if (amount > user.getAccountBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                user.setAccountBalance(user.getAccountBalance() - amount);
                JOptionPane.showMessageDialog(this, "Withdrawal successful. Remaining balance: $" + user.getAccountBalance(), "Success", JOptionPane.INFORMATION_MESSAGE);
                saveUserData();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void depositMoney(User user) {
        String input = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        try {
            double amount = Double.parseDouble(input);
            user.setAccountBalance(user.getAccountBalance() + amount);
            JOptionPane.showMessageDialog(this, "Deposit successful. Updated balance: $" + user.getAccountBalance(), "Success", JOptionPane.INFORMATION_MESSAGE);
            saveUserData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                String userID = userData[0];
                String userPIN = userData[1];
                double accountBalance = Double.parseDouble(userData[2]);
                users.put(userID, new User(userID, userPIN, accountBalance));
            }
        } catch (IOException e) {
            System.out.println("Error loading user data: " + e.getMessage());
        }
    }

    private void saveUserData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (User user : users.values()) {
                writer.write(user.getUserID() + "," + user.getUserPIN() + "," + user.getAccountBalance());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ATM atm = new ATM();

//        adding the user data in a userdata.txt file
//        atm.addUser("ANIKET", "1234", 10000.0);
//        atm.addUser("SAM", "1234", 1000.0);
        atm.setVisible(true);
    }
}

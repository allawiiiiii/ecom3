import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Customer extends JPanel {
    //kund attribut
    private String name;
    private String email;
    private String password;

    //statisk lista för att spara alla registrerade kunder
    private static List<Customer> registeredCustomers = new ArrayList<>();

    //GUI komponenter för registrering
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton registerButton;

    //constructor
    public Customer() {
        buildUI();
    }

    public Customer(String name, String email, String password) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;

        //uppdatera fält
        nameField.setText(name);
        emailField.setText(email);
        passwordField.setText(password);
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        //name text fält
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        nameField = new JTextField(20);
        add(nameField, gbc);

        //email text fält
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        emailField = new JTextField(20);
        add(emailField, gbc);

        //registrera knapp
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registerButton = new JButton("Register");
        add(registerButton, gbc);

        //hanterar registration när knappen trycks
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerCustomer();
            }
        });
    }

    //spara kund efter registrering
    private void registerCustomer() {
        name = nameField.getText().trim();
        email = emailField.getText().trim();
        password = new String(passwordField.getPassword());

        //basic validering
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //kontrollera ifall kund med samma email finns
        if (findCustomerByEmail(email) != null) {
            JOptionPane.showMessageDialog(this, "User with this email already exists.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //lägg till kund i static list
        registeredCustomers.add(this);
        JOptionPane.showMessageDialog(this, "Registration successfu",
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Static method att söka efter kund via email
    public static Customer findCustomerByEmail(String email) {
        for (Customer customer : registeredCustomers) {
            if (customer.email.equals(email)) {
                return customer;
            }
        }
        return null;
    }

    //Getter för kund data
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

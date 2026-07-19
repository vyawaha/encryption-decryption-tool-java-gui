package gui;


import crypto.CryptoUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;



/*
 * EncryptionFrame
 *
 * Responsible for:
 *
 * - Creating application window
 * - Designing GUI components
 * - Handling user interaction
 * - Connecting GUI with CryptoUtils
 *
 */


public class EncryptionFrame extends JFrame {


    // ===============================
    // GUI Components
    // ===============================


    private JTextArea inputArea;

    private JTextArea outputArea;

    private JPasswordField passwordField;


    private JButton encryptButton;

    private JButton decryptButton;

    private JButton copyButton;

    private JButton clearButton;


    private JButton encryptFileButton;

    private JButton decryptFileButton;


    private JLabel statusLabel;



    // ===============================
    // Constructor
    // ===============================


    public EncryptionFrame() {


        setTitle(
                "Encryption Decryption Tool"
        );


        setSize(
                900,
                600
        );


        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );


        setLocationRelativeTo(null);


        createGUI();

    }





    // ===============================
    // Create GUI
    // ===============================


    private void createGUI(){


        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );


        mainPanel.setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );



        // Password Panel

        JPanel passwordPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );


        JLabel passwordLabel =
                new JLabel(
                        "Password:"
                );


        passwordField =
                new JPasswordField(
                        25
                );


        passwordPanel.add(passwordLabel);

        passwordPanel.add(passwordField);



        mainPanel.add(
                passwordPanel,
                BorderLayout.NORTH
        );





        // Text Areas


        JPanel textPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                10,
                                10
                        )
                );



        inputArea =
                new JTextArea();


        inputArea.setLineWrap(true);

        inputArea.setWrapStyleWord(true);




        outputArea =
                new JTextArea();


        outputArea.setLineWrap(true);

        outputArea.setWrapStyleWord(true);




        JScrollPane inputScroll =
                new JScrollPane(
                        inputArea
                );


        JScrollPane outputScroll =
                new JScrollPane(
                        outputArea
                );



        inputScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Input Text"
                )
        );



        outputScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Output Text"
                )
        );



        textPanel.add(inputScroll);

        textPanel.add(outputScroll);



        mainPanel.add(
                textPanel,
                BorderLayout.CENTER
        );





        // Buttons


        JPanel buttonPanel =
                new JPanel();



        encryptButton =
                new JButton(
                        "Encrypt Text"
                );



        decryptButton =
                new JButton(
                        "Decrypt Text"
                );



        copyButton =
                new JButton(
                        "Copy Output"
                );



        clearButton =
                new JButton(
                        "Clear"
                );



        encryptFileButton =
                new JButton(
                        "Encrypt File"
                );



        decryptFileButton =
                new JButton(
                        "Decrypt File"
                );




        buttonPanel.add(encryptButton);

        buttonPanel.add(decryptButton);

        buttonPanel.add(copyButton);

        buttonPanel.add(clearButton);

        buttonPanel.add(encryptFileButton);

        buttonPanel.add(decryptFileButton);



        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );






        // Status Bar


        statusLabel =
                new JLabel(
                        "Ready"
                );



        JPanel statusPanel =
                new JPanel(
                        new BorderLayout()
                );


        statusPanel.setBorder(
                new EmptyBorder(
                        5,
                        5,
                        5,
                        5
                )
        );



        statusPanel.add(
                statusLabel,
                BorderLayout.WEST
        );



        add(
                mainPanel,
                BorderLayout.CENTER
        );


        add(
                statusPanel,
                BorderLayout.SOUTH
        );



        addButtonActions();

    }






    // ===============================
    // Button Events
    // ===============================


    private void addButtonActions(){



        encryptButton.addActionListener(
                e -> encryptText()
        );



        decryptButton.addActionListener(
                e -> decryptText()
        );



        copyButton.addActionListener(
                e -> {


                    outputArea.selectAll();

                    outputArea.copy();


                    statusLabel.setText(
                            "Output copied"
                    );

                }
        );





        clearButton.addActionListener(
                e -> {


                    inputArea.setText("");

                    outputArea.setText("");


                    statusLabel.setText(
                            "Cleared"
                    );

                }
        );




        encryptFileButton.addActionListener(
                e ->
                statusLabel.setText(
                        "File encryption selected"
                )
        );




        decryptFileButton.addActionListener(
                e ->
                statusLabel.setText(
                        "File decryption selected"
                )
        );

    }






    // ===============================
    // Encrypt Text
    // ===============================


    private void encryptText(){


        try {


            String text =
                    inputArea.getText();



            char[] password =
                    passwordField.getPassword();




            if(text.isEmpty()){


                JOptionPane.showMessageDialog(
                        this,
                        "Please enter text"
                );


                return;

            }





            if(password.length == 0){


                JOptionPane.showMessageDialog(
                        this,
                        "Please enter password"
                );


                return;

            }




            String encrypted =
                    CryptoUtils.encrypt(
                            text,
                            password
                    );



            outputArea.setText(
                    encrypted
            );



            statusLabel.setText(
                    "Text encrypted successfully"
            );


        }
        catch(Exception e){


            JOptionPane.showMessageDialog(
                    this,
                    "Encryption failed : "
                    +
                    e.getMessage()
            );


            statusLabel.setText(
                    "Encryption error"
            );

        }


    }








    // ===============================
    // Decrypt Text
    // ===============================


    private void decryptText(){


        try {


            String encryptedText =
                    inputArea.getText();



            char[] password =
                    passwordField.getPassword();




            if(encryptedText.isEmpty()){


                JOptionPane.showMessageDialog(
                        this,
                        "Please enter encrypted text"
                );


                return;

            }





            if(password.length == 0){


                JOptionPane.showMessageDialog(
                        this,
                        "Please enter password"
                );


                return;

            }





            String decrypted =
                    CryptoUtils.decrypt(
                            encryptedText,
                            password
                    );




            outputArea.setText(
                    decrypted
            );



            statusLabel.setText(
                    "Text decrypted successfully"
            );



        }
        catch(Exception e){



            JOptionPane.showMessageDialog(
                    this,
                    "Wrong password or corrupted encrypted data"
            );


            statusLabel.setText(
                    "Decryption failed"
            );


        }


    }



}
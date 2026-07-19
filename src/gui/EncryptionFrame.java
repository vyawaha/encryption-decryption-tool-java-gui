package gui;


import crypto.CryptoUtils;
import service.FileEncryptionService;


import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.nio.file.Path;



public class EncryptionFrame extends JFrame {



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






    public EncryptionFrame(){


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



        JPanel passwordPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );



        passwordPanel.add(
                new JLabel("Password:")
        );


        passwordField =
                new JPasswordField(
                        25
                );


        passwordPanel.add(
                passwordField
        );



        mainPanel.add(
                passwordPanel,
                BorderLayout.NORTH
        );






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



        textPanel.add(
                inputScroll
        );


        textPanel.add(
                outputScroll
        );



        mainPanel.add(
                textPanel,
                BorderLayout.CENTER
        );







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







    private void addButtonActions(){



        encryptButton.addActionListener(
                e -> encryptText()
        );



        decryptButton.addActionListener(
                e -> decryptText()
        );



        encryptFileButton.addActionListener(
                e -> encryptFile()
        );



        decryptFileButton.addActionListener(
                e -> decryptFile()
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


    }









    private void encryptText(){


        try{


            String text =
                    inputArea.getText();


            char[] password =
                    passwordField.getPassword();



            if(text.isBlank())
            {

                throw new Exception(
                        "Enter text"
                );

            }



            if(password.length==0)
            {

                throw new Exception(
                        "Enter password"
                );

            }




            outputArea.setText(
                    CryptoUtils.encrypt(
                            text,
                            password
                    )
            );


            statusLabel.setText(
                    "Text encrypted"
            );


        }
        catch(Exception e)
        {

            showError(
                    e.getMessage()
            );

        }


    }







    private void decryptText(){


        try{


            char[] password =
                    passwordField.getPassword();



            outputArea.setText(
                    CryptoUtils.decrypt(
                            inputArea.getText(),
                            password
                    )
            );



            statusLabel.setText(
                    "Text decrypted"
            );


        }
        catch(Exception e)
        {

            showError(
                    "Wrong password or corrupted data"
            );

        }


    }









    private void encryptFile(){


        try{


            JFileChooser chooser =
                    new JFileChooser();



            chooser.setDialogTitle(
                    "Select File To Encrypt"
            );



            if(
                chooser.showOpenDialog(this)
                !=
                JFileChooser.APPROVE_OPTION
            )
            {
                return;
            }



            Path input =
                    chooser
                    .getSelectedFile()
                    .toPath();



            Path output =
                    input.resolveSibling(
                            input.getFileName()
                            +
                            ".enc"
                    );



            char[] password =
                    passwordField.getPassword();



            FileEncryptionService.encryptFile(
                    input,
                    output,
                    password
            );



            statusLabel.setText(
                    "File encrypted"
            );


            JOptionPane.showMessageDialog(
                    this,
                    "Created:\n"
                    +
                    output
            );


        }
        catch(Exception e)
        {

            showError(
                    e.getMessage()
            );

        }


    }









    private void decryptFile(){


        try{


            JFileChooser chooser =
                    new JFileChooser();



            chooser.setDialogTitle(
                    "Select Encrypted File"
            );



            if(
                chooser.showOpenDialog(this)
                !=
                JFileChooser.APPROVE_OPTION
            )
            {
                return;
            }



            Path input =
                    chooser
                    .getSelectedFile()
                    .toPath();




            String name =
                    input.getFileName()
                    .toString();



            String outputName;



            if(name.endsWith(".enc"))
            {

                outputName =
                        name.substring(
                                0,
                                name.length()-4
                        )
                        +
                        ".dec";

            }
            else
            {

                outputName =
                        name
                        +
                        ".dec";

            }



            Path output =
                    input.resolveSibling(
                            outputName
                    );



            char[] password =
                    passwordField.getPassword();




            FileEncryptionService.decryptFile(
                    input,
                    output,
                    password
            );



            statusLabel.setText(
                    "File decrypted"
            );


            JOptionPane.showMessageDialog(
                    this,
                    "Created:\n"
                    +
                    output
            );


        }
        catch(Exception e)
        {

            showError(
                    "Wrong password or corrupted encrypted file"
            );

        }


    }








    private void showError(
            String message
    )
    {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );


        statusLabel.setText(
                "Operation failed"
        );

    }




}
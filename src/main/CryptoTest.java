package main;


import crypto.CryptoUtils;


public class CryptoTest {


    public static void main(String[] args)
            throws Exception {


        String message =
                "Hello Encryption Tool";


        char[] encryptionPassword =
                "Secure123".toCharArray();


        char[] wrongPassword =
                "WrongPassword".toCharArray();



        // Encryption

        String encrypted =
                CryptoUtils.encrypt(
                        message,
                        encryptionPassword
                );


        System.out.println("Encrypted:");
        System.out.println(encrypted);



        // Decryption using wrong password

        try {


            String decrypted =
                    CryptoUtils.decrypt(
                            encrypted,
                            wrongPassword
                    );


            System.out.println(
                    "\nDecrypted:"
            );


            System.out.println(
                    decrypted
            );


        }
        catch(Exception e){


            System.out.println(
                    "\nAuthentication Failed"
            );


            System.out.println(
                    e.getMessage()
            );


        }


    }

}
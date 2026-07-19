package service;


import crypto.CryptoUtils;


import java.nio.file.Files;
import java.nio.file.Path;



public class FileEncryptionService {



    /*
     * Encrypt File
     */


    public static void encryptFile(
            Path inputFile,
            Path outputFile,
            char[] password
    )
            throws Exception {



        validateFile(inputFile);



        byte[] fileData =
                Files.readAllBytes(
                        inputFile
                );



        String encrypted =
                CryptoUtils.encrypt(
                        new String(fileData),
                        password
                );



        Files.writeString(
                outputFile,
                encrypted
        );



    }





    /*
     * Decrypt File
     */


    public static void decryptFile(
            Path inputFile,
            Path outputFile,
            char[] password
    )
            throws Exception {



        validateFile(inputFile);



        String encrypted =
                Files.readString(
                        inputFile
                );



        String decrypted =
                CryptoUtils.decrypt(
                        encrypted,
                        password
                );



        Files.writeString(
                outputFile,
                decrypted
        );



    }







    /*
     * File Validation
     */


    private static void validateFile(
            Path file
    )
            throws Exception {



        if(file == null){


            throw new Exception(
                    "File not selected"
            );


        }




        if(!Files.exists(file)){


            throw new Exception(
                    "File does not exist"
            );


        }


    }


}
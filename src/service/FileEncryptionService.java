package service;


import crypto.CryptoUtils;


import java.nio.file.Files;
import java.nio.file.Path;



public class FileEncryptionService {



    /*
     * Encrypt Any File
     *
     * Supports:
     *
     * PDF
     * JPG
     * PNG
     * ZIP
     * DOCX
     * EXE
     *
     */


    public static void encryptFile(
            Path inputFile,
            Path outputFile,
            char[] password
    )
            throws Exception {



        validateFile(inputFile);



        byte[] fileBytes =
                Files.readAllBytes(
                        inputFile
                );



        byte[] encryptedBytes =
                CryptoUtils.encryptBytes(
                        fileBytes,
                        password
                );



        Files.write(
                outputFile,
                encryptedBytes
        );



    }







    /*
     * Decrypt Any File
     */


    public static void decryptFile(
            Path inputFile,
            Path outputFile,
            char[] password
    )
            throws Exception {



        validateFile(inputFile);




        byte[] encryptedBytes =
                Files.readAllBytes(
                        inputFile
                );



        byte[] decryptedBytes =
                CryptoUtils.decryptBytes(
                        encryptedBytes,
                        password
                );



        Files.write(
                outputFile,
                decryptedBytes
        );



    }








    /*
     * Validate File
     */


    private static void validateFile(
            Path file
    )
            throws Exception {



        if(file == null)
        {

            throw new Exception(
                    "No file selected"
            );

        }



        if(!Files.exists(file))
        {

            throw new Exception(
                    "File does not exist"
            );

        }



        if(!Files.isRegularFile(file))
        {

            throw new Exception(
                    "Invalid file"
            );

        }


    }



}
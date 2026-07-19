package service;


import crypto.CryptoException;
import crypto.CryptoUtils;


import java.nio.file.Files;
import java.nio.file.Path;



/*
 * FileEncryptionService
 *
 * Responsibilities:
 *
 * - Read binary files
 * - Encrypt files using CryptoUtils
 * - Save encrypted container files
 * - Decrypt encrypted files
 * - Restore original files
 *
 */


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



        if(!Files.exists(inputFile)){


            throw new Exception(
                    "Input file does not exist"
            );


        }




        byte[] data =
                Files.readAllBytes(
                        inputFile
                );





        byte[] encrypted =
                CryptoUtils.encryptBytes(
                        data,
                        inputFile.getFileName().toString(),
                        password
                );





        Files.write(
                outputFile,
                encrypted
        );


    }









    /*
     * Decrypt File
     */


    public static void decryptFile(
            Path encryptedFile,
            Path outputFile,
            char[] password
    )
            throws Exception {



        if(!Files.exists(encryptedFile)){


            throw new Exception(
                    "Encrypted file does not exist"
            );


        }






        byte[] encryptedData =
                Files.readAllBytes(
                        encryptedFile
                );








        CryptoUtils.DecryptedData decrypted =
                CryptoUtils.decryptBytes(
                        encryptedData,
                        password
                );







        Files.write(
                outputFile,
                decrypted.getData()
        );



    }



}
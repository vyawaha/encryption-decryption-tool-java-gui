package crypto;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;


import java.util.Base64;




public class CryptoUtils {



    /*
     * Security Parameters
     */


    private static final int SALT_LENGTH = 16;

    private static final int IV_LENGTH = 12;

    private static final int KEY_LENGTH = 256;

    private static final int ITERATIONS = 120000;

    private static final int TAG_LENGTH = 128;



    private static final SecureRandom RANDOM =
            new SecureRandom();



    private static final byte[] MAGIC =
            {
                    'E',
                    'N',
                    'C',
                    'R'
            };


    private static final byte VERSION = 1;





    /*
     * Generate AES Key From Password
     */


    private static SecretKey generateKey(
            char[] password,
            byte[] salt
    )
            throws GeneralSecurityException {



        PBEKeySpec spec =
                new PBEKeySpec(
                        password,
                        salt,
                        ITERATIONS,
                        KEY_LENGTH
                );



        SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                        "PBKDF2WithHmacSHA256"
                );



        byte[] keyBytes =
                factory
                .generateSecret(spec)
                .getEncoded();



        return new SecretKeySpec(
                keyBytes,
                "AES"
        );


    }







    /*
     * Encrypt Text
     */


    public static String encrypt(
            String plainText,
            char[] password
    )
            throws GeneralSecurityException {



        byte[] encrypted =
                encryptBytes(
                        plainText.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        password
                );



        return Base64
                .getEncoder()
                .encodeToString(
                        encrypted
                );


    }







    /*
     * Decrypt Text
     */


    public static String decrypt(
            String encryptedText,
            char[] password
    )
            throws GeneralSecurityException {



        byte[] decrypted =
                decryptBytes(
                        Base64
                        .getDecoder()
                        .decode(
                                encryptedText
                        ),
                        password
                );



        return new String(
                decrypted,
                StandardCharsets.UTF_8
        );


    }









    /*
     * Encrypt Binary Data
     *
     * Used for:
     *
     * PDF
     * JPG
     * PNG
     * ZIP
     * DOCX
     * EXE
     *
     */


    public static byte[] encryptBytes(
            byte[] data,
            char[] password
    )
            throws GeneralSecurityException {



        byte[] salt =
                new byte[SALT_LENGTH];


        byte[] iv =
                new byte[IV_LENGTH];



        RANDOM.nextBytes(salt);

        RANDOM.nextBytes(iv);




        SecretKey key =
                generateKey(
                        password,
                        salt
                );




        Cipher cipher =
                Cipher.getInstance(
                        "AES/GCM/NoPadding"
                );




        cipher.init(
                Cipher.ENCRYPT_MODE,
                key,
                new GCMParameterSpec(
                        TAG_LENGTH,
                        iv
                )
        );



        byte[] encrypted =
                cipher.doFinal(
                        data
                );






        ByteBuffer buffer =
                ByteBuffer.allocate(
                        MAGIC.length
                        +
                        1
                        +
                        salt.length
                        +
                        iv.length
                        +
                        encrypted.length
                );



        buffer.put(MAGIC);

        buffer.put(VERSION);

        buffer.put(salt);

        buffer.put(iv);

        buffer.put(encrypted);



        return buffer.array();


    }









    /*
     * Decrypt Binary Data
     */


    public static byte[] decryptBytes(
            byte[] encryptedData,
            char[] password
    )
            throws GeneralSecurityException {



        ByteBuffer buffer =
                ByteBuffer.wrap(
                        encryptedData
                );



        byte[] magic =
                new byte[4];



        buffer.get(magic);




        if(
                magic[0] != 'E'
                ||
                magic[1] != 'N'
                ||
                magic[2] != 'C'
                ||
                magic[3] != 'R'
        )
        {

            throw new GeneralSecurityException(
                    "Invalid encrypted file format"
            );

        }





        byte version =
                buffer.get();



        if(version != VERSION)
        {

            throw new GeneralSecurityException(
                    "Unsupported encryption version"
            );

        }






        byte[] salt =
                new byte[SALT_LENGTH];


        buffer.get(salt);





        byte[] iv =
                new byte[IV_LENGTH];


        buffer.get(iv);






        byte[] cipherText =
                new byte[
                        buffer.remaining()
                ];



        buffer.get(cipherText);







        SecretKey key =
                generateKey(
                        password,
                        salt
                );







        Cipher cipher =
                Cipher.getInstance(
                        "AES/GCM/NoPadding"
                );





        cipher.init(
                Cipher.DECRYPT_MODE,
                key,
                new GCMParameterSpec(
                        TAG_LENGTH,
                        iv
                )
        );





        return cipher.doFinal(
                cipherText
        );


    }




}
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
     * Generate AES key from password
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
     * Encrypt text
     */


    public static String encrypt(
            String plainText,
            char[] password
    )
            throws GeneralSecurityException {



        byte[] plaintextBytes =
                plainText.getBytes(
                        StandardCharsets.UTF_8
                );



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



        GCMParameterSpec spec =
                new GCMParameterSpec(
                        TAG_LENGTH,
                        iv
                );



        cipher.init(
                Cipher.ENCRYPT_MODE,
                key,
                spec
        );



        byte[] encrypted =
                cipher.doFinal(
                        plaintextBytes
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



        return Base64
                .getEncoder()
                .encodeToString(
                        buffer.array()
                );

    }






    /*
     * Decrypt text
     */


    public static String decrypt(
            String encryptedText,
            char[] password
    )
            throws GeneralSecurityException {



        byte[] data =
                Base64
                .getDecoder()
                .decode(
                        encryptedText
                );



        ByteBuffer buffer =
                ByteBuffer.wrap(
                        data
                );



        byte[] magic =
                new byte[4];


        buffer.get(magic);



        if(
            magic[0]!='E'
            ||
            magic[1]!='N'
            ||
            magic[2]!='C'
            ||
            magic[3]!='R'
        ){

            throw new GeneralSecurityException(
                    "Invalid encrypted data"
            );

        }



        buffer.get();



        byte[] salt =
                new byte[SALT_LENGTH];


        buffer.get(salt);



        byte[] iv =
                new byte[IV_LENGTH];


        buffer.get(iv);



        byte[] encrypted =
                new byte[buffer.remaining()];


        buffer.get(encrypted);




        SecretKey key =
                generateKey(
                        password,
                        salt
                );




        Cipher cipher =
                Cipher.getInstance(
                        "AES/GCM/NoPadding"
                );



        GCMParameterSpec spec =
                new GCMParameterSpec(
                        TAG_LENGTH,
                        iv
                );



        cipher.init(
                Cipher.DECRYPT_MODE,
                key,
                spec
        );



        byte[] decrypted =
                cipher.doFinal(
                        encrypted
                );



        return new String(
                decrypted,
                StandardCharsets.UTF_8
        );


    }




}
package crypto;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


import java.io.ByteArrayOutputStream;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.SecureRandom;

import java.util.Arrays;
import java.util.Base64;



public class CryptoUtils {


    private static final int SALT_LENGTH = 16;

    private static final int IV_LENGTH = 12;

    private static final int KEY_LENGTH = 256;

    private static final int ITERATIONS = 120000;

    private static final int TAG_LENGTH = 128;


    private static final byte VERSION = 1;


    private static final byte[] MAGIC =
            {
                    'E',
                    'N',
                    'C',
                    'R'
            };


    private static final SecureRandom RANDOM =
            new SecureRandom();



    /*
        Generate AES Key using PBKDF2
     */

    private static SecretKey generateKey(
            char[] password,
            byte[] salt
    )
            throws Exception {


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
                factory.generateSecret(spec)
                        .getEncoded();



        return new SecretKeySpec(
                keyBytes,
                "AES"
        );

    }





    /*
        Encrypt Binary Data

        Supports:
        PDF
        JPG
        PNG
        ZIP
        EXE
        DOCX
     */


    public static byte[] encryptBytes(
            byte[] data,
            String fileName,
            char[] password
    )
            throws CryptoException {


        try {


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
                    cipher.doFinal(data);



            byte[] hash =
                    sha256(data);



            byte[] nameBytes =
                    fileName.getBytes(
                            StandardCharsets.UTF_8
                    );



            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();



            output.write(MAGIC);

            output.write(VERSION);


            writeInt(
                    output,
                    nameBytes.length
            );


            output.write(nameBytes);



            writeLong(
                    output,
                    data.length
            );


            writeInt(
                    output,
                    hash.length
            );


            output.write(hash);


            output.write(salt);

            output.write(iv);

            output.write(encrypted);



            return output.toByteArray();



        }
        catch(Exception e){


            throw new CryptoException(
                    "Encryption failed",
                    e
            );

        }

    }






    /*
        Decrypt Binary Data
     */


    public static DecryptedData decryptBytes(
            byte[] encryptedFile,
            char[] password
    )
            throws CryptoException {



        try {


            ByteBuffer buffer =
                    ByteBuffer.wrap(
                            encryptedFile
                    );



            byte[] magic =
                    new byte[4];


            buffer.get(magic);



            if(!Arrays.equals(
                    magic,
                    MAGIC
            )){


                throw new CryptoException(
                        "Invalid encrypted file format"
                );

            }




            byte version =
                    buffer.get();



            if(version != VERSION){


                throw new CryptoException(
                        "Unsupported file version"
                );

            }




            int nameLength =
                    buffer.getInt();



            byte[] nameBytes =
                    new byte[nameLength];


            buffer.get(nameBytes);



            String fileName =
                    new String(
                            nameBytes,
                            StandardCharsets.UTF_8
                    );




            long originalSize =
                    buffer.getLong();




            int hashLength =
                    buffer.getInt();



            byte[] storedHash =
                    new byte[hashLength];



            buffer.get(storedHash);




            byte[] salt =
                    new byte[SALT_LENGTH];


            buffer.get(salt);



            byte[] iv =
                    new byte[IV_LENGTH];


            buffer.get(iv);




            byte[] cipherText =
                    new byte[buffer.remaining()];


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



            byte[] decrypted;



            try {


                decrypted =
                        cipher.doFinal(
                                cipherText
                        );


            }
            catch(Exception e){


                throw new CryptoException(
                        "Authentication failed: Wrong password"
                );

            }




            byte[] calculatedHash =
                    sha256(
                            decrypted
                    );



            if(!Arrays.equals(
                    storedHash,
                    calculatedHash
            )){


                throw new CryptoException(
                        "Integrity verification failed"
                );

            }




            return new DecryptedData(
                    fileName,
                    decrypted,
                    originalSize
            );



        }
        catch(CryptoException e){

            throw e;

        }
        catch(Exception e){


            throw new CryptoException(
                    "Decryption failed",
                    e
            );

        }

    }






    /*
        Text Encryption Wrapper
     */


    public static String encrypt(
            String text,
            char[] password
    )
            throws CryptoException {


        byte[] encrypted =
                encryptBytes(
                        text.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        "text.txt",
                        password
                );


        return Base64
                .getEncoder()
                .encodeToString(
                        encrypted
                );

    }






    /*
        Text Decryption Wrapper
     */


    public static String decrypt(
            String encryptedText,
            char[] password
    )
            throws CryptoException {


        try {


            byte[] data =
                    Base64
                    .getDecoder()
                    .decode(
                            encryptedText
                    );



            DecryptedData result =
                    decryptBytes(
                            data,
                            password
                    );



            return new String(
                    result.getData(),
                    StandardCharsets.UTF_8
            );


        }
        catch(Exception e){


            throw new CryptoException(
                    "Authentication failed: Wrong password or corrupted data",
                    e
            );

        }

    }






    private static byte[] sha256(
            byte[] data
    )
            throws Exception {


        MessageDigest digest =
                MessageDigest.getInstance(
                        "SHA-256"
                );


        return digest.digest(
                data
        );

    }





    private static void writeInt(
            ByteArrayOutputStream out,
            int value
    ){


        out.write(
                ByteBuffer.allocate(4)
                        .putInt(value)
                        .array(),
                0,
                4
        );

    }






    private static void writeLong(
            ByteArrayOutputStream out,
            long value
    ){


        out.write(
                ByteBuffer.allocate(8)
                        .putLong(value)
                        .array(),
                0,
                8
        );

    }





    /*
        Decrypted Container
     */


    public static class DecryptedData {


        private final String fileName;

        private final byte[] data;

        private final long originalSize;



        public DecryptedData(
                String fileName,
                byte[] data,
                long originalSize
        ){

            this.fileName = fileName;

            this.data = data;

            this.originalSize = originalSize;

        }




        public String getFileName(){

            return fileName;

        }



        public byte[] getData(){

            return data;

        }



        public long getOriginalSize(){

            return originalSize;

        }


    }


}
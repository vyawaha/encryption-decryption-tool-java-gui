package crypto;


/*
 * CryptoException
 *
 * Custom exception for encryption/decryption failures.
 *
 * Handles:
 *
 * - Authentication failures
 * - Invalid encrypted files
 * - Integrity verification failures
 * - Crypto operation errors
 *
 */


public class CryptoException extends Exception {


    public CryptoException(
            String message
    ) {

        super(message);

    }



    public CryptoException(
            String message,
            Throwable cause
    ) {

        super(
                message,
                cause
        );

    }


}
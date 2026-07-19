package utils;


import java.io.IOException;
import java.util.logging.*;



public class AppLogger {


    private static final Logger logger =
            Logger.getLogger(
                    "EncryptionToolLogger"
            );



    static {


        try {


            FileHandler fileHandler =
                    new FileHandler(
                            "encryption-tool.log",
                            true
                    );


            fileHandler.setFormatter(
                    new SimpleFormatter()
            );



            logger.addHandler(
                    fileHandler
            );



            logger.setUseParentHandlers(
                    false
            );



        }
        catch(IOException e){


            e.printStackTrace();

        }


    }




    public static Logger getLogger(){

        return logger;

    }



    public static void info(
            String message
    ){

        logger.info(message);

    }





    public static void warning(
            String message
    ){

        logger.warning(message);

    }





    public static void error(
            String message,
            Exception e
    ){


        logger.log(
                Level.SEVERE,
                message,
                e
        );


    }



}
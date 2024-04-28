package org.example.idsclientapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class to launch the application.
 */
public class Main extends Application
{

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        LoginController lc = fxmlLoader.getController();
        lc.initialize();

        stage.setTitle("Klientská aplikace pro správu světa kouzelníků");
        stage.setScene(scene);

        stage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        launch();

        // Add a shutdown hook to ensure stop() method is called even if the user closes the window directly
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            System.out.println("");
        }));
    }

    @Override
    public void stop() throws Exception
    {
        DatabaseController.closeConnection();
    }
}
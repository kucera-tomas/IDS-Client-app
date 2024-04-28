package org.example.idsclientapp;

import java.io.IOException;
import java.sql.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller class for managing the login view.
 */
public class LoginController
{

    @FXML
    private ImageView db_reset;

    @FXML
    private ImageView help;

    @FXML
    private TextField input_runove_jmeno;

    @FXML
    private ImageView login_image;

    @FXML
    private ImageView pozadi;

    @FXML
    private Text text_runove_jmena;

    @FXML
    private ImageView image_help_background;

    /**
     * Displays help information on mouse click.
     *
     * @param event The MouseEvent triggering the event.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void displayHelp(MouseEvent event) throws SQLException
    {
        boolean isVisible = !text_runove_jmena.isVisible();
        text_runove_jmena.setVisible(isVisible);
        image_help_background.setVisible(isVisible);
        if (isVisible)
        {
            String sql = "SELECT runove_jmeno, specializace FROM KOUZELNIK";

            ResultSet rs = DatabaseController.query(sql, new String[]{});

            StringBuffer ministr = new StringBuffer("");
            StringBuffer kouzelnik = new StringBuffer("");
            StringBuffer spravce = new StringBuffer("");

            while (rs.next())
            {
                String jmeno = rs.getString("runove_jmeno");
                String specializace =rs.getString("specializace");

                String value = "  " + jmeno + "\n";
                if(specializace == null)
                {
                    kouzelnik.append(value);
                }

                else if(specializace.equals("Ministr"))
                {
                    ministr.append(value);
                }

                else if (specializace.equals("Správce"))
                {
                    spravce.append(value);
                }
            }

            text_runove_jmena.setText("Kouzelníci:\n" + kouzelnik + "Ministři:\n" + ministr + "Správci:\n" + spravce);
        }
    }

    /**
     * Initializes the login view.
     */
    public void initialize()
    {
        pozadi.setImage(new Image(getClass().getResourceAsStream("background.png")));
    }

    /**
     * Handles the login action.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void login(MouseEvent event) throws IOException, SQLException
    {
        String runove_jmeno = input_runove_jmeno.getText();

        // Load the new scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("kouzelnik.fxml"));
        Parent root = loader.load();


        KouzelnikController kc = loader.getController();
        try
        {
            kc.initialize(runove_jmeno);
        }
        catch(SQLDataException e)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Chybné runové jméno");
            alert.setHeaderText(null);
            alert.setContentText("Runové jméno " + runove_jmeno + " nebylo nalezeno v databázi.");
            alert.showAndWait();
            return;
        }

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Get the stage from the event
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Resets the database.
     *
     * @param event The MouseEvent triggering the event.
     */
    @FXML
    void resetDatabase(MouseEvent event)
    {
        String[] sql_statements =
        {
            "DELETE FROM STOPA",
            "DELETE FROM VLASTNICTVI",

            "INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty, datum_ztraty) VALUES (TO_DATE('2024-03-16', 'YYYY-MM-DD'), 'AML_0001', 'harry_potter', 'Dar od přítele Ron Weasleyho', 'Prodej', TO_DATE('2024-04-18', 'YYYY-MM-DD'))",
            "INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty) VALUES (TO_DATE('2024-04-18', 'YYYY-MM-DD'), 'AML_0001', 'hermiona_grangerova', 'Nákup', NULL)",
            "INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty) VALUES (TO_DATE('2023-05-21', 'YYYY-MM-DD'), 'HLK_3000', 'harry_potter', 'Nákup v obchodě s kouzelnickými potřebami', NULL)",
            "INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty) VALUES (TO_DATE('2022-12-01', 'YYYY-MM-DD'), 'KST_0024', 'albus_dumbledore', 'Vlastní výroba', NULL)",
            "INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty) VALUES (TO_DATE('2023-08-10', 'YYYY-MM-DD'), 'KNH_1000', 'hermiona_grangerova', 'Dar od profesora Albusa Dumbledorea', NULL)",

            "INSERT INTO STOPA (datum_vyvolani, id_detektoru, id_stopy, runove_jmeno, runovy_kod) VALUES (TO_DATE('2024-01-05', 'YYYY-MM-DD'), 1, 1, 'albus_dumbledore', 'AML_0001')",
            "INSERT INTO STOPA (datum_vyvolani, id_detektoru, id_stopy, runove_jmeno, runovy_kod) VALUES (TO_DATE('2024-02-15', 'YYYY-MM-DD'), 1, 2, 'albus_dumbledore', 'AML_0001')",
            "INSERT INTO STOPA (datum_vyvolani, id_detektoru, id_stopy, runove_jmeno, runovy_kod)  VALUES (TO_DATE('2024-03-10', 'YYYY-MM-DD'), 3, 4, 'albus_dumbledore', 'HLK_3000')"
        };

        for(String sql : sql_statements)
        {
            DatabaseController.query(sql, new String[]{});
        }


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Obnova databáze");
        alert.setHeaderText(null);
        alert.setContentText("Databáze byla úspěšně obnovena do původního stavu.");
        alert.showAndWait();
    }

    /**
     * Removes the hover effect from the button.
     *
     * @param event The MouseEvent triggering the event.
     */
    @FXML
    void button_off_hover(MouseEvent event)
    {
        ImageView imageView = (ImageView) event.getSource();
        imageView.setEffect(null);
    }

    /**
     * Applies a hover effect to the button.
     *
     * @param event The MouseEvent triggering the event.
     */
    @FXML
    void button_on_hover(MouseEvent event)
    {
        ImageView imageView = (ImageView) event.getSource();

        Bloom bloom = new Bloom();
        bloom.setThreshold(0.09); // Adjust the threshold as needed
        imageView.setEffect(bloom);
    }
}

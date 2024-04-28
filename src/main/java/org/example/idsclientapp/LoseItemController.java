package org.example.idsclientapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Controller class for managing the lose item view.
 */
public class LoseItemController
{

    @FXML
    private TextField input_runove_jmeno;

    @FXML
    private ChoiceBox<String> option_kouzelnik;

    @FXML
    private ImageView image_zmena;

    @FXML
    private Text text_zpusob_ztraty;

    private String runove_jmeno;
    private String runovy_kod;

    private String obcanske_jmeno;

    private String stupen;

    /**
     * Handles the lose item action.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void lose_item(MouseEvent event) throws IOException, SQLException
    {
        String sql = """
                UPDATE VLASTNICTVI SET zpusob_ztraty = ?, datum_ztraty = SYSDATE
                WHERE zpusob_ztraty IS NULL AND
                    runovy_kod = ?""";

        String zpusob_ztraty = input_runove_jmeno.getText().toString().strip();

        if (zpusob_ztraty == null)
        {
            zpusob_ztraty = "Dobrovolné vzdání se předmětu";
        }

        String novy_vlastnik = option_kouzelnik.getValue().toString().strip();

        DatabaseController.query(sql, new String[]{zpusob_ztraty, runovy_kod});

        sql = """
                INSERT INTO VLASTNICTVI(datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani) 
                VALUES(SYSDATE, ?, (SELECT runove_jmeno FROM KOUZELNIK WHERE obcanske_jmeno = ?), 'Předání předmětu.')
                """;

        DatabaseController.query(sql, new String[]{runovy_kod, novy_vlastnik});

        if (Integer.parseInt(stupen) >= 5)
        {
            sql = "SELECT id_urovne FROM KOUZELNIK WHERE obcanske_jmeno = ?";
            ResultSet rs = DatabaseController.query(sql, new String[]{novy_vlastnik});

            rs.next();

            if (rs.getInt("id_urovne") <= 9)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Varování");
                alert.setHeaderText(null);
                alert.setContentText("Předmět " + runovy_kod + " byl předán kouzelníkovi s nedostatečnou úrovní.");
                alert.showAndWait();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Předání předmětu");
                alert.setHeaderText(null);
                alert.setContentText("Předmět " + runovy_kod + " byl úspěšně předán.");
                alert.showAndWait();
            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Předání předmětu");
            alert.setHeaderText(null);
            alert.setContentText("Předmět " + runovy_kod + " byl úspěšně předán.");
            alert.showAndWait();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_list.fxml"));
        Parent root = loader.load();

        ItemListController controller = loader.getController();
        controller.initialize(runove_jmeno, obcanske_jmeno);

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Get the stage from the event
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();


    }

    /**
     * Initializes the lose item view.
     *
     * @param runove_jmeno    The runove_jmeno parameter.
     * @param runovy_kod      The runovy_kod parameter.
     * @param obcanske_jmeno  The obcanske_jmeno parameter.
     * @param nazev_predmetu  The nazev_predmetu parameter.
     * @throws SQLException If an SQL exception occurs.
     */
    public void initialize(String runove_jmeno, String runovy_kod, String obcanske_jmeno, String nazev_predmetu, String stupen) throws SQLException
    {
        this.runove_jmeno = runove_jmeno;
        this.runovy_kod = runovy_kod;
        this.obcanske_jmeno = obcanske_jmeno;
        this.stupen = stupen;

        text_zpusob_ztraty.setText("Způsob ztráty předmětu " + nazev_predmetu);

        String sql = "SELECT obcanske_jmeno FROM KOUZELNIK ORDER BY obcanske_jmeno ASC";
        ResultSet rs = DatabaseController.query(sql, new String[]{});

        ObservableList<String> items = FXCollections.observableArrayList();

        // Add null as a default option
        items.add(null);

        // Extract values from ResultSet and add them to the list
        while (rs.next())
        {
            items.add(rs.getString("obcanske_jmeno"));
        }

        // Set items to the ChoiceBox
        option_kouzelnik.setItems(items);

        // Set null as the default value
        option_kouzelnik.setValue(null);
    }

    /**
     * Handles the back button action.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void button_back(MouseEvent event) throws IOException, SQLException
    {
        // Load the new scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_list.fxml"));
        Parent root = loader.load();

        ItemListController controller = loader.getController();
        controller.initialize(runove_jmeno, obcanske_jmeno);

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Get the stage from the event
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
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
        bloom.setThreshold(0.09);
        imageView.setEffect(bloom);
    }
}

package org.example.idsclientapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemComplexViewController
{
    @FXML
    private ImageView image_historie;

    @FXML
    private ImageView image_predmet;

    @FXML
    private ImageView image_zmena;

    @FXML
    private HBox item_hbox;

    @FXML
    private Label text_nazev_predmetu;

    @FXML
    private Label text_nazev_vlastnika;

    @FXML
    private Label text_stupen;

    @FXML
    private Label text_typ_predmetu;

    @FXML
    private Label text_valikost;

    @FXML
    private Label text_zpusob_zisku;

    private String runove_jmeno;

    private String nazev;
    private String runovy_kod;
    private String obcanske_jmeno;
    private String image;
    private String stupen;
    private String typ_predmetu;

    /**
     * Handles the event when the ownership history is requested.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void historie_vlastnictvi(MouseEvent event) throws IOException, SQLException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_history.fxml"));
        Parent root = loader.load();

        ItemHistoryController controller = loader.getController();
        controller.initialize(runove_jmeno, obcanske_jmeno, image, runovy_kod, nazev, typ_predmetu, stupen);

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Handles the event when the ownership is to be changed.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void zmena_vlastnika(MouseEvent event) throws IOException, SQLException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lose_item.fxml"));
        Parent root = loader.load();

        LoseItemController controller = loader.getController();
        controller.initialize(runove_jmeno, runovy_kod, obcanske_jmeno, nazev, stupen);

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Initializes the view with item details.
     *
     * @param obcanske_jmeno The name of the current user.
     * @param runove_jmeno   The rune name of the current user.
     * @param runovy_kod     The code of the current item.
     * @param rs             The ResultSet containing item details.
     * @throws SQLException If an SQL exception occurs.
     */
    public void initialize(String obcanske_jmeno, String runove_jmeno, String runovy_kod, ResultSet rs) throws SQLException
    {
        this.runovy_kod = runovy_kod;
        this.runove_jmeno = runove_jmeno;
        this.obcanske_jmeno = obcanske_jmeno;

        nazev = rs.getNString("nazev");
        typ_predmetu = rs.getNString("nazev_typu");
        stupen = rs.getNString("stupen_nebezpecnosti");
        image = rs.getNString("image");

        String velikost = rs.getNString("velikost");
        String vlastnik = rs.getString("obcanske_jmeno");
        String zpusob_zisku = rs.getString("zpusob_ziskani");

        if (image == null)
        {
            image = "unknown.png";
        }

        image_predmet.setImage(new Image(getClass().getResourceAsStream("images/items/" + image)));

        text_nazev_predmetu.setText("Název: " + nazev);
        text_typ_predmetu.setText("Typ: " + typ_predmetu);
        text_stupen.setText("Stupeň nebezpečnosti: " + stupen);
        text_valikost.setText("Velikost: " + velikost);

        text_zpusob_zisku.setVisible(vlastnik != null);
        text_zpusob_zisku.setText("Způsob zisku: " + zpusob_zisku);

        if (vlastnik == null)
        {
            vlastnik = "Předmět není nikým vlastněn.";
        }

        text_nazev_vlastnika.setText("Vlastník: " + vlastnik);
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

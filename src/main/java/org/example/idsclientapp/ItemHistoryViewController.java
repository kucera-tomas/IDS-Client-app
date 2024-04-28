package org.example.idsclientapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Controller class for managing item history view.
 */
public class ItemHistoryViewController
{

    @FXML
    private ImageView image_vlastnik_predmetu;

    @FXML
    private HBox item_hbox;

    @FXML
    private Label text_runove_jmeno_vlastnika;

    @FXML
    private Label text_vlastni_do;

    @FXML
    private Label text_vlastni_od;

    @FXML
    private Label text_zpusob_zisku;

    @FXML
    private Label text_zpusob_ztraty;

    /**
     * Initializes the item history view with the provided details.
     *
     * @param image The image of the item.
     * @param vlastnik The owner of the item.
     * @param zpusob_zisku The method of acquisition of the item.
     * @param zpusob_ztraty The method of loss of the item.
     * @param datum_ziskani The date of acquisition of the item.
     * @param datum_ztraty The date of loss of the item.
     */
    public void initialize(String image, String vlastnik, String zpusob_zisku, String zpusob_ztraty, String datum_ziskani, String datum_ztraty)
    {
        if (image == null)
        {
            image = "unknown.png";
        }

        image_vlastnik_predmetu.setImage(new Image(getClass().getResourceAsStream("images/mages/" + image)));

        if (vlastnik == null)
        {
            text_runove_jmeno_vlastnika.setText("Předmět není nikým vlastněn");
            text_zpusob_zisku.setVisible(false);
        }
        else
        {
            text_runove_jmeno_vlastnika.setText("Vlastník: " + vlastnik);

            text_zpusob_zisku.setVisible(true);
            text_zpusob_zisku.setText("Způsob zisku: " + zpusob_zisku);
        }

        text_vlastni_do.setText("Datum ztráty: " + datum_ztraty);
        text_vlastni_od.setText("Datum získání: " + datum_ziskani);

        text_zpusob_ztraty.setVisible(zpusob_ztraty != null);
        text_zpusob_ztraty.setText("Způsob ztráty: " + zpusob_ztraty);
    }
}

package org.example.idsclientapp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ItemViewController
{
    @FXML
    private ImageView image_background;

    @FXML
    private ImageView image_pouzit;

    @FXML
    private ImageView image_predmet;

    @FXML
    private ImageView image_ztratit;

    @FXML
    private HBox item_hbox;

    @FXML
    private Label text_nazev_predmetu;

    @FXML
    private Label text_stupen;

    @FXML
    private Label text_typ_predmetu;

    @FXML
    private Label text_valikost;

    private String stupen;
    private String runovy_kod;

    private KouzelnikController kc;

    /**
     * Handles the event when using the item is requested.
     *
     * @param event The MouseEvent triggering the event.
     */
    @FXML
    void pouzit_predmet(MouseEvent event)
    {
        int stupen_value = Integer.parseInt(stupen);
        if (stupen_value == 10)
        {
            // Emergency warnings
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Systémový poplach");
            alert.setHeaderText(null);
            alert.setContentText("Bylo manipulováno s předmětem se stupňem nebezpečnosti 10!");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Použití předmětu");
            alert.setHeaderText(null);
            alert.setContentText("Předmět " + text_nazev_predmetu.getText() + " byl úspěšně použit.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the event when losing the item is requested.
     *
     * @param event The MouseEvent triggering the event.
     * @throws SQLException If an SQL exception occurs.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    void ztratit_predmet(MouseEvent event) throws SQLException, IOException
    {
        String sql = """
                UPDATE VLASTNICTVI 
                SET
                    zpusob_ztraty = ?,
                    datum_ztraty = SYSDATE
                WHERE 
                    zpusob_ztraty IS NULL 
                    AND runovy_kod = ?""";

        String zpusob_ztraty = "Dobrovolné vzdání se předmětu";

        DatabaseController.query(sql, new String[]{zpusob_ztraty, runovy_kod});

        kc.loadItems();
    }


    /**
     * Initializes the item view with the provided details.
     *
     * @param runovy_kod The code of the current run.
     * @param image The image of the item.
     * @param nazev The name of the item.
     * @param typ The type of the item.
     * @param stupen The danger level of the item.
     * @param popis The description of the danger level.
     * @param velikost The size of the item.
     * @param kc The KouzelnikController instance.
     */
    public void initialize(String runovy_kod, String image, String nazev, String typ, String stupen, String popis, String velikost, KouzelnikController kc)
    {
        image_background.setImage(new Image(getClass().getResourceAsStream("images/item_background.png")));

        this.stupen = stupen;
        this.runovy_kod = runovy_kod;

        text_nazev_predmetu.setText("Název předmětu: " + nazev);
        text_stupen.setText("Stupeň nebezpečnosti: " + popis + " (" + stupen + ")");
        text_valikost.setText("Velikost: " + velikost);
        text_typ_predmetu.setText("Typ předmětu: " + typ);

        if (image == null)
        {
            image = "unknown.png";
        }

        image_predmet.setImage(new Image(getClass().getResourceAsStream("images/items/" + image)));

        this.kc = kc;
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


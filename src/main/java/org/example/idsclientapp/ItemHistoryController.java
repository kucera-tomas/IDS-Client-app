package org.example.idsclientapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemHistoryController
{

    @FXML
    private AnchorPane anchor_pane_items;

    @FXML
    private ImageView button_back_item_list;

    @FXML
    private ImageView image_predmet;

    @FXML
    private Text text_nazev_predmetu;

    @FXML
    private Text text_nebezpecnost;

    @FXML
    private Text text_typ_predmetu;

    @FXML
    private VBox vbox_items;

    @FXML
    private ScrollPane scroll_pane;

    private String runove_jmeno;
    private String obcanske_jmeno;

    /**
     * Handles the event when returning to the item list is requested.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void button_back_item_list(MouseEvent event) throws IOException, SQLException
    {
        // Load the new scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("item_list.fxml"));
        Parent root = loader.load();

        ItemListController controller = loader.getController();
        controller.initialize(runove_jmeno, obcanske_jmeno);

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Initializes the item history view.
     *
     * @param runove_jmeno The name of the current runner.
     * @param obcanske_jmeno The name of the current user.
     * @param image The image of the item.
     * @param runovy_kod The code of the current run.
     * @param nazev The name of the item.
     * @param typ The type of the item.
     * @param stupen The danger level of the item.
     * @throws SQLException If an SQL exception occurs.
     * @throws IOException If an I/O error occurs.
     */
    public void initialize(String runove_jmeno, String obcanske_jmeno, String image, String runovy_kod, String nazev, String typ, String stupen) throws SQLException, IOException
    {
        this.runove_jmeno = runove_jmeno;
        this.obcanske_jmeno = obcanske_jmeno;

        if (image == null)
        {
            image = "unknown.png";
        }

        image_predmet.setImage(new Image(getClass().getResourceAsStream("images/items/" + image)));

        text_nazev_predmetu.setText("Název: " + nazev);
        text_typ_predmetu.setText("Typ: " + typ);
        text_nebezpecnost.setText("Stupeň nebezpečnosti: " + stupen);

        String sql = """
                SELECT k.image AS image, obcanske_jmeno, TO_CHAR(datum_ziskani, 'DD. MM. YYYY') AS datum, zpusob_ziskani, zpusob_ztraty, TO_CHAR(datum_ziskani, 'YYYY-MM-DD'),
                    CASE
                        WHEN zpusob_ztraty IS NULL THEN 'Stále vlastní'
                        ELSE TO_CHAR(datum_ztraty, 'DD. MM. YYYY')
                    END AS datum_ztraty
                FROM VLASTNICTVI JOIN PREDMET USING(runovy_kod) JOIN KOUZELNIK k USING(runove_jmeno)
                WHERE runovy_kod = ?
                ORDER BY datum_ztraty
                """;

        ResultSet rs = DatabaseController.query(sql, new String[]{runovy_kod});

        if (rs == null)
        {
            return;
        }

        int i;
        for (i = 0; rs.next(); i++)
        {
            String vlastnik = rs.getString("obcanske_jmeno");
            String zpusob_zisku = rs.getString("zpusob_ziskani");
            String zpusob_ztraty = rs.getNString("zpusob_ztraty");
            String datum_ziskani = rs.getString("datum");
            String datum_ztraty = rs.getString("datum_ztraty");
            image = rs.getString("image");


            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_view_history.fxml"));
            Pane itemNode = loader.load();


            ItemHistoryViewController controller = loader.getController();
            controller.initialize(image, vlastnik, zpusob_zisku, zpusob_ztraty, datum_ziskani, datum_ztraty);

            vbox_items.getChildren().add(itemNode);
        }

        int height = Math.min(5, i) * 100;
        scroll_pane.setPrefHeight(height);
        anchor_pane_items.setPrefHeight(height);
        vbox_items.setPrefHeight(height);
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
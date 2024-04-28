package org.example.idsclientapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import org.controlsfx.control.PropertySheet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller class for managing the item list view.
 */
public class ItemListController
{

    @FXML
    private VBox vbox_items;

    @FXML
    private ImageView image_back;

    @FXML
    private AnchorPane anchor_pane_items;

    @FXML
    private ScrollPane scroll_pane;
    private String runove_jmeno;

    /**
     * Handles the event when returning to the previous view is requested.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void button_back(MouseEvent event) throws IOException, SQLException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("kouzelnik.fxml"));
        Parent root = loader.load();

        KouzelnikController kc = loader.getController();
        kc.initialize(runove_jmeno);

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initializes the item list view.
     *
     * @param runove_jmeno The name of the current runner.
     * @param obcanske_jmeno The name of the current user.
     * @throws SQLException If an SQL exception occurs.
     * @throws IOException If an I/O error occurs.
     */
    public void initialize(String runove_jmeno, String obcanske_jmeno) throws SQLException, IOException
    {
        this.runove_jmeno = runove_jmeno;
        String sql = """
                SELECT runovy_kod, p.nazev, tp.nazev AS nazev_typu, stupen_nebezpecnosti, velikost, p.image, obcanske_jmeno, zpusob_ziskani, zpusob_ztraty
                FROM PREDMET p
                    LEFT JOIN VLASTNICTVI USING(runovy_kod)
                    LEFT JOIN KOUZELNIK USING(runove_jmeno)
                    INNER JOIN TYP_PREDMETU tp ON tp.id_typu = p.typ_predmetu""";

        ResultSet rs = DatabaseController.query(sql, new String[]{});

        if (rs == null)
        {
            return;
        }

        ArrayList<String> runove_kody = new ArrayList<String>();

        for (int i = 0; rs.next(); i++)
        {
            String runovy_kod = rs.getNString("runovy_kod");

            if (runove_kody.indexOf(runovy_kod) != -1)
            {
                continue;
            }

            runove_kody.add(runovy_kod);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_complex_view.fxml"));
            Pane itemNode = loader.load();

            ItemComplexViewController controller = loader.getController();
            controller.initialize(obcanske_jmeno, runove_jmeno, runovy_kod, rs);

            vbox_items.getChildren().add(itemNode);
        }

        int height = runove_kody.size() * 100;
        anchor_pane_items.setMaxHeight(height);
        anchor_pane_items.setPrefHeight(height);
        vbox_items.setMaxHeight(height);
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

    public ItemListController()
    {

    }
}

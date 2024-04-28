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
import java.sql.SQLDataException;
import java.sql.SQLException;

/**
 * Controller class for managing the Kouzelnik view.
 */
public class KouzelnikController
{
    @FXML
    private AnchorPane anchor_pane_items;

    @FXML
    private ImageView image_frame;

    @FXML
    private ImageView image_kouzelnik;

    @FXML
    private ImageView image_logout;

    @FXML
    private ImageView image_sprava_predmetu;

    @FXML
    private ImageView image_zachytit_stopu;

    @FXML
    private VBox items_list;

    @FXML
    private Text text_adresa;

    @FXML
    private Text text_kancelar;

    @FXML
    private Text text_obcanske_jmeno;

    @FXML
    private Text text_pracovni_areal;

    @FXML
    private Text text_runove_jmeno;

    @FXML
    private Text text_specializace;

    @FXML
    private ScrollPane scroll_pane;

    @FXML
    private Text text_uroven;
    private String runove_jmeno = "";
    private String obcanske_jmeno = "";

    /**
     * Loads the items associated with the current user.
     *
     * @throws SQLException If an SQL exception occurs.
     * @throws IOException  If an I/O error occurs.
     */
    void loadItems() throws SQLException, IOException
    {

        while (!items_list.getChildren().isEmpty())
        {
            items_list.getChildren().remove(0);
        }

        String sql = """
                SELECT runovy_kod AS kod, p.nazev AS nazev, t.nazev AS typ, n.stupen as stupen, n.popis AS popis, p.velikost AS velikost, p.image AS image
                FROM VLASTNICTVI v 
                    JOIN PREDMET p USING(runovy_kod) 
                    JOIN NEBEZPECNOST n ON p.stupen_nebezpecnosti = n.stupen
                    JOIN TYP_PREDMETU t ON p.typ_predmetu = t.id_typu
                WHERE runove_jmeno=? AND v.zpusob_ztraty IS NULL
                """;

        ResultSet rs = DatabaseController.query(sql, new String[]{runove_jmeno});

        if (rs == null)
        {
            return;
        }

        int i;
        for (i = 0; rs.next(); i++)
        {
            String nazev = rs.getNString("nazev");
            String runovy_kod = rs.getNString("kod");
            String typ_predmetu = rs.getNString("typ");
            String stupen = rs.getNString("stupen");
            String popis = rs.getNString("popis");
            String velikost = rs.getNString("velikost");
            String image = rs.getNString("image");


            FXMLLoader loader = new FXMLLoader(getClass().getResource("item_view.fxml"));
            Pane itemNode = loader.load();

            ItemViewController controller = loader.getController();
            controller.initialize(runovy_kod, image, nazev, typ_predmetu, stupen, popis, velikost, this);

            items_list.getChildren().add(itemNode);
        }

        int scroll_height = Math.min(4, i) * 100;
        scroll_pane.setMaxHeight(scroll_height);
        scroll_pane.setHvalue(scroll_height);
        scroll_pane.setHmax(scroll_height);
        scroll_pane.setHmin(scroll_height);
    }

    /**
     * Logs out the current user and navigates to the login page.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    void logout(MouseEvent event) throws IOException
    {
        // Load the new scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Get the stage from the event
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Navigates to the item list management page.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void sprava_predmetu(MouseEvent event) throws IOException, SQLException
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
     * Navigates to the trace capture page.
     *
     * @param event The MouseEvent triggering the event.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    void zachytit_stopu(MouseEvent event) throws IOException, SQLException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("zachytit_stopu.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        DetekceStopyController controller = loader.getController();
        controller.initialize(runove_jmeno);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Initializes the Kouzelnik view with the provided user details.
     *
     * @param runove_jmeno The user's run name.
     * @throws SQLException If an SQL exception occurs.
     * @throws IOException  If an I/O error occurs.
     */
    public void initialize(String runove_jmeno) throws SQLException, IOException
    {
        this.runove_jmeno = runove_jmeno;
        image_frame.setImage(new Image(getClass().getResourceAsStream("images/frame.png")));

        String sql = "SELECT * FROM KOUZELNIK JOIN KOUZELNICKA_UROVEN USING(id_urovne) WHERE runove_jmeno=?";

        ResultSet rs = DatabaseController.query(sql, new String[]{runove_jmeno});

        if (rs == null || !rs.next())
        {
            throw new SQLDataException();
        }

        String obcanske_jmeno = rs.getString("obcanske_jmeno");
        String adresa = rs.getString("adresa_pobytu");
        String nazev = rs.getString("nazev");
        String specializace =rs.getString("specializace");
        String kancelar = rs.getString("kancelar");
        String pracovni_areal = rs.getString("pracovni_areal");
        String image = rs.getString("image");

        text_runove_jmeno.setText("Runové jméno: " + runove_jmeno);
        text_obcanske_jmeno.setText("Občanské jméno: " + obcanske_jmeno);
        text_adresa.setText("Adresa: " + adresa);
        text_uroven.setText("Úroveň: " + nazev);

        text_specializace.setVisible(specializace != null);
        text_specializace.setText("Specializace: " + specializace);

        text_kancelar.setVisible(kancelar != null);
        text_kancelar.setText("Kancelář: " + kancelar);

        text_pracovni_areal.setVisible(pracovni_areal != null);
        text_pracovni_areal.setText("Pracovní areál: " + pracovni_areal);

        if (image == null)
        {
            image = "unknown.png";
        }

        Image img = new Image(getClass().getResourceAsStream("images/mages/" + image));
        image_kouzelnik.setImage(img);

        if (specializace != null)
        {
            image_sprava_predmetu.setVisible(specializace.equals("Ministr"));
            image_zachytit_stopu.setVisible(specializace.equals("Ministr"));
        }
        else
        {
            image_sprava_predmetu.setVisible(false);
            image_zachytit_stopu.setVisible(false);
        }

        loadItems();
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

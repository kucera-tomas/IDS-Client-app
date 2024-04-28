package org.example.idsclientapp;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DetekceStopyController
{
    @FXML
    private ImageView image_back;

    @FXML
    private ImageView image_zachytit;

    @FXML
    private VBox vbox_detektor;

    @FXML
    private VBox vbox_predmet;

    @FXML
    private VBox vbox_stopa;

    private int id_detektoru;
    private String nazev_detektoru;

    private String typ_stopy = null;

    @FXML
    void button_back_kouzelnik(MouseEvent event) throws IOException, SQLException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("kouzelnik.fxml"));
        Parent root = loader.load();

        KouzelnikController controller = loader.getController();
        controller.initialize(runove_jmeno);

        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    void detekovat_stopu(MouseEvent event) throws IOException, SQLException
    {
        if (id_detektoru == -1 || typ_stopy == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chyba při detekci stopy");
            alert.setHeaderText(null);
            alert.setContentText("Pro detekci stopy je nutné zadat detektor a typ detekované stopy.");
            alert.showAndWait();
        }

        if (runovy_kod == null)
        {
            String sql = """
                    INSERT INTO STOPA(datum_vyvolani, id_detektoru, id_stopy, runove_jmeno)
                    VALUES(SYSDATE, ?, (SELECT id_stopy FROM TYP_MAGICKE_STOPY WHERE popis = ?), ?)
                    """;
            DatabaseController.query(sql, new String[]{String.valueOf(id_detektoru), typ_stopy, runove_jmeno});
        }
        else
        {
            String sql = """
                    INSERT INTO STOPA(datum_vyvolani, id_detektoru, id_stopy, runove_jmeno, runovy_kod)
                    VALUES(SYSDATE, ?, (SELECT id_stopy FROM TYP_MAGICKE_STOPY WHERE popis = ?), ?, ?)
                    """;
            DatabaseController.query(sql, new String[]{String.valueOf(id_detektoru), typ_stopy, runove_jmeno, runovy_kod});
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detekce stopy");
        alert.setHeaderText(null);
        alert.setContentText("Stopa byla úspěšně detekována.");
        alert.showAndWait();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("kouzelnik.fxml"));
        Parent root = loader.load();

        KouzelnikController kc = loader.getController();
        kc.initialize(runove_jmeno);

        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private String runove_jmeno;

    private String runovy_kod;
    public void initialize(String runove_jmeno) throws SQLException, IOException
    {
        this.runove_jmeno = runove_jmeno;

        LoadDetectors();
        id_detektoru = -1;
        LoadMagickeStopy();
        LoadItems();
    }

    /**
     * @brief Loads detector options from the database.
     * @throws SQLException if a SQL error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void LoadDetectors() throws SQLException, IOException
    {
        String sql = "SELECT * FROM DETEKTOR_STOP JOIN STAV_DETEKTORU USING(id_stavu)";

        ResultSet rs = DatabaseController.query(sql, new String[] {});

        while (rs.next())
        {
            nazev_detektoru = rs.getString("nazev");
            id_detektoru = rs.getInt("id_detektoru");

            String value = id_detektoru + ": " + nazev_detektoru;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("choiceOption.fxml"));
            Node itemNode = loader.load();

            itemNode.setOnMouseClicked(event -> onDetektorChange(event, itemNode));

            ChoiceOptionController controller = loader.getController();
            controller.setValue(value);

            vbox_detektor.getChildren().add(itemNode);
        }
    }

    /**
     * @brief Loads magical footprint options from the database based on selected detector.
     * @throws SQLException if a SQL error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void LoadMagickeStopy() throws SQLException, IOException
    {
        ResultSet rs;
        if (id_detektoru != -1)
        {
            String sql = "SELECT * FROM TYP_MAGICKE_STOPY JOIN DETEKOVATELNA_STOPA USING(id_stopy) WHERE id_detektoru = ?";
            rs = DatabaseController.query(sql, new String[] { String.valueOf(id_detektoru) });
        }
        else
        {
            String sql = "SELECT * FROM TYP_MAGICKE_STOPY";
            rs = DatabaseController.query(sql, new String[] {});
        }

        while (rs.next())
        {
            String popis = rs.getString("popis");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("choiceOption.fxml"));
            Node itemNode = loader.load();

            itemNode.setOnMouseClicked(event -> onStopaChange(event, itemNode));

            ChoiceOptionController controller = loader.getController();
            controller.setValue(popis);

            vbox_stopa.getChildren().add(itemNode);
        }
    }

    /**
     * @brief Loads item options from the database based on selected magical footprint.
     * @throws SQLException if a SQL error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void LoadItems() throws SQLException, IOException
    {
        ResultSet rs;

        if (typ_stopy != null)
        {
            String sql = "SELECT * FROM PREDMET JOIN TYP_ZANECHAVANE_STOPY USING(runovy_kod) JOIN TYP_MAGICKE_STOPY tms USING(id_stopy) WHERE tms.popis = ?";
            rs = DatabaseController.query(sql, new String[] { typ_stopy });
        }
        else
        {
            String sql = "SELECT * FROM PREDMET WHERE ROWNUM <= 7";
            rs = DatabaseController.query(sql, new String[] {});
        }

        if (rs == null)
        {
            return;
        }

        while (rs.next())
        {
            String runovy_kod = rs.getString("runovy_kod");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("choiceOption.fxml"));
            Node itemNode = loader.load();

            itemNode.setOnMouseClicked(event -> onItemChange(event, itemNode));

            ChoiceOptionController controller = loader.getController();
            controller.setValue(runovy_kod);

            vbox_predmet.getChildren().add(itemNode);
        }
    }

    /**
     * @brief Event handler for when the detector option is clicked.
     * @param mouseEvent MouseEvent representing the click event.
     * @param node The Node representing the clicked detector option.
     */
    private void onDetektorChange(MouseEvent mouseEvent, Node node)
    {
        id_detektoru = Integer.parseInt(getOptionValue(node).split(":")[0]);
        resetOptions(vbox_detektor);
        selectOption(node);
        System.out.println(id_detektoru);
    }

    /**
     * @brief Event handler for when the magical footprint option is clicked.
     * @param mouseEvent MouseEvent representing the click event.
     * @param node The Node representing the clicked magical footprint option.
     */
    private void onStopaChange(MouseEvent mouseEvent, Node node)
    {
        typ_stopy = getOptionValue(node);
        resetOptions(vbox_stopa);
        selectOption(node);
        System.out.println(typ_stopy);
    }

    /**
     * @brief Event handler for when the item option is clicked.
     * @param mouseEvent MouseEvent representing the click event.
     * @param node The Node representing the clicked item option.
     */
    private void onItemChange(MouseEvent mouseEvent, Node node)
    {
        runovy_kod = getOptionValue(node);
        resetOptions(vbox_predmet);
        selectOption(node);
        System.out.println(runovy_kod);
    }

    /**
     * @brief Retrieves the value of the selected option.
     * @param node The Node representing the selected option.
     * @return The value of the selected option.
     */
    public String getOptionValue(Node node)
    {
        Pane p = (Pane)node;

        for (Node child : p.getChildren())
        {
            if (child instanceof Text)
            {
                return ((Text)child).getText();
            }
        }
        return null;
    }

    /**
     * @brief Selects the clicked option and updates its visual appearance.
     * @param node The Node representing the clicked option.
     */
    public void selectOption(Node node)
    {
        Pane p = (Pane)node;

        for (Node child : p.getChildren())
        {
            if (child instanceof ImageView)
            {
                ((ImageView)child).setImage(new Image(getClass().getResourceAsStream("buttons/selected_background.png")));
            }
        }
    }

    /**
     * @brief Resets the visual appearance of all options in the specified VBox.
     * @param vbox The VBox containing the options to be reset.
     */
    public void resetOptions(VBox vbox)
    {
        for (Node child : vbox.getChildren())
        {
            if (child instanceof Pane)
            {
                for (Node n : ((Pane)child).getChildren())
                {
                    if (n instanceof ImageView)
                    {
                        ((ImageView)n).setImage(new Image(getClass().getResourceAsStream("buttons/background.png")));
                    }
                }
            }
        }
    }

    @FXML void button_off_hover(MouseEvent event)
    {
        ImageView imageView = (ImageView)event.getSource();
        imageView.setEffect(null);
    }

    @FXML void button_on_hover(MouseEvent event)
    {
        ImageView imageView = (ImageView)event.getSource();

        Bloom bloom = new Bloom();
        bloom.setThreshold(0.09);
        imageView.setEffect(bloom);
    }
}
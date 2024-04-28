package org.example.idsclientapp;

import javafx.fxml.FXML;
import javafx.scene.effect.Bloom;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * @brief Controller class for managing choice options in the application.
 */
public class ChoiceOptionController
{

    @FXML
    private ImageView image;

    @FXML
    private Text item_text;

    /**
     * @brief Event handler for when an option is clicked.
     * @param event MouseEvent representing the click event.
     */
    @FXML
    void onOptionClicked(MouseEvent event)
    {
        // Print a message indicating that an option is clicked
        System.out.println("option");
    }

    /**
     * @brief Sets the value of the choice option.
     * @param value The value to be set for the choice option.
     */
    public void setValue(String value)
    {
        // Set the text of the choice option
        item_text.setText(value);
    }

    /**
     * @brief Event handler for when the mouse hovers off the button.
     * @param event MouseEvent representing the hover event.
     */
    @FXML
    void button_off_hover(MouseEvent event)
    {
        // Remove any visual effect when the mouse hovers off the button
        ImageView imageView = (ImageView)event.getSource();
        imageView.setEffect(null);
    }

    /**
     * @brief Event handler for when the mouse hovers on the button.
     * @param event MouseEvent representing the hover event.
     */
    @FXML
    void button_on_hover(MouseEvent event)
    {
        // Apply a bloom effect when the mouse hovers on the button
        ImageView imageView = (ImageView)event.getSource();

        Bloom bloom = new Bloom();
        bloom.setThreshold(0.09); // Adjust the threshold as needed
        imageView.setEffect(bloom);
    }
}

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.util.List;
import java.util.function.UnaryOperator;

public class Main extends Application {
    protected TextField budgetField;
    protected TextField quantityField;
    protected ComboBox setSelector;
    protected Label previewArea;
    protected CheckBox popupResultsCheckBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        VBox vbox = new VBox();
        setSelector = new ComboBox();
        try {
            setSelector.getItems().addAll(Scraper.scrapSets());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setSelector.getSelectionModel().select(0);
        setSelector.valueProperty().addListener(new UpdatePreviewListener());
        vbox.getChildren().add(setSelector);

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]*)")) {
                return change;
            }
            return null;
        };

        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]*\\.?\\d{0,2})")) {
                return change;
            }
            return null;
        };

        HBox budgetBox = new HBox();
        budgetBox.getChildren().add(new Label("Budget: $"));
        budgetField = new TextField();
        budgetField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, doubleFilter));
        budgetField.setText("60");
        budgetField.textProperty().addListener(new UpdatePreviewListener());
        budgetBox.getChildren().add(budgetField);
        budgetBox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(budgetBox);

        HBox quantityBox = new HBox();
        quantityBox.getChildren().add(new Label("Cards: "));
        quantityField = new TextField();
        quantityField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
        quantityField.setText("270");
        quantityField.textProperty().addListener(new UpdatePreviewListener());
        quantityBox.getChildren().add(quantityField);
        quantityBox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(quantityBox);

        previewArea = new Label();
        previewArea.setWrapText(true);
        previewArea.setText(generatePreview());

        Button generateButton = new Button("Generate!");
        popupResultsCheckBox = new CheckBox();
        generateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GeneratorList generatorList = null;
                try {
                    generatorList = CubeGenerator.generateFromSet(Integer.parseInt(quantityField.getText()), Double.parseDouble(budgetField.getText()), (Set) setSelector.getSelectionModel().getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (popupResultsCheckBox.isSelected()) {
                    displayResultPopup(generatorList, primaryStage);
                } else {
                    List<Card> cardList = generatorList.getList();
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Image");
                    File file = fileChooser.showSaveDialog(primaryStage);
                    if (file != null) {
                        saveOutputFile(file,cardList);
                    }
                }

            }
        });

        HBox generateButtonBox = new HBox();
        generateButtonBox.setAlignment(Pos.BOTTOM_RIGHT);
        generateButtonBox.getChildren().add(new Label("Display popup?"));
        generateButtonBox.getChildren().add(popupResultsCheckBox);
        generateButtonBox.getChildren().add(generateButton);

        vbox.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(10,10,10,10));
        pane.setTop(vbox);
        pane.setCenter(previewArea);
        pane.setBottom(generateButtonBox);
        primaryStage.setScene(new Scene(pane, 300,250));
        primaryStage.show();
    }

    protected String generatePreview() {
        return quantityField.getText() + " cards from " + ((Set) setSelector.getSelectionModel().getSelectedItem()).name + " totalling $" + budgetField.getText();
    }

    protected void saveOutputFile (File file, List<Card> cardList) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))){
            for (Card card:cardList) {
                out.write("1 " + card.getName() + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void displayResultPopup (GeneratorList generatorList, Stage primaryStage) {
        List<Card> cardList = generatorList.getList();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        VBox dialogVbox = new VBox(20);
        Text costText = new Text("$"+generatorList.getBudgetUsed());
        dialogVbox.getChildren().add(costText);

        StringBuilder cardDisplayText1 = new StringBuilder();
        for (int i = 0; i < cardList.size()/2; i++) {
            cardDisplayText1.append(cardList.get(i));
            cardDisplayText1.append("\n");
        }
        StringBuilder cardDisplayText2 = new StringBuilder();
        for (int i = cardList.size()/2; i < cardList.size(); i++) {
            cardDisplayText2.append(cardList.get(i));
            cardDisplayText2.append("\n");
        }

        Text cardDisplay1 = new Text(cardDisplayText1.toString());
        Text cardDisplay2 = new Text(cardDisplayText2.toString());
        ScrollPane cardPane = new ScrollPane();
        HBox displayHBox = new HBox(20);
        displayHBox.getChildren().add(cardDisplay1);
        displayHBox.getChildren().add(cardDisplay2);
        cardPane.setContent(displayHBox);
        dialogVbox.getChildren().add(cardPane);
        Scene dialogScene = new Scene(dialogVbox, 600, 500);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private class UpdatePreviewListener implements ChangeListener<Object> {
        @Override
        public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
            previewArea.setText(generatePreview());
        }
    }
}

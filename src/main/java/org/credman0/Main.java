package org.credman0;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class Main extends Application {
    protected GeneratorConfiguration generatorConfiguration = new GeneratorConfiguration();
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
        try {
            loadConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BorderPane pane = new BorderPane();
        VBox vbox = new VBox();
        setSelector = new ComboBox();
        try {
            List<Set> sets = Scraper.scrapSets();
            Collections.sort(sets);
            setSelector.getItems().addAll(sets);
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
                new TextFormatter<Double>(new DoubleStringConverter(), 0d, doubleFilter));
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

        Button advancedButton = new Button("Configuration");
        advancedButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                displayAdvancedOptionsPopup(primaryStage);
            }
        });
        vbox.getChildren().add(advancedButton);

        previewArea = new Label();
        previewArea.setWrapText(true);
        previewArea.setText(generatePreview());

        Button generateButton = new Button("Generate!");
        popupResultsCheckBox = new CheckBox();
        generateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GeneratorList generatorList = null;
                CubeGenerator generator = null;
                try {
                    generator = new CubeGenerator(Integer.parseInt(quantityField.getText()), Double.parseDouble(budgetField.getText()), (Set) setSelector.getSelectionModel().getSelectedItem());
                    generator.setRerollCommons(generatorConfiguration.getRerollCommons());
                    generator.setRerollUncommons(generatorConfiguration.getRerollUncommons());
                    generator.setRerollRares(generatorConfiguration.getRerollRares());
                    generator.setPackSize(generatorConfiguration.getPackSize());
                    generator.setCommonsPer(generatorConfiguration.getCommonsPer());
                    generator.setUncommonsPer(generatorConfiguration.getUncommonsPer());

                    generatorList = generator.generate(generatorConfiguration.getExclusions());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (popupResultsCheckBox.isSelected()) {
                    displayResultPopup(generatorList, primaryStage);
                } else {
                    List<Card> cardList = generatorList.getList();
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Generated Cards");
                    fileChooser.setInitialFileName(generator.getSet().getValue() + "("+ NumberFormat.getCurrencyInstance().format(generatorList.budgetUsed)+").txt");
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

    protected void displayAdvancedOptionsPopup (Stage primaryStage) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        BorderPane dialogPane = new BorderPane();
        dialogPane.setPadding(new Insets(10,10,10,10));
        VBox dialogBox = new VBox();

        dialogBox.getChildren().add(new Text("Any card that contains a piece of text in this list will be excluded from generation."));
        ListView<String> listView = new ListView<>(FXCollections.observableArrayList(generatorConfiguration.getExclusions()));
        listView.prefWidthProperty().bind(dialogPane.widthProperty());
        listView.setEditable(true);
        listView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                listView.getItems().set(t.getIndex(), t.getNewValue());
            }
        });
        listView.setCellFactory(TextFieldListCell.forListView());
        HBox listViewBox = new HBox();
        listViewBox.getChildren().add(listView);
        Button listViewAddExclusionButton = new Button("+");
        listViewAddExclusionButton.setPrefWidth(50);
        listViewAddExclusionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                listView.getItems().add("");
            }
        });
        Button listViewRemoveExclusionButton = new Button("-");
        listViewRemoveExclusionButton.setPrefWidth(50);
        listViewRemoveExclusionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (listView.getSelectionModel().getSelectedIndex()>=0) {
                    listView.getItems().remove(listView.getSelectionModel().getSelectedIndex());
                }
            }
        });
        VBox listViewControlBox = new VBox();
        listViewControlBox.setFillWidth(false);
        listViewControlBox.setPrefWidth(50);
        listViewControlBox.getChildren().add(listViewAddExclusionButton);
        listViewControlBox.getChildren().add(listViewRemoveExclusionButton);
        listViewControlBox.setAlignment(Pos.TOP_CENTER);
        listViewBox.getChildren().add(listViewControlBox);
        dialogBox.getChildren().add(listViewBox);

        UnaryOperator<TextFormatter.Change> rerollFilter = change -> {

            String newText = change.getControlNewText();
            if (newText.matches("([0-9]?)")) {
                return change;
            }
            return null;
        };

        HBox dualOptionsBox = new HBox();
        dualOptionsBox.setSpacing(20);
        VBox leftOptionsBox = new VBox();
        VBox rightOptionsBox = new VBox();
        dualOptionsBox.getChildren().add(leftOptionsBox);
        dualOptionsBox.getChildren().add(new Separator(Orientation.VERTICAL));
        dualOptionsBox.getChildren().add(rightOptionsBox);
        dialogBox.getChildren().add(dualOptionsBox);

        HBox rerollCommonsBox = new HBox();
        rerollCommonsBox.getChildren().add(new Text("Reroll duplicate commons "));
        TextField rerollCommonsField = new TextField();
        rerollCommonsField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, rerollFilter));
        rerollCommonsField.setPrefWidth(24);
        rerollCommonsField.setMinWidth(24);
        rerollCommonsField.setText(generatorConfiguration.getRerollCommons() + "");
        rerollCommonsBox.getChildren().add(rerollCommonsField);
        rerollCommonsBox.getChildren().add(new Text(" times."));
        rerollCommonsBox.setAlignment(Pos.CENTER_LEFT);
        leftOptionsBox.getChildren().add(rerollCommonsBox);

        HBox rerollUncommonsBox = new HBox();
        rerollUncommonsBox.getChildren().add(new Text("Reroll duplicate uncommons "));
        TextField rerollUncommonsField = new TextField();
        rerollUncommonsField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, rerollFilter));
        rerollUncommonsField.setPrefWidth(24);
        rerollUncommonsField.setMinWidth(24);
        rerollUncommonsField.setText(generatorConfiguration.getRerollUncommons() + "");
        rerollUncommonsBox.getChildren().add(rerollUncommonsField);
        rerollUncommonsBox.getChildren().add(new Text(" times."));
        rerollUncommonsBox.setAlignment(Pos.CENTER_LEFT);
        leftOptionsBox.getChildren().add(rerollUncommonsBox);

        HBox rerollRaresBox = new HBox();
        rerollRaresBox.getChildren().add(new Text("Reroll duplicate rares "));
        TextField rerollRaresField = new TextField();
        rerollRaresField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, rerollFilter));
        rerollRaresField.setPrefWidth(24);
        rerollRaresField.setMinWidth(24);
        rerollRaresField.setText(generatorConfiguration.getRerollRares() + "");
        rerollRaresBox.getChildren().add(rerollRaresField);
        rerollRaresBox.getChildren().add(new Text(" times."));
        rerollRaresBox.setAlignment(Pos.CENTER_LEFT);
        leftOptionsBox.getChildren().add(rerollRaresBox);

        int currentPackSize = 15;
        // one count arrays becuase they must be final to be accessed in listeners
        final int[] currentCommonCount = {generatorConfiguration.getCommonsPer()};
        final int[] currentUncommonCount = {generatorConfiguration.getUncommonsPer()};
        Text rareCountText = new Text((currentPackSize - generatorConfiguration.getCommonsPer() - generatorConfiguration.getUncommonsPer()) + "");

        UnaryOperator<TextFormatter.Change> commonCountFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([0-9][0-9]*)") && Integer.parseInt(newText) + currentUncommonCount[0] <= currentPackSize) {
                return change;
            }
            return null;
        };

        UnaryOperator<TextFormatter.Change> uncommonCountFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([0-9][0-9]*)") && Integer.parseInt(newText) + currentCommonCount[0] <= currentPackSize) {
                return change;
            }
            return null;
        };

        HBox commonsCountBox = new HBox();
        commonsCountBox.getChildren().add(new Text("Place exactly "));
        TextField commonsCountField = new TextField();
        commonsCountField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, commonCountFilter));
        commonsCountField.setPrefWidth(48);
        commonsCountField.setText(currentCommonCount[0] + "");
        commonsCountField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                currentCommonCount[0] = Integer.parseInt(t1);
                rareCountText.setText((currentPackSize - currentCommonCount[0] - currentUncommonCount[0]) + "");
            }
        });
        commonsCountBox.getChildren().add(commonsCountField);
        Text boldC = new Text(" C");
        boldC.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 12));
        commonsCountBox.getChildren().add(boldC);
        commonsCountBox.getChildren().add(new Text("ommons per pack."));
        commonsCountBox.setAlignment(Pos.CENTER_LEFT);
        rightOptionsBox.getChildren().add(commonsCountBox);

        HBox uncommonsCountBox = new HBox();
        uncommonsCountBox.getChildren().add(new Text("Place exactly "));
        TextField uncommonsCountField = new TextField();
        uncommonsCountField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, uncommonCountFilter));
        uncommonsCountField.setPrefWidth(48);
        uncommonsCountField.setText(currentUncommonCount[0] + "");
        uncommonsCountField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                currentUncommonCount[0] = Integer.parseInt(t1);
                rareCountText.setText((currentPackSize - currentCommonCount[0] - currentUncommonCount[0]) + "");
            }
        });
        uncommonsCountBox.getChildren().add(uncommonsCountField);
        Text boldU = new Text(" U");
        boldU.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 12));
        uncommonsCountBox.getChildren().add(boldU);
        uncommonsCountBox.getChildren().add(new Text("ncommons per pack."));
        uncommonsCountBox.setAlignment(Pos.CENTER_LEFT);
        rightOptionsBox.getChildren().add(uncommonsCountBox);

        HBox raresCountBox = new HBox();
        raresCountBox.getChildren().add(new Text("Place exactly "));
        rareCountText.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 12));
        raresCountBox.getChildren().add(rareCountText);
        Text boldR = new Text(" R");
        boldR.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 12));
        raresCountBox.getChildren().add(boldR);
        raresCountBox.getChildren().add(new Text("ares or"));
        Text boldM = new Text(" M");
        boldM.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 12));
        raresCountBox.getChildren().add(boldM);
        raresCountBox.getChildren().add(new Text("ythics per pack."));
        raresCountBox.setAlignment(Pos.CENTER_LEFT);
        rightOptionsBox.getChildren().add(raresCountBox);

        dialogPane.setCenter(dialogBox);

        HBox confirmBox = new HBox();
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                generatorConfiguration.getExclusions().clear();
                listView.getItems().removeAll("");
                generatorConfiguration.getExclusions().addAll(listView.getItems());
                if (!rerollCommonsField.getText().equals("")){
                    generatorConfiguration.setRerollCommons(Integer.parseInt(rerollCommonsField.getText()));
                }
                if (!rerollUncommonsField.getText().equals("")){
                    generatorConfiguration.setRerollUncommons(Integer.parseInt(rerollUncommonsField.getText()));
                }
                if (!rerollRaresField.getText().equals("")){
                    generatorConfiguration.setRerollRares(Integer.parseInt(rerollRaresField.getText()));
                }
                generatorConfiguration.setCommonsPer(currentCommonCount[0]);
                generatorConfiguration.setUncommonsPer(currentUncommonCount[0]);
                try {
                    saveConfiguration();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.close();
            }
        });
        confirmBox.getChildren().add(confirmButton);
        confirmBox.setAlignment(Pos.BOTTOM_RIGHT);
        dialogPane.setBottom(confirmBox);

        Scene dialogScene = new Scene(dialogPane, 600, 500);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    protected void saveConfiguration () throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(generatorConfiguration);
        File file = new File("config.json");
        FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
    }

    protected void loadConfiguration () throws IOException {
        Gson gson = new Gson();
        File file = new File("config.json");
        if (!file.exists()) {
            return;
        }
        String json = FileUtils.readFileToString(file, Charset.defaultCharset());
        generatorConfiguration = gson.fromJson(json, GeneratorConfiguration.class);
    }

    private class UpdatePreviewListener implements ChangeListener<Object> {
        @Override
        public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
            previewArea.setText(generatePreview());
        }
    }
}

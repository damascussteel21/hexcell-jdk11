package com.jramos.hexcelljdk11;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class AppDisplayController implements Initializable {

    private final ConversionCalculator converter = new ConversionCalculator();

    private int baseMax = 16;

    @FXML
    private Button convert;

    @FXML
    private Button swap;

    @FXML
    private Button clear;

    @FXML
    private TextField input;

    @FXML
    private TextField output;

    @FXML
    private TextField base1;

    @FXML
    private TextField base2;

    @FXML
    private TextField sum;

    @FXML
    private TextField result1;

    @FXML
    private TextField result2;

    @FXML
    private AnchorPane pane;

    @FXML
    private AnchorPane pane1;

    @FXML
    private AnchorPane pane2;

    @FXML
    private AnchorPane pane3;

    @FXML
    private ColorPicker color1;

    @FXML
    private ColorPicker color2;

    @FXML
    private TextField precision;

    @FXML
    private TextField bases;

    @FXML
    private TableView<ConversionCalculator.TableData1> table1;

    @FXML
    private TableView<ConversionCalculator.TableData2> table2;

    @FXML
    private TableView<ConversionCalculator.TableData3> table3;

    @FXML
    private TableColumn<ConversionCalculator.TableData1, String> _operation = new TableColumn<>("Operation 1");

    @FXML
    private TableColumn<ConversionCalculator.TableData1, String> _product = new TableColumn<>("Product 1");

    @FXML
    private TableColumn<ConversionCalculator.TableData2, String> _operation1 = new TableColumn<>("Operation 2");

    @FXML
    private TableColumn<ConversionCalculator.TableData2, String> _quotient1 = new TableColumn<>("Quotient 2");

    @FXML
    private TableColumn<ConversionCalculator.TableData2, String> _remainder1 = new TableColumn<>("Remainder 2");

    @FXML
    private TableColumn<ConversionCalculator.TableData3, String> _operation2 = new TableColumn<>("Operation 3");

    @FXML
    private TableColumn<ConversionCalculator.TableData3, String> _product2 = new TableColumn<>("Product 3");

    @FXML
    private TableColumn<ConversionCalculator.TableData3, String> _integral2 = new TableColumn<>("Integral 3");

    // 2.1 - Process Conversion
    @FXML
    public void processConversion() {

        converter.clearTables();
        convert.setEffect(new Glow());
        removeGlow(convert);

        String value = "100";
        int b1 = 10, b2 = 10;

        // 2.1.1 - Fetch Values From Fields
        try {
            value = input.getText();
            b1 = Integer.parseInt(base1.getText());
            b2 = Integer.parseInt(base2.getText());
        } catch (Exception exception) {
            return;
        }

        // 2.1.2 - Select Conversion Processes
        try {
            if (b1 == 10 && b2 != 10) {
                updateOutput(converter.convertFromDecimal(value, b2));
            } else if (b1 != 10 && b2 == 10) {
                updateOutput(converter.convertToDecimal(value, b1));
            } else if (b1 != 10 && b2 != 10) {
                updateOutput(converter.convertFromDecimal(converter.convertToDecimal(value, b1), b2));
            } else {
                output.setText(value);
            }
            sum.setText(converter.getSum());
            result1.setText(converter.getResult1());
            result2.setText(converter.getResult2());
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    // 2.2 - Process Swap
    @FXML
    public void processSwap() {
        swap.setEffect(new Glow());
        removeGlow(swap);
        String tempVal = input.getText(), tempBase = base1.getText();
        input.setText(output.getText());
        base1.setText(base2.getText());
        output.setText(tempVal);
        base2.setText(tempBase);
    }

    // 2.3 - Process Clear
    @FXML
    public void processClear() {
        converter.clearTables();
        output.setStyle("-fx-font-family: monospaced;");
        clear.setEffect(new Glow());
        removeGlow(clear);
        input.setText("");
        output.setText("");
        base1.setText("");
        base2.setText("");
        sum.setText("");
        result1.setText("");
        result2.setText("");
    }

    // 2.4 - Update Output
    public void updateOutput(String out) {
        output.setText(out);
    }

    // 2.5 - Remove Glow
    public void removeGlow(Button button) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                button.setEffect(null);
            }
        }, 100);
    }

    // 2.6 - Set Colors
    public void setColors() {
        String style1 = String.format("#%02X%02X%02X",
                (int) (color1.getValue().getRed() * 255),
                (int) (color1.getValue().getGreen() * 255),
                (int) (color1.getValue().getBlue() * 255)
        );
        String style2 = String.format("#%02X%02X%02X",
                (int) (color2.getValue().getRed() * 255),
                (int) (color2.getValue().getGreen() * 255),
                (int) (color2.getValue().getBlue() * 255)
        );
        pane.setStyle("-fx-background-color: " + style1 + ";");
        pane1.setStyle("-fx-background-color: " + style2 + ";");
        pane2.setStyle("-fx-background-color: " + style2 + ";");
        pane3.setStyle("-fx-background-color: " + style2 + ";");
    }

    public void setPrecision() {
        String field = "64";
        try {
            if (precision.getText().isBlank()) {
                converter.setPrecision(64);
                return;
            }
            field = precision.getText();
        } catch (Exception exception) {
            field = "64";
        }
        converter.setPrecision(Integer.parseInt(field));
    }

    public void setBaseMax() {
        String field = "16";
        try {
            if (bases.getText().isBlank()) {
                baseMax = 16;
                return;
            }
            field = bases.getText();
            baseMax = Integer.parseInt(field);
        } catch (Exception exception) {
            field = "16";
        }
        clampBases();
    }

    public void clampBases() {
        int base1;
        int base2;

        try {
            base1 = Integer.parseInt(this.base1.getText());
            if (base1 > baseMax) {
                this.base1.setText(String.valueOf(baseMax));
            }
            if (base1 < 2) {
                this.base1.setText("2");
            }
        } catch (Exception exception) {
            this.base1.setText("");
        }

        try {
            base2 = Integer.parseInt(this.base2.getText());
            if (base2 > baseMax) {
                this.base2.setText(String.valueOf(baseMax));
            }
            if (base2 < 2) {
                this.base2.setText("2");
            }
        } catch (Exception exception) {
            this.base2.setText("");
        }

        clampInput();
    }

    public void clampInput() {
        int base1;
        String input;
        String replaced = "";

        try {
            base1 = Integer.parseInt(this.base1.getText());
            input = this.input.getText();
        } catch (Exception exception) {
            return;
        }

        for (int i = 0; i < input.length(); i++) {
            int digitValue;
            if (Character.isLetter(input.charAt(i))) {
                digitValue = Character.toUpperCase(input.charAt(i)) - 55;
                char replace = base1 < 10 ? String.valueOf(base1 - 1).charAt(0): (char) (base1 - 1 + 55);
                if (digitValue > base1 - 1) replaced += replace; else replaced += (char) Character.toUpperCase(input.charAt(i));
            } else if (Character.isDigit(input.charAt(i))){
                digitValue = input.charAt(i) - '0';
                replaced += digitValue > base1 - 1 ? base1 - 1 : input.charAt(i) - '0';
            } else if (input.charAt(i) == '.') {
                replaced += '.';
            } else {
                this.input.setText("");
                return;
            }
        }
        if (!input.equalsIgnoreCase(replaced)) {
            this.input.setText(replaced);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                processConversion();
            }
        });

        base1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                processConversion();
            }
        });

        base2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                processConversion();
            }
        });

        output.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                processConversion();
            }
        });

        try {
            table1.setItems(converter.table1);
            table2.setItems(converter.table2);
            table3.setItems(converter.table3);

            _operation.setCellValueFactory(cellData -> cellData.getValue().getOperation());
            _product.setCellValueFactory(cellData -> cellData.getValue().getProduct());

            _operation1.setCellValueFactory(cellData -> cellData.getValue().getOperation());
            _quotient1.setCellValueFactory(cellData -> cellData.getValue().getQuotient());
            _remainder1.setCellValueFactory(cellData -> cellData.getValue().getRemainder());

            _operation2.setCellValueFactory(cellData -> cellData.getValue().getOperation());
            _product2.setCellValueFactory(cellData -> cellData.getValue().getProduct());
            _integral2.setCellValueFactory(cellData -> cellData.getValue().getIntegral());

            if (!table1.getColumns().contains(_operation)) {
                table1.getColumns().add(_operation);
            }
            if (!table1.getColumns().contains(_product)) {
                table1.getColumns().add(_product);
            }

            if (!table2.getColumns().contains(_operation1)) {
                table2.getColumns().add(_operation1);
            }
            if (!table2.getColumns().contains(_quotient1)) {
                table2.getColumns().add(_quotient1);
            }
            if (!table2.getColumns().contains(_remainder1)) {
                table2.getColumns().add(_remainder1);
            }

            if (!table3.getColumns().contains(_operation2)) {
                table3.getColumns().add(_operation2);
            }
            if (!table3.getColumns().contains(_product2)) {
                table3.getColumns().add(_product2);
            }
            if (!table3.getColumns().contains(_integral2)) {
                table3.getColumns().add(_integral2);
            }

            base1.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    clampBases();
                }
            });

            base2.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    clampBases();
                }
            });

            input.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    clampInput();
                }
            });

            precision.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    setPrecision();
                }
            });

            bases.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    setBaseMax();
                }
            });

        } catch (Exception exception) {
            return;
        }

    }

}
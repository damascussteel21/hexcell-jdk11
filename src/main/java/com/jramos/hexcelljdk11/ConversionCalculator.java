package com.jramos.hexcelljdk11;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

public class ConversionCalculator extends Application {

    public ObservableList<TableData1> table1 = FXCollections.observableArrayList();
    public ObservableList<TableData2> table2 = FXCollections.observableArrayList();
    public ObservableList<TableData3> table3 = FXCollections.observableArrayList();

    private String sum;
    private String result1;
    private String result2;

    private int precision = 64;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(ConversionCalculator.class.getResource("app-ui.fxml")));
        Scene scene = new Scene(fxmlLoader.load());

        InputStream iconStream = getClass().getResourceAsStream("logo.png");
        Image icon = new Image(iconStream);
        stage.getIcons().add(icon);

        stage.setTitle("Number System Converter");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // 1.1 - Split String
    public static String[] splitString(String value) {
        String[] components = new String[2];
        if (!value.contains(".")) {
            components[0] = value;
            components[1] = "0";
            return components;
        }
        return value.split("\\.");
    }

    // 1.2 - Get Cell
    private BigDecimal getCell(char digit, int base) {
        int value = Character.isLetter(digit) ? Character.toUpperCase(digit) - 55 : Character.getNumericValue(digit);
        return BigDecimal.valueOf(value);
    }

    // 1.3 - Base To Power
    private BigDecimal baseToPower(int base, int exponent) {
        BigDecimal baseValue = BigDecimal.valueOf(base);

        if (exponent >= 0) {
            return baseValue.pow(exponent);
        } else {
            return BigDecimal.ONE.divide(baseValue.pow(-exponent), precision, RoundingMode.HALF_UP);
        }
    }

    // 1.4 - Convert To Decimal
    public String convertToDecimal(String value, int base) {
        String[] components = splitString(value);
        BigDecimal sum = BigDecimal.ZERO;
        int length1 = components[0].length();
        int length2 = components[1].length();

        // 1.4.1 - Float Converter
        if (!components[1].equals("0"))
        for (int i = length2 - 1; i >= 0; i--) {
            BigDecimal cell = getCell(components[1].charAt(i), base);
            BigDecimal power = baseToPower(base, -i - 1);
            sum = sum.add(cell.multiply(power));
            table1.add(new TableData1(components[1].charAt(i), base, -1 * (i + 1), cell.multiply(power).stripTrailingZeros().toString()));
        }

        // 1.4.2 - Integer Converter
        for (int i = length1 - 1; i >= 0; i--) {
            int cell = Character.getNumericValue(components[0].charAt(i));
            BigDecimal power = baseToPower(base, length1 - i - 1);
            sum = sum.add(BigDecimal.valueOf(cell).multiply(power));
            table1.add(new TableData1(components[0].charAt(i), base, length1 - i - 1, BigDecimal.valueOf(cell).multiply(power).stripTrailingZeros().toString()));
        }

        this.sum = sum.stripTrailingZeros().toString();
        return sum.stripTrailingZeros().toPlainString();
    }

    // 1.5 - Convert From Decimal
    public String convertFromDecimal(String value, int base) {
        String[] components = splitString(value);
        BigInteger decimal = new BigInteger(components[0]);
        BigDecimal floats = new BigDecimal("0." + components[1]);
        StringBuilder cell = new StringBuilder();

        // 1.5.1 - Integer Converter
        do {
            BigInteger[] divRemainder = decimal.divideAndRemainder(BigInteger.valueOf(base));
            BigInteger remainder = divRemainder[1];
            if (remainder.compareTo(BigInteger.TEN) >= 0) {
                cell.insert(0, (char) (remainder.intValue() + 55));
            } else {
                cell.insert(0, remainder);
            }
            table2.add(new TableData2(decimal, base, divRemainder[0], remainder));
            decimal = divRemainder[0];
        } while (decimal.compareTo(BigInteger.ZERO) > 0);

        this.result1 = cell.toString();

        if (floats.compareTo(BigDecimal.ZERO) > 0) {
            cell.append(".");
        }

        // 1.5.2 - Float Converter
        while (floats.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal product = floats.multiply(BigDecimal.valueOf(base));
            BigInteger floor = product.toBigInteger();
            if (floor.compareTo(BigInteger.TEN) >= 0) {
                cell.append((char) (floor.intValue() + 55)); // Convert to character for bases > 10
            } else {
                cell.append(floor.toString());
            }
            table3.add(new TableData3(floats.stripTrailingZeros(), base, product.stripTrailingZeros(), floor));
            floats = product.subtract(new BigDecimal(floor)).setScale(floats.scale(), RoundingMode.HALF_UP);
        }

        // Remove any leading zeros
        String cellString = cell.toString().replaceAll("^0+(?!$)", "");
        String[] res2 = splitString(cell.toString());
        this.result2 = res2[1];
        return cellString.isEmpty() ? "0" : cellString;
    }


    // 1.6 - Clear Tables
    public void clearTables() {
        table1.clear();
        table2.clear();
        table3.clear();
    }

    public String getSum() {
        return sum;
    }

    public String getResult1() {
        return result1;
    }

    public String getResult2() {
        return result2;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public static void main(String[] args) {
        launch();
    }

    // 1.7 - Table Data (To Decimal Conversion Steps)
    public static class TableData1 {
        private StringProperty operation;
        private StringProperty product;

        public TableData1(char digit, int base, int power, String product) {
            String value;
            if (Character.isLetter(digit)) {
                value = digit + " (" + (Character.toUpperCase(digit) - 55) + ")";
            } else {
                value = Character.toString(digit);
            }
            this.operation = new SimpleStringProperty(value + " * " + base + "^" + power + " =");
            this.product = new SimpleStringProperty(product);
        }

        public StringProperty getOperation() {
            return operation;
        }

        public StringProperty getProduct() {
            return product;
        }
    }

    // 1.8 - Table Data (From Decimal Conversion Steps - Integrals)
    public static class TableData2 {
        private StringProperty operation;
        private StringProperty quotient;
        private StringProperty remainder;

        public TableData2(BigInteger decimal, int base, BigInteger quotient, BigInteger remainder) {
            String value;
            if (remainder.compareTo(BigInteger.TEN) >= 0) {
                value = remainder + " (" + (char) (remainder.intValue() + 55) + ")";
            } else {
                value = remainder.toString();
            }
            this.operation = new SimpleStringProperty(decimal + " / " + base + " =");
            this.quotient = new SimpleStringProperty(quotient.toString());
            this.remainder = new SimpleStringProperty(value);
        }

        public StringProperty getOperation() {
            return operation;
        }

        public StringProperty getQuotient() {
            return quotient;
        }

        public StringProperty getRemainder() {
            return remainder;
        }
    }

    // 1.9 - Table Data (From Decimal Conversion Steps - Floats)
    public static class TableData3 {
        private StringProperty operation;
        private StringProperty product;
        private StringProperty integral;

        public TableData3(BigDecimal decimal, int base, BigDecimal product, BigInteger integral) {
            String value;
            if (integral.compareTo(BigInteger.valueOf(9)) > 0) {
                value = integral.toString() + " (" + (char) (integral.intValue() + 55) + ")";
            } else {
                value = integral.toString();
            }
            this.operation = new SimpleStringProperty(decimal + " * " + base + " =");
            this.product = new SimpleStringProperty(product.toString());
            this.integral = new SimpleStringProperty(value);
        }

        public StringProperty getOperation() {
            return operation;
        }

        public StringProperty getProduct() {
            return product;
        }

        public StringProperty getIntegral() {
            return integral;
        }
    }
}

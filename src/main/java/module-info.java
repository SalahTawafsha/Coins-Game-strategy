module com.example.coinsgamestrategy {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.coinsgamestrategy to javafx.fxml;
    exports com.example.coinsgamestrategy;
}
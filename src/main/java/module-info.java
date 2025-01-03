module com.example.robotsim {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.robotsim to javafx.fxml;
    exports com.example.robotsim;
}
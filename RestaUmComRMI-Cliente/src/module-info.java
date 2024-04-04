module RestaUm {
	requires javafx.controls;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.base;
	requires java.rmi;
	
	opens application to javafx.graphics, javafx.fxml;
	exports application to java.rmi;
}

package controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Mensagem {

    private Mensagem() {
    }

    public static void erro(String mensagem, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.YES);
        alert.setTitle("Erro");
        alert.setHeaderText(mensagem);
        alert.showAndWait();
    }

    public static void sucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensagem, ButtonType.YES);
        alert.setTitle("Sucesso");
        alert.setHeaderText("Sucesso ao salvar!");
        alert.showAndWait();
    }
    
    public static void aviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensagem, ButtonType.YES);
        alert.setHeaderText("Aviso!");
        alert.showAndWait();
    }
}

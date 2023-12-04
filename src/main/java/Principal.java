import controllers.Conexao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Principal extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/views/Pdv.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ponto de Venda");
            Conexao.conectar();
            primaryStage.show();
        } catch (Exception ex) {
            ex.getStackTrace();
            ex.getMessage();
            System.out.println("erro:" + ex);
        }
    }

}

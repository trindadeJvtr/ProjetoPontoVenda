package controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import models.ModelItemVenda;
import models.ModelVendedor;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ListaProdutoController implements Initializable {

    @FXML
    private Button btn_sair;

    @FXML
    private Button btn_selecionar;

    @FXML
    private TableView<ModelItemVenda> tb_produto;

    @FXML
    private TableColumn<ModelItemVenda, String> tb_produto_descricao;

    @FXML
    private TableColumn<ModelItemVenda, Integer> tb_produto_id;

    @FXML
    private TableColumn<ModelItemVenda, Double> tb_produto_quantidade;

    @FXML
    private TableColumn<ModelItemVenda, Double> tb_produto_valor;

    private int codRetorno;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btn_selecionar.setOnAction(event -> handleSelecionar());
        btn_sair.setOnAction(event -> handleSair());

        tb_produto_id.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getCodigo()));
        tb_produto_descricao.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDescricao()));
        tb_produto_quantidade.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getQuantidade()));
        tb_produto_valor.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValor()));

        atualizaTabela();
    }

    private void handleSelecionar() {
        if(tb_produto.getSelectionModel() == null){
            Mensagem.aviso("Nada selecionado.");
            return;
        }

        codRetorno = tb_produto.getSelectionModel().getSelectedItem().getCodigo();
        handleSair();
    }

    private void handleSair() {
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        stage.close();
        Conexao.fechar();
    }

    private void atualizaTabela() {

        tb_produto.getItems().clear();
        try {
            List<ModelItemVenda> lista = new ArrayList<>();
            try (PreparedStatement statement = Conexao.get("SELECT * FROM produto")) {
                try (ResultSet resultset = statement.executeQuery()) {
                    while (resultset.next()) {
                        ModelItemVenda itemVenda = new ModelItemVenda();

                        itemVenda.setCodigo(resultset.getInt("id"));
                        itemVenda.setDescricao(resultset.getString("descricao"));
                        itemVenda.setQuantidade(resultset.getDouble("quantidade"));
                        itemVenda.setValor(resultset.getDouble("valor"));
                        lista.add(itemVenda);
                    }
                }
            } catch (Exception ex) {
                Mensagem.erro("Erro atualizar tabela: ", ex);
                System.out.println(ex.getMessage());
                System.out.println(Arrays.toString(ex.getStackTrace()));
            }

            tb_produto.getItems().addAll(lista);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public int showAndWaitRetorno(Stage stage) {
        stage.showAndWait();
        return codRetorno;
    }
}

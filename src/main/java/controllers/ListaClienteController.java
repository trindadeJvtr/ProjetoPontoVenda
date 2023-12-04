package controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.ModelCliente;
import models.ModelItemVenda;
import models.ModelVendedor;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ListaClienteController implements Initializable {


    @FXML
    private Button btn_alterar;

    @FXML
    private Button btn_excluir;

    @FXML
    private Button btn_selecionar;

    @FXML
    private Button btn_sair;

    @FXML
    private TableView<ModelCliente> tb_clientes;

    @FXML
    private TableColumn<ModelCliente, Integer> tb_clientes_id_cliente;

    @FXML
    private TableColumn<ModelCliente, String> tb_clientes_nome;

    int codRetorno;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        btn_selecionar.setVisible(false);
        btn_alterar.setVisible(false);
        btn_excluir.setVisible(false);

        btn_selecionar.setOnAction(event -> handleSelecionar());
        btn_sair.setOnAction(event -> handleSair());

        tb_clientes_id_cliente.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getCodigo()));
        tb_clientes_nome.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getNome()));

        atualizaTabela();
    }

    private void handleSelecionar() {
        if(tb_clientes.getSelectionModel() == null){
            Mensagem.aviso("Nada selecionado.");
            return;
        }

        codRetorno = tb_clientes.getSelectionModel().getSelectedItem().getCodigo();
        handleSair();
    }

    private void handleSair() {
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        stage.close();
        Conexao.fechar();
    }

    private void atualizaTabela() {

        tb_clientes.getItems().clear();
        try (PreparedStatement statement = Conexao.get("SELECT * FROM clientes")) {
            List<ModelCliente> lista = new ArrayList<>();
            try (ResultSet resultset = statement.executeQuery()) {
                while (resultset.next()) {
                    ModelCliente itemVenda = new ModelCliente();

                    itemVenda.setCodigo(resultset.getInt("id"));
                    itemVenda.setNome(resultset.getString("nome"));
                    lista.add(itemVenda);
                }
            }
            tb_clientes.getItems().addAll(lista);

        } catch (Exception ex) {
            Mensagem.erro("Erro atualizar tabela.", ex);
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    public int showAndWaitRetorno(Stage stage) {
        stage.showAndWait();
        return codRetorno;
    }
}

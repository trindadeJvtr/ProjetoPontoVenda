package controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.ModelItemVenda;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PdvController implements Initializable {

    @FXML
    private Button btn_cliente;

    @FXML
    private Button btn_finalizarVenda;

    @FXML
    private Button btn_vendedor;

    @FXML
    private Label lb_total;

    @FXML
    private TableView<ModelItemVenda> tb_item;

    @FXML
    private TableColumn<ModelItemVenda, Integer> tb_item_codigo;

    @FXML
    private TableColumn<ModelItemVenda, String> tb_item_nome;

    @FXML
    private TableColumn<ModelItemVenda, Double> tb_item_valor;

    @FXML
    private TableColumn<ModelItemVenda, Double> tb_item_qtd;

    @FXML
    private TextField tf_cliente;

    @FXML
    private TextField tf_produto;

    @FXML
    private TextField tf_vendedor;

    @FXML
    private TextField tf_quantidade;

    @FXML
    private ImageView img_venda;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tf_quantidade.requestFocus();
        btn_cliente.setOnAction(event -> handleSelecionaCliente());
        btn_vendedor.setOnAction(event -> handleSelecionaVendedor());
        btn_finalizarVenda.setOnAction(event -> handleFinalizaVenda());

        tb_item_codigo.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getCodigo()));
        tb_item_nome.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDescricao()));
        tb_item_qtd.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getQuantidade()));
        tb_item_valor.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValor()));

        tf_produto.setOnAction(event -> buscaProduto());
        tf_produto.setText("");
        tf_cliente.setText("");
        tf_cliente.setEditable(false);
        tf_vendedor.setText("");
        tf_vendedor.setEditable(false);
        tf_quantidade.setText("1");
        tf_quantidade.setOnAction(event -> tf_produto.requestFocus());
        tb_item.setFocusTraversable(false);
        carregarImagem();
        tf_quantidade.requestFocus();
    }

    private void carregarImagem() {
        File arquivoSelecionado = new File("/views/imageVenda.png");

        if (arquivoSelecionado != null) {
            // Carrega a imagem no ImageView
            Image imagem = new Image(arquivoSelecionado.toURI().toString());
            img_venda.setImage(imagem);
        }
    }

    private void handleFinalizaVenda() {
        tb_item.getItems().clear();
        lb_total.setText("0,00");


        Mensagem.sucesso("Sucesso ao enviar nota.");
    }


    private void handleSelecionaVendedor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ListaVendedor.fxml"));
            Stage cadastro = new Stage();
            cadastro.setScene(new Scene(loader.load()));
            cadastro.setTitle("Lista Vendedor");

            ListaVendedorController listaVendedorController = loader.getController();
            int codigo = listaVendedorController.showAndWaitRetorno(cadastro);

            selectVendedor(codigo);
        } catch (IOException ex) {
            Mensagem.erro("Erro: ", ex);
        }
    }

    private void selectVendedor(int codigo) {
        try (PreparedStatement statement = Conexao.get("SELECT * FROM vendedores WHERE id = ?;")) {
            statement.setObject(1, codigo);
            try (ResultSet resultset = statement.executeQuery()) {
                while (resultset.next()) {
                    tf_vendedor.setText(resultset.getInt("id") + " - " + resultset.getString("nome"));
                }
            }
        } catch (Exception ex) {
            Mensagem.erro("Erro atualizar tabela: ", ex);
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    private void selectCliente(int codigo) {
        try (PreparedStatement statement = Conexao.get("SELECT * FROM clientes WHERE id = ?;")) {
            statement.setObject(1, codigo);
            try (ResultSet resultset = statement.executeQuery()) {
                while (resultset.next()) {
                    tf_cliente.setText(resultset.getInt("id") + " - " + resultset.getString("nome"));
                }
            }
        } catch (Exception ex) {
            Mensagem.erro("Erro atualizar tabela: ", ex);
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void buscaProduto() {

        if (tf_cliente.getText().isEmpty() || tf_vendedor.getText().isEmpty()) {
            Mensagem.aviso("Cliente e Vendedor precisam estar preenchidos.");
            return;
        }

        String codProduto = tf_produto.getText();

        if (codProduto.isEmpty() || !codProduto.matches("\\d+")) {
            return;
        }

        String quantidade = tf_quantidade.getText();

        if (!isDouble(quantidade)) {
            return;
        }

        try {
            List<ModelItemVenda> lista = new ArrayList<>();
            try (PreparedStatement statement = Conexao.get("SELECT * FROM produto WHERE id = ?;")) {
                statement.setObject(1, Integer.parseInt(tf_produto.getText()));
                try (ResultSet resultset = statement.executeQuery()) {
                    while (resultset.next()) {
                        ModelItemVenda itemVenda = new ModelItemVenda();

                        itemVenda.setCodigo(resultset.getInt("id"));
                        itemVenda.setDescricao(resultset.getString("descricao"));
                        itemVenda.setQuantidade(Double.parseDouble(quantidade));
                        itemVenda.setValor(resultset.getInt("valor"));
                        lista.add(itemVenda);
                    }
                }
            } catch (Exception ex) {
                Mensagem.erro("Erro atualizar tabela: ", ex);
                System.out.println(ex.getMessage());
                System.out.println(Arrays.toString(ex.getStackTrace()));
            }

            tb_item.getItems().addAll(lista);
        } catch (Exception e) {
            System.out.println(e);
        }

        Double valorTotal = 0.0;

        for (ModelItemVenda a : tb_item.getItems()) {

            valorTotal = valorTotal + (a.getValor() * a.getQuantidade());

            lb_total.setText(String.format("R$%.2f", valorTotal));
        }

        tf_produto.setText("");
        tf_quantidade.setText("1");
    }

    private void handleSelecionaCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ListaClientes.fxml"));
            Stage cadastro = new Stage();
            cadastro.setScene(new Scene(loader.load()));
            cadastro.setTitle("Lista Clientes");

            ListaClienteController listaClienteController = loader.getController();
            int codigo = listaClienteController.showAndWaitRetorno(cadastro);

            selectCliente(codigo);
        } catch (IOException ex) {
            Mensagem.erro("Erro: ", ex);
        }
    }
}

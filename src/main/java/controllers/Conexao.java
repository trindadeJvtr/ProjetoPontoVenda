package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author trindade
 */
public class Conexao {

    static Connection connection;
//    try(PreparedStatement statement = controllers.Conexao.get("query")){
//        try(ResultSet resultset = statement.executeQuery()){
//            while(resultset){
//                
//            }
//        }
//    }   
    
     public static PreparedStatement getInserts(String query, String colunaPrimaria) throws Exception {

        try {
            if(connection.isClosed()){
                conectar();
                System.out.println("abriu");
            }
            
            return connection.prepareStatement(query, new String[] { colunaPrimaria });
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public static PreparedStatement get(String query) throws Exception {

        try {
            if(connection.isClosed()){
                conectar();
                System.out.println("abriu");
            }
            
            return connection.prepareStatement(query);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public static void conectar() throws Exception {
        String jdbcUrl = "jdbc:mysql://localhost:3307/pontodevenda";
        String username = "root";
//        String password = "ky$14gr@";
        String password = "";

        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (Exception e) {
            throw new Exception("Erro de conexão com a base de dados: " + e);
        }
    }

    public static void begin() throws SQLException {
        connection.setAutoCommit(false);
    }

    public static void commit() throws SQLException {
        connection.commit();
    }

    public static void rollback() {
        try {
            connection.rollback();
        } catch (Exception ex) {
            Mensagem.erro(ex.getMessage(), ex);
        }
    }

    public static void fechar() {
        try {
            connection.close();
        } catch (Exception ex) {
            Mensagem.erro(ex.getMessage(), ex);
        }
    }
}

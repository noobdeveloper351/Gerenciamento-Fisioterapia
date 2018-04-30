package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Fisioterapeuta extends Pessoa{
	
	Logger logger = Logger.getLogger(Fisioterapeuta.class.getName());
	
	private long crefito;
	
	public Fisioterapeuta(String nome, String nascimento, String sexo, String email, long telefone, long celular, long rg,
			long cpf, String rua, long numero, String bairro, String complemento, long cep, String uf,
			long crefito) {
		super(nome, nascimento, sexo, email, telefone, celular, rg, cpf, rua, numero, bairro, complemento, cep, uf);
		this.setCrefito(crefito);
	}
	
	//Construtor com apenas os valores obrigatórios
	public Fisioterapeuta(String nome, long crefito) {
		this.setNome(nome);
		this.setCrefito(crefito);
	}
	
	@Override 
	public void printData() {
		super.printData();
		System.out.println(crefito);
	}
	
	@Override
	public void printEntry() {
		sqlite_connect sq = new sqlite_connect();
		sq.select_from("fisioterapiaSUS.db", "fisioterapeuta", "*");
	}
	
	@Override
	public void saveData() {
		
		try(Connection conn = DriverManager.getConnection("jdbc:sqlite:" + "fisioterapiaSUS.db")){
			
			@SuppressWarnings("unused")
			ResultSet rs = null;
			//Importante para a transction funcionar
			conn.setAutoCommit(false);
			
			
			String endereco = "INSERT INTO endereco (rua, numero, bairro, complemento, cep, uf) "
					+ "VALUES (?, ?, ?, ?, ?, ?);";
			String contato = "INSERT INTO contato (email, telefone, celular) "
					+ "VALUES (?, ?, ?);";
			String dados_pessoais = "INSERT INTO dados_pessoais (nome, nascimento, sexo, rg, cpf, endereco_id, contato_id) "
					+ "VALUES (?, ?, ?, ?, ?, (SELECT MAX(endereco_id) FROM endereco), (SELECT MAX(contato_id) FROM contato));";
			String fisioterapeuta = "INSERT INTO fisioterapeuta (crefito, pessoa_id) "
					+ "VALUES (?, (SELECT cpf FROM dados_pessoais WHERE dados_pessoais_id = (SELECT MAX(dados_pessoais_id) FROM dados_pessoais)));";
			
			//Insert endereco
			PreparedStatement pst1 = conn.prepareStatement(endereco, Statement.RETURN_GENERATED_KEYS);
			pst1.setString(1, rua);
			pst1.setLong(2, numero);
			pst1.setString(3, bairro);
			pst1.setString(4, complemento);
			pst1.setLong(5, cep);
			pst1.setString(6, uf);
			
			checkInsert(conn, pst1);
			
			//Insert contato
			PreparedStatement pst2 = conn.prepareStatement(contato, Statement.RETURN_GENERATED_KEYS);
			pst2.setString(1, email);
			pst2.setLong(2, telefone);
			pst2.setLong(3, celular);
			
			checkInsert(conn, pst2);
			
			//Insert dados_pessoais
			PreparedStatement pst3 = conn.prepareStatement(dados_pessoais, Statement.RETURN_GENERATED_KEYS);
			pst3.setString(1, nome);
			pst3.setString(2, nascimento);
			pst3.setString(3, sexo);
			pst3.setLong(4, rg);
			pst3.setLong(5, cpf);
			
			checkInsert(conn, pst3);
			
			//Insert Fisioterapeuta
			PreparedStatement pst4 = conn.prepareStatement(fisioterapeuta, Statement.RETURN_GENERATED_KEYS);
			pst4.setLong(1, crefito);
			
			pst4.executeUpdate();
			
			checkInsert(conn, pst4);
			
		}catch(SQLException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	public long getCrefito() {
		return crefito;
	}
	
	public void setCrefito(long crefito) {
		this.crefito = crefito;
	}
}

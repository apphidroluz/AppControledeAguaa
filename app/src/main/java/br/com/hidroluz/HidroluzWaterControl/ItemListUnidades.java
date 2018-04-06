package br.com.hidroluz.HidroluzWaterControl;

public class ItemListUnidades {
	private String bloco;
	private String id_unidade;
	private String login;
	private String razsoc_nome;
	private String codigo;
	private String unidade;
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	public ItemListUnidades(String bloco, String id_unidade, String login,
			String razsoc_nome, String unidade, String email) {
		super();
		this.bloco = bloco;
		this.id_unidade = id_unidade;
		this.login = login;
		this.razsoc_nome = razsoc_nome;
		this.unidade = unidade;
		this.email = email;
	}

	public ItemListUnidades(String bloco, String id_unidade, String login,
			String razsoc_nome, String codigo, String unidade, String email) {
		super();
		this.bloco = bloco;
		this.id_unidade = id_unidade;
		this.login = login;
		this.razsoc_nome = razsoc_nome;
		this.codigo = codigo;
		this.unidade = unidade;
		this.email = email;
	}

	public String getId_unidade() {
		return this.id_unidade;
	}

	public void setId_unidade(String id_unidade) {
		this.id_unidade = id_unidade;
	}

	public String getBloco() {
		return this.bloco;
	}

	public void setBloco(String bloco) {
		this.bloco = bloco;
	}

	public String getUnidade() {
		return this.unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getRazsoc_nome() {
		return this.razsoc_nome;
	}

	public void setRazsoc_nome(String razsoc_nome) {
		this.razsoc_nome = razsoc_nome;
	}

	public ItemListUnidades(String razsoc_nome, String bloco, String unidade,
			String codigo) {
		this.razsoc_nome = razsoc_nome;
		this.bloco = bloco;
		this.unidade = unidade;
		this.codigo = codigo;
	}
}

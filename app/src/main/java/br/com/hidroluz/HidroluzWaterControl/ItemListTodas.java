package br.com.hidroluz.HidroluzWaterControl;

public class ItemListTodas {

	private Integer Consumo;
	private String data;
	private String data_anterior;
	private String dias;
	private String mesano;

	public ItemListTodas() {
		super();
	}

	public ItemListTodas(Integer consumo, String data, String dias,
			String data_anterior, String mesano) {
		super();
		Consumo = consumo;
		this.data = data;
		this.dias = dias;
		this.data_anterior = data_anterior;
		this.mesano = mesano;
	}

	public Integer getConsumo() {
		return Consumo;
	}

	public void setConsumo(Integer consumo) {
		Consumo = consumo;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData_anterior() {
		return data_anterior;
	}

	public void setData_anterior(String data_anterior) {
		this.data_anterior = data_anterior;
	}

	public String getDias() {
		return dias;
	}

	public void setDias(String dias) {
		this.dias = dias;
	}

	public String getMesano() {
		return mesano;
	}

	public void setMesano(String mesano) {
		this.mesano = mesano;
	}

}

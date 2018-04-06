package br.com.hidroluz.HidroluzWaterControl;

public class ItemListLeituras {
    private Integer Consumo;
    private String data;
    private String data_anterior;
    private String dias;
    private String fluxo_continuo;
    private String mesano;
    private Double valorrateio;
    private Double valortotal;
    
    

    public ItemListLeituras(Integer consumo, String data, String data_anterior,
			String dias, String mesano) {
		super();
		this.Consumo = consumo;
		this.data = data;
		this.data_anterior = data_anterior;
		this.dias = dias;
		this.mesano = mesano;
	}

	public ItemListLeituras(Integer consumo, String data, Double valortotal, Double valorrateio, String dias, String data_anterior, String mesano) {
        this.Consumo = consumo;
        this.data = data;
        this.valortotal = valortotal;
        this.valorrateio = valorrateio;
        this.dias = dias;
        this.data_anterior = data_anterior;
        this.mesano = mesano;
    }

    public ItemListLeituras(Integer consumo, String data, Double valortotal, Double valorrateio, String dias, String data_anterior, String mesano, String fluxo_continuo) {
        this.Consumo = consumo;
        this.data = data;
        this.valortotal = valortotal;
        this.valorrateio = valorrateio;
        this.dias = dias;
        this.data_anterior = data_anterior;
        this.mesano = mesano;
        this.fluxo_continuo = fluxo_continuo;
    }

    public String getFluxo_continuo() {
        return this.fluxo_continuo;
    }

    public void setFluxo_continuo(String fluxo_continuo) {
        this.fluxo_continuo = fluxo_continuo;
    }

    public String getMesano() {
        return this.mesano;
    }

    public void setMesano(String mesano) {
        this.mesano = mesano;
    }

    public String getData_anterior() {
        return this.data_anterior;
    }

    public void setData_anterior(String data_anterior) {
        this.data_anterior = data_anterior;
    }

    public Integer getConsumo() {
        return this.Consumo;
    }

    public void setConsumo(Integer consumo) {
        this.Consumo = consumo;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Double getValortotal() {
        return this.valortotal;
    }

    public void setValortotal(Double valortotal) {
        this.valortotal = valortotal;
    }

    public Double getValorrateio() {
        return this.valorrateio;
    }

    public void setValorrateio(Double valorrateio) {
        this.valorrateio = valorrateio;
    }

    public String getDias() {
        return this.dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }
}

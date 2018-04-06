package br.com.hidroluz.HidroluzWaterControl;

public class ItemListConsumo {
    private int consumo;
    private int indantigo;
    private int indatual;
    private String localizacao;

    public ItemListConsumo(String localizacao, int indatual, int indantigo, int consumo) {
        this.localizacao = localizacao;
        this.indatual = indatual;
        this.indantigo = indantigo;
        this.consumo = consumo;
    }

    public int getIndatual() {
        return this.indatual;
    }

    public void setIndatual(int indatual) {
        this.indatual = indatual;
    }

    public int getIndantigo() {
        return this.indantigo;
    }

    public void setIndantigo(int indantigo) {
        this.indantigo = indantigo;
    }

    public int getConsumo() {
        return this.consumo;
    }

    public void setConsumo(int consumo) {
        this.consumo = consumo;
    }

    public String getLocalizacao() {
        return this.localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}

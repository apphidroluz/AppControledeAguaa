package br.com.hidroluz.HidroluzWaterControl;

public class ItemListMenu {
    public int icon;
    public String nome;

    public ItemListMenu(String nome, int icon) {
        this.nome = nome;
        this.icon = icon;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}

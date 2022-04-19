package com.frf.app.data;

public class RankingData {

    private int id;
    private Integer img;
    private String titulo;
    private String subtitulo;
    private String total;

    public RankingData(int id, Integer img, String titulo, String subtitulo, String total) {
        this.id = id;
        this.img = img;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

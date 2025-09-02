package br.com.a3.gp.modelo;

public class Projeto {
    private Integer id;
    private String nome;
    private String descricao;
    private String dataInicio;
    private String dataPrevFim;
    private String status = "planejado";
    private Integer gerenteId;
    private boolean ativo = true;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }
    public String getDataPrevFim() { return dataPrevFim; }
    public void setDataPrevFim(String dataPrevFim) { this.dataPrevFim = dataPrevFim; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getGerenteId() { return gerenteId; }
    public void setGerenteId(Integer gerenteId) { this.gerenteId = gerenteId; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override public String toString() { return nome; }
}

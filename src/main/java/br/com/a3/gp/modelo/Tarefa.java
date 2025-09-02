package br.com.a3.gp.modelo;

public class Tarefa {
    private Integer id;
    private Integer projetoId;
    private Integer responsavelId;
    private String titulo;
    private String descricao;
    private String status = "em_execucao";
    private String prioridade = "media";
    private String dataCriacao;
    private String dataPrevisao;
    private String dataConclusao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProjetoId() { return projetoId; }
    public void setProjetoId(Integer projetoId) { this.projetoId = projetoId; }
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }
    public String getDataPrevisao() { return dataPrevisao; }
    public void setDataPrevisao(String dataPrevisao) { this.dataPrevisao = dataPrevisao; }
    public String getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(String dataConclusao) { this.dataConclusao = dataConclusao; }
}

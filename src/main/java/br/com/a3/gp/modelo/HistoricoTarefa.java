package br.com.a3.gp.modelo;

public class HistoricoTarefa {
    private Integer id;
    private Integer tarefaId;
    private Integer usuarioId;
    private String acao;
    private String observacao;
    private String dataRegistro;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTarefaId() { return tarefaId; }
    public void setTarefaId(Integer tarefaId) { this.tarefaId = tarefaId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public String getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(String dataRegistro) { this.dataRegistro = dataRegistro; }
}

package br.com.a3.gp.modelo;

/**
 * Representa um registro de histórico de uma tarefa no sistema.
 * Cada ação tomada sobre uma tarefa (ex.: envio para QA, aprovação, reprovação)
 * é registrada com quem fez, quando fez e uma observação (se houver).
 */
public class HistoricoTarefa {

    // Identificador único do histórico (chave primária)
    private Integer id;

    // ID da tarefa associada (chave estrangeira para a tabela tarefa)
    private Integer tarefaId;

    // ID do usuário que executou a ação (pode ser null em ações automáticas)
    private Integer usuarioId;

    // Ação realizada: exemplos comuns — enviar_para_qa, aprovar_qa, reprovar_qa
    private String acao;

    // Observação opcional sobre a ação (ex.: motivo da reprovação)
    private String observacao;

    // Data e hora do registro (padrão: CURRENT_TIMESTAMP no banco)
    private String dataRegistro;

    // Getters e Setters

    /** @return ID do registro de histórico */
    public Integer getId() {
        return id;
    }

    /** @param id define o identificador do histórico */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return ID da tarefa vinculada a este histórico */
    public Integer getTarefaId() {
        return tarefaId;
    }

    /** @param tarefaId define qual tarefa está sendo referenciada */
    public void setTarefaId(Integer tarefaId) {
        this.tarefaId = tarefaId;
    }

    /** @return ID do usuário que realizou a ação (ou null) */
    public Integer getUsuarioId() {
        return usuarioId;
    }

    /** @param usuarioId define o usuário que executou a ação */
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    /** @return Ação registrada (ex: aprovar_qa) */
    public String getAcao() {
        return acao;
    }

    /** @param acao define a ação realizada na tarefa */
    public void setAcao(String acao) {
        this.acao = acao;
    }

    /** @return Observação associada à ação (opcional) */
    public String getObservacao() {
        return observacao;
    }

    /** @param observacao define um comentário adicional sobre a ação */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    /** @return Data e hora em que a ação foi registrada */
    public String getDataRegistro() {
        return dataRegistro;
    }

    /** @param dataRegistro define a data/hora do registro (geralmente preenchido pelo banco) */
    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
}

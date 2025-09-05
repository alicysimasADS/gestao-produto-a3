package br.com.a3.gp.modelo;

/**
 * Representa uma tarefa vinculada a um projeto no sistema de gestão.
 * Cada tarefa pode ter um responsável, status, prioridade e datas associadas ao seu ciclo de vida.
 */
public class Tarefa {

    // Identificador único da tarefa (chave primária no banco de dados)
    private Integer id;

    // ID do projeto ao qual esta tarefa pertence (FK para projeto.id)
    private Integer projetoId;

    // ID do usuário responsável pela tarefa (FK para usuario.id — pode ser null)
    private Integer responsavelId;

    // Título descritivo da tarefa
    private String titulo;

    // Descrição detalhada ou observações da tarefa
    private String descricao;

    // Status atual da tarefa. Valor padrão: "em_execucao"
    private String status = "em_execucao"; // outros valores possíveis: em_qa, aprovado, reprovado

    // Prioridade da tarefa. Valor padrão: "media"
    private String prioridade = "media"; // outros valores possíveis: alta, baixa

    // Data de criação da tarefa (gerada automaticamente pelo banco)
    private String dataCriacao;

    // Data prevista para conclusão (estimativa)
    private String dataPrevisao;

    // Data em que a tarefa foi de fato concluída
    private String dataConclusao;

    // Getters e Setters

    /** @return ID da tarefa */
    public Integer getId() {
        return id;
    }

    /** @param id define o identificador único da tarefa */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return ID do projeto ao qual esta tarefa pertence */
    public Integer getProjetoId() {
        return projetoId;
    }

    /** @param projetoId define o projeto vinculado a esta tarefa */
    public void setProjetoId(Integer projetoId) {
        this.projetoId = projetoId;
    }

    /** @return ID do usuário responsável (pode ser null) */
    public Integer getResponsavelId() {
        return responsavelId;
    }

    /** @param responsavelId define o usuário responsável (pode ser null) */
    public void setResponsavelId(Integer responsavelId) {
        this.responsavelId = responsavelId;
    }

    /** @return Título da tarefa */
    public String getTitulo() {
        return titulo;
    }

    /** @param titulo define o título da tarefa */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /** @return Descrição detalhada da tarefa */
    public String getDescricao() {
        return descricao;
    }

    /** @param descricao define a descrição da tarefa */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /** @return Status atual da tarefa */
    public String getStatus() {
        return status;
    }

    /** @param status define o status atual da tarefa (em_execucao, em_qa, etc.) */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return Prioridade da tarefa */
    public String getPrioridade() {
        return prioridade;
    }

    /** @param prioridade define a prioridade (baixa, media, alta) */
    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    /** @return Data em que a tarefa foi criada */
    public String getDataCriacao() {
        return dataCriacao;
    }

    /** @param dataCriacao define a data de criação da tarefa */
    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    /** @return Data prevista para a conclusão da tarefa */
    public String getDataPrevisao() {
        return dataPrevisao;
    }

    /** @param dataPrevisao define a data esperada de finalização */
    public void setDataPrevisao(String dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    /** @return Data real em que a tarefa foi concluída */
    public String getDataConclusao() {
        return dataConclusao;
    }

    /** @param dataConclusao define a data de conclusão (finalização real) */
    public void setDataConclusao(String dataConclusao) {
        this.dataConclusao = dataConclusao;
    }
}

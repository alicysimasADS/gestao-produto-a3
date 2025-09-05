package br.com.a3.gp.modelo;

/**
 * Representa um projeto dentro do sistema.
 * Cada projeto possui dados básicos como nome, descrição, datas, status e gerente responsável.
 * É possível ativar ou desativar projetos ao longo do ciclo de vida.
 */
public class Projeto {

    // Identificador único do projeto (chave primária no banco)
    private Integer id;

    // Nome do projeto (obrigatório e único)
    private String nome;

    // Descrição opcional do projeto
    private String descricao;

    // Data de início do projeto (em formato string ISO, ex: yyyy-MM-dd)
    private String dataInicio;

    // Data prevista para conclusão (estimativa)
    private String dataPrevFim;

    // Status atual do projeto. Valor padrão: "planejado"
    private String status = "planejado";

    // ID do gerente do projeto (chave estrangeira para usuario.id)
    private Integer gerenteId;

    // Indica se o projeto está ativo (padrão: true)
    private boolean ativo = true;

    // Getters e setters

    /** @return ID do projeto */
    public Integer getId() {
        return id;
    }

    /** @param id define o identificador do projeto */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return Nome do projeto */
    public String getNome() {
        return nome;
    }

    /** @param nome define o nome do projeto */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /** @return Descrição textual do projeto */
    public String getDescricao() {
        return descricao;
    }

    /** @param descricao define a descrição detalhada do projeto */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /** @return Data de início (formato string) */
    public String getDataInicio() {
        return dataInicio;
    }

    /** @param dataInicio define a data de início planejada */
    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    /** @return Data prevista para finalização */
    public String getDataPrevFim() {
        return dataPrevFim;
    }

    /** @param dataPrevFim define a data estimada de conclusão */
    public void setDataPrevFim(String dataPrevFim) {
        this.dataPrevFim = dataPrevFim;
    }

    /** @return Status atual do projeto (planejado, em andamento, finalizado, etc.) */
    public String getStatus() {
        return status;
    }

    /** @param status define o status atual do projeto */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return ID do gerente vinculado ao projeto */
    public Integer getGerenteId() {
        return gerenteId;
    }

    /** @param gerenteId define o ID do usuário responsável pelo projeto */
    public void setGerenteId(Integer gerenteId) {
        this.gerenteId = gerenteId;
    }

    /** @return true se o projeto estiver ativo */
    public boolean isAtivo() {
        return ativo;
    }

    /** @param ativo define se o projeto está ativo ou inativo */
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * Sobrescreve o método toString para retornar apenas o nome do projeto.
     * Isso permite que o projeto seja exibido diretamente em ComboBox ou listas.
     * @return nome do projeto
     */
    @Override
    public String toString() {
        return nome;
    }
}

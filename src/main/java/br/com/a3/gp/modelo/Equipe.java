package br.com.a3.gp.modelo;

/**
 * Classe que representa uma equipe dentro do sistema de gestão de projetos.
 * Cada equipe possui um ID único, um nome e uma descrição opcional.
 */
public class Equipe {

    // Identificador único da equipe (chave primária no banco de dados)
    private Integer id;

    // Nome da equipe (obrigatório)
    private String nome;

    // Descrição adicional da equipe (opcional)
    private String descricao;

    // Getters e setters padrão

    /**
     * Retorna o ID da equipe.
     * @return Integer id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Define o ID da equipe.
     * @param id identificador único da equipe
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retorna o nome da equipe.
     * @return String nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da equipe.
     * @param nome nome da equipe
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna a descrição da equipe.
     * @return String descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define a descrição da equipe.
     * @param descricao texto explicativo sobre a equipe
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Sobrescreve o método toString() para retornar o nome da equipe.
     * Isso é útil, por exemplo, quando a equipe é exibida em um ComboBox.
     * @return nome da equipe
     */
    @Override
    public String toString() {
        return nome;
    }
}

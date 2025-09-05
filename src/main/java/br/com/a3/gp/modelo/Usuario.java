package br.com.a3.gp.modelo;

/**
 * Representa um usuário do sistema.
 * Cada usuário possui dados básicos de autenticação, identidade e cargo/perfil organizacional.
 */
public class Usuario {

    // Identificador único do usuário (chave primária no banco)
    private Integer id;

    // Nome completo do usuário
    private String nome;

    // Nome de login (utilizado na autenticação)
    private String login;

    // Hash da senha (gerado com BCrypt, nunca armazena senha em texto puro)
    private String senhaHash;

    // Indica se o usuário está ativo (padrão: true)
    private boolean ativo = true;

    // CPF do usuário (campo opcional)
    private String cpf;

    // Endereço de e-mail do usuário (campo opcional)
    private String email;

    // Cargo ou função ocupada (ex: Desenvolvedor, QA, Coordenador)
    private String cargo;

    // Perfil ou papel organizacional (ex: ADMIN, GERENTE_PROJETO, etc.)
    private String perfil;

    // ----------------------------
    // Getters e Setters
    // ----------------------------

    /** @return ID do usuário */
    public Integer getId() {
        return id;
    }

    /** @param id define o identificador do usuário */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return Nome completo do usuário */
    public String getNome() {
        return nome;
    }

    /** @param nome define o nome do usuário */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /** @return Login utilizado para autenticação */
    public String getLogin() {
        return login;
    }

    /** @param login define o nome de usuário utilizado no login */
    public void setLogin(String login) {
        this.login = login;
    }

    /** @return Hash da senha do usuário */
    public String getSenhaHash() {
        return senhaHash;
    }

    /** @param senhaHash define o hash da senha (gerado com BCrypt) */
    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    /** @return true se o usuário estiver ativo no sistema */
    public boolean isAtivo() {
        return ativo;
    }

    /** @param ativo define o status ativo/inativo do usuário */
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    /** @return CPF do usuário (opcional) */
    public String getCpf() {
        return cpf;
    }

    /** @param cpf define o CPF do usuário */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /** @return E-mail de contato do usuário */
    public String getEmail() {
        return email;
    }

    /** @param email define o e-mail do usuário */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return Cargo ou função ocupada pelo usuário */
    public String getCargo() {
        return cargo;
    }

    /** @param cargo define o cargo do usuário (ex: Desenvolvedor, QA) */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /** @return Perfil ou papel do usuário (ADMIN, QA, TESTADOR, etc.) */
    public String getPerfil() {
        return perfil;
    }

    /** @param perfil define o perfil/papel organizacional do usuário */
    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    /**
     * Retorna a representação em texto do usuário.
     * Usado automaticamente por componentes visuais (ex.: ComboBox).
     * @return nome do usuário, ou fallback "Usuario#{id}" se nome for null
     */
    @Override
    public String toString() {
        return nome != null ? nome : ("Usuario#" + id);
    }
}

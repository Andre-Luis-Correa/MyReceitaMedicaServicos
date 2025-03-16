package unioeste.geral.receitamedica.utils;

public class RespostaInteligenciaArtificial {
    private String intencao;
    private Integer id;

    public RespostaInteligenciaArtificial() {

    }

    public RespostaInteligenciaArtificial(String intencao, Integer id) {
        this.intencao = intencao;
        this.id = id;
    }

    public String getIntencao() {
        return intencao;
    }

    public void setIntencao(String intencao) {
        this.intencao = intencao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

package fate.spring.data.neo4j.servants;

public class ServantsDTO {
    private Servant servant;
    private Classe classe;
    private Power ranking;

    public Servant getServant() {
        return servant;
    }

    public Classe getClasse() {
        return classe;
    }

    public Power getRanking() {
        return ranking;
    }

    public ServantsDTO(Servant servant, Classe classe, Power ranking) {
        this.servant = servant;
        this.classe = classe;
        this.ranking = ranking;
    }
}





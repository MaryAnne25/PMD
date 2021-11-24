package fate.spring.data.neo4j.servants;

public class MaterialWithServant {

    private final String name;
    private final int servantId;
    private final int quantidade;


    public MaterialWithServant(int quantidade, String name, int servantId) {
        this.name = name;
        this.servantId = servantId;
        this.quantidade = quantidade;
    }

    public String getName() {
        return name;
    }

    public int getServantId() {
        return servantId;
    }

    public int getQuantidade() {
        return quantidade;
    }
}

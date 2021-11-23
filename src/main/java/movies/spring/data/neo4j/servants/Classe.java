package movies.spring.data.neo4j.servants;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("SClasse")
public class Classe {
    @Id
    @Property("classe")
    private final String classe;

    public Classe(String classe) {
        this.classe = classe;
    }

    public String getClasse() {
        return classe;
    }
}

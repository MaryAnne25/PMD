package fate.spring.data.neo4j.servants;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("SRanking")
public class Power {
    @Id
    @Property("ranking")
    private final String ranking;

    public Power(String ranking) {
        this.ranking = ranking;
    }

    public String getRanking() {
        return ranking;
    }
}

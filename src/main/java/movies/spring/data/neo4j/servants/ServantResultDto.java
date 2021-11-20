package movies.spring.data.neo4j.servants;

import java.util.Objects;

public class ServantResultDto {

    private final Servant servant;

    public ServantResultDto(Servant servant) {
        this.servant = servant;
    }

    public Servant getServant() {
        return servant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServantResultDto that = (ServantResultDto) o;
        return Objects.equals(servant, that.servant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servant);
    }
}

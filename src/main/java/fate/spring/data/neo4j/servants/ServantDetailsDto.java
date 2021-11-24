package fate.spring.data.neo4j.servants;

import java.util.Objects;

public class ServantDetailsDto {

    private final String name;

    private final int id;

    private final String classe;

    private final String power;

    public ServantDetailsDto(String name, int id, String classe, String power) {
        this.name = name;
        this.id = id;
        this.classe = classe;
        this.power = power;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServantDetailsDto that = (ServantDetailsDto) o;
        return Objects.equals(name, that.name);// && Objects.equals(cast, that.cast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

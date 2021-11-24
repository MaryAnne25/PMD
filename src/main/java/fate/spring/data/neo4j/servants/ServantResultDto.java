package fate.spring.data.neo4j.servants;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ServantResultDto {

    private final String name;

    private final int id;

    private final String classe;

    private final String ranking;

    public ServantResultDto(ServantsDTO servantDTO) {
        this.name = servantDTO.getServant().getName();
        this.id = servantDTO.getServant().getServantId();
        this.classe = servantDTO.getClasse().getClasse();
        this.ranking = servantDTO.getRanking().getRanking();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getClasse() {
        return classe;
    }

    public String getRanking() {
        return ranking;
    }
}

class ServantResultDtoSerializer extends StdSerializer<ServantResultDto> {
    protected ServantResultDtoSerializer(Class<ServantResultDto> t) {
        super(t);
    }

    @Override
    public void serialize(ServantResultDto servantResultDto, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", servantResultDto.getName());
        jsonGenerator.writeNumberField("id", servantResultDto.getId());
        jsonGenerator.writeStringField("classe", servantResultDto.getClasse());
        jsonGenerator.writeStringField("ranking", servantResultDto.getRanking());

    }
}

package movies.spring.data.neo4j.servants;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.TypeSystem;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Michael Hunger
 * @author Mark Angrish
 * @author Jennifer Reif
 * @author Michael J. Simons
 */
@Service
public class ServantService {

	private final ServantRepository servantRepository;

	private final Neo4jClient neo4jClient;

	private final Driver driver;

	private final DatabaseSelectionProvider databaseSelectionProvider;

	ServantService(ServantRepository servantRepository,
				 Neo4jClient neo4jClient,
				 Driver driver,
				 DatabaseSelectionProvider databaseSelectionProvider) {

		this.servantRepository = servantRepository;
		this.neo4jClient = neo4jClient;
		this.driver = driver;
		this.databaseSelectionProvider = databaseSelectionProvider;
	}

	public ServantDetailsDto fetchDetailsById(int id) {
		return this.neo4jClient
				.query("MATCH (servant:Servant {servant_id: $id}) " +
						"MATCH (servant)-[:É_DA_CLASSE]->(classe:SClasse) " +
						"MATCH (servant)-[:É_DA_FORÇA]->(power:SRanking) " +
						"RETURN servant { .name, .servant_id, .classe, .power } " +
						"LIMIT 1"
				)
				.in(database())
				.bindAll(Map.of("id", id))
				.fetchAs(ServantDetailsDto.class)
				.mappedBy(this::toServantDetails)
				.one()
				.orElse(null);
	}

	public List<ServantResultDto> searchServantsByName(String name) {
		List<ServantResultDto> var = this.servantRepository.findSearchResults(name)
				.stream()
				.map(ServantResultDto::new)
				.collect(Collectors.toList());
		return var;
	}

	public List<ServantResultDto> searchSubstituteServants(int id) {
		return this.servantRepository.findServantSubstitutes(id)
				.stream()
				.map(ServantResultDto::new)
				.collect(Collectors.toList());
	}

	public List<ServantMaterialDTO> searchServantsWithMaterial(String materialName) {
		return (List<ServantMaterialDTO>) this.neo4jClient
				.query("MATCH (m:Materials) " +
				"WHERE m.nome CONTAINS $materialName " +
				"MATCH (servant:Servant)-[p]->(m) " +
				"WITH m.nome AS materialName, COLLECT({servantId: servant.servant_id, name:servant.name, quantidade: p.quantidade}) AS materialWithServant " +
				"RETURN materialName, materialWithServant")
				.bindAll(Map.of("materialName", materialName))
				.fetchAs(ServantMaterialDTO.class)
				.mappedBy(this::toServantMaterials)
				.all();
	}

	private Session sessionFor(String database) {
		if (database == null) {
			return driver.session();
		}
		return driver.session(SessionConfig.forDatabase(database));
	}

	private String database() {
		return databaseSelectionProvider.getDatabaseSelection().getValue();
	}

	private ServantDetailsDto toServantDetails(TypeSystem ignored, org.neo4j.driver.Record record) {
		var servant = record.get("servant");
		return new ServantDetailsDto(
				servant.get("name").asString(), servant.get("servant_id").asInt(), servant.get("classe").asString(), servant.get("power").asString()
		);
	}

	private ServantMaterialDTO toServantMaterials(TypeSystem ignored, org.neo4j.driver.Record record) {
		var material = record.get("materialName").toString();
		var servantQuantity = record.get("materialWithServant").values();
		var mws = new ArrayList<MaterialWithServant>();
			for(Iterator<Value> i = servantQuantity.iterator(); i.hasNext();){
				var item = i.next();
				var ms = new MaterialWithServant(item.get("quantidade").asInt(), item.get("name").asString(), item.get("servantId").asInt());
				mws.add(ms);
			}
		return new ServantMaterialDTO(material, mws);
	}
}

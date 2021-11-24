package fate.spring.data.neo4j.servants;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.TypeSystem;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServantService {

	private final ServantRepository servantRepository;

	private final Neo4jClient neo4jClient;

	private final Driver driver;

	private final DatabaseSelectionProvider databaseSelectionProvider;

	private List<ServantResultDto> servantResultDtoList;

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

	public List<ServantResultDto> getAllServants() {
		return this.servantRepository.getAllServants()
				.stream()
				.map(ServantResultDto::new)
				.collect(Collectors.toList());
	}

	public List<ServantResultDto> getGrafoConhecidos(String name1, String name2) {
		servantResultDtoList = new ArrayList<ServantResultDto>();
		String query = "OPTIONAL MATCH (p1:Servant {name:'" + name1 + "'}), (p2:Servant {name: '" + name2 +"'}) " +
				"CALL apoc.algo.dijkstra(p1, p2, 'CONHECE', 'l', 1) " +
				"YIELD path " +
				"WITH COLLECT(nodes(path)) as no " +
				"RETURN no";
		var item = (List<ServantResultDto>) this.neo4jClient
				.query(query)
				.bindAll(Map.of(name1, name2))
				.fetchAs(ServantResultDto.class)
				.mappedBy(this::toServantResult)
				.all();

		return servantResultDtoList;
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

	private ServantResultDto toServantResult(TypeSystem ignored, Record record) {
		var no = record.get("no").values();
		for(Iterator<Value> i = no.iterator(); i.hasNext();){
			var item = i.next();
			for(Iterator<Value> j = item.values().iterator(); j.hasNext();){
				var item2 = j.next().asNode();
				var id = item2.get("servant_id").asInt();
				var name = item2.get("name").asString();
				Classe classeAux = new Classe("n");
				Power powerAux = new Power("s");
				Servant servantAux = new Servant(name, id, classeAux, powerAux);
				ServantsDTO sdAux = new ServantsDTO(servantAux, classeAux, powerAux);
				ServantResultDto srdAux = new ServantResultDto(sdAux);
				servantResultDtoList.add(srdAux);
			}
		}
		Classe classeAux = new Classe("n");
		Power powerAux = new Power("s");
		Servant servantAux = new Servant("a", 1, classeAux, powerAux);
		ServantsDTO sdAux = new ServantsDTO(servantAux, classeAux, powerAux);
		return new ServantResultDto(sdAux);
	}
}

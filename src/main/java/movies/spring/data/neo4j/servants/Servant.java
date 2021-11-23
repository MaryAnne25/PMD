package movies.spring.data.neo4j.servants;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node("Servant")
public class Servant {

	@Id
	@Property("servant_id")
	private final int servant_id;
	@Property("name")
	private final String name;
	//@Relationship(type = "CONHECE", direction = INCOMING)
	//private Set<Servant> amigos1 = new HashSet<>();
	//@Relationship(type = "CONHECE", direction = OUTGOING)
	//private Set<Servant> amigos2 = new HashSet<>();
	@Relationship(type = "É_DA_CLASSE", direction = OUTGOING)
	private Classe classe;
	@Relationship(type = "É_DA_FORÇA", direction = OUTGOING)
	private Power ranking;


	public Servant(String name, int servant_id, Classe classe, Power ranking) {
		this.name = name;
		this.servant_id = servant_id;
		this.classe = classe;
		this.ranking = ranking;
	}

	public String getName() {
		return name;
	}

	public int getServantId() {
		return servant_id;
	}

	public void setClasse(Classe classe) {
		this.classe = classe;
	}
}

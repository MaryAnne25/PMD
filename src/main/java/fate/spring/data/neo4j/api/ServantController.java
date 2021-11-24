package fate.spring.data.neo4j.api;

import fate.spring.data.neo4j.servants.ServantDetailsDto;
import fate.spring.data.neo4j.servants.ServantMaterialDTO;
import fate.spring.data.neo4j.servants.ServantResultDto;
import fate.spring.data.neo4j.servants.ServantService;
import fate.spring.data.neo4j.servants.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class ServantController {

	private final ServantService servantService;

	ServantController(ServantService servantService) {
		this.servantService = servantService;
	}

	@GetMapping("/servant/{id}")
	public ServantDetailsDto findById(@PathVariable("id") int id) {
		return servantService.fetchDetailsById(id);
	}

	@GetMapping("/search")
	List<ServantResultDto> search(@RequestParam("q") String name) {
		return servantService.searchServantsByName(stripWildcards(name));
	}

	@GetMapping("/subs/{id}")
	List<ServantResultDto> subs(@PathVariable("id") int id) {
		return servantService.searchSubstituteServants(id);
	}

	@GetMapping("/servantWithMaterial/{name}")
	List<ServantMaterialDTO> materials(@PathVariable("name") String name) {
		return servantService.searchServantsWithMaterial(stripWildcards(name));
	}

	@GetMapping("/allServants")
	List<ServantResultDto> allServants() {
		return servantService.getAllServants();
	}

	@GetMapping("/conhece/{name1}/{name2}")
	List<ServantResultDto> conhecidos(@PathVariable("name1") String name1, @PathVariable("name2") String name2) {
		return servantService.getGrafoConhecidos(name1, name2);
	}


	private static String stripWildcards(String name) {
		String result = name;
		if (result.startsWith("*")) {
			result = result.substring(1);
		}
		if (result.endsWith("*")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}

package movies.spring.data.neo4j.api;

import movies.spring.data.neo4j.servants.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Michael J. Simons
 */
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

	@GetMapping("servantWithMaterial/{name}")
	List<ServantMaterialDTO> materials(@PathVariable("name") String name) {
		return servantService.searchServantsWithMaterial(stripWildcards(name));
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

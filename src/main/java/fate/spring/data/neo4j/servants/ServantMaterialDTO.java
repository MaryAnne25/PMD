package fate.spring.data.neo4j.servants;

import java.util.List;

public class ServantMaterialDTO {
    private String materialName;
    private List<MaterialWithServant> materialWithServant;

    public String getMaterialName() {
        return materialName;
    }

    public List<MaterialWithServant> getServantWithMaterial() { return materialWithServant; }

    public ServantMaterialDTO(String materialName, List<MaterialWithServant> materialWithServant) {
        this.materialName = materialName;
        this.materialWithServant = materialWithServant;
    }
}

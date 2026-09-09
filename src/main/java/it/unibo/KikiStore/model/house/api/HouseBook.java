package it.unibo.KikiStore.model.house.api;

import java.util.List;
import java.util.Optional;

public interface HouseBook {
    Optional<String> getOwnerNameByTileId(int tileId);
    List<String> getAllOwnerNames();
    Optional<String> getLocationHintByOwnerName(String ownerName);
}

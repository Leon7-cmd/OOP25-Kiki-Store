package it.unibo.KikiStore.model.house.impl;
import it.unibo.KikiStore.model.house.api.HouseBook;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HouseBookImpl implements HouseBook {
    private final Map<Integer, String> tileIdToOwnerName;
    private final Map<String, String> ownerNameToLocationHint;

    public HouseBookImpl(final Map<Integer, String> tileIdToOwnerName,final Map<String, String> ownerNameToLocationHint) {
        this.tileIdToOwnerName = tileIdToOwnerName;
        this.ownerNameToLocationHint = ownerNameToLocationHint;
    }

    @Override
    public Optional<String> getOwnerNameByTileId(final int tileId) {
        return Optional.ofNullable(tileIdToOwnerName.get(tileId));
    }

    @Override
    public List<String> getAllOwnerNames() {
        return List.copyOf(tileIdToOwnerName.values());
    }

    @Override
    public Optional<String> getLocationHintByOwnerName(final String ownerName) {
        return Optional.ofNullable(ownerNameToLocationHint.get(ownerName));
    }
}

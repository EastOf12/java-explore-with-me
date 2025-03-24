package ru.practicum.locations;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LocationMapper {
    public Location mapToLocation(LocationDto locationDto) {
        return new Location(locationDto.getLat(), locationDto.getLon());
    }

    public LocationDto mapToLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}

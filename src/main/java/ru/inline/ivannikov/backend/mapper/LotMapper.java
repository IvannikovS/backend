package ru.inline.ivannikov.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.inline.ivannikov.backend.dto.LotDto;
import ru.inline.ivannikov.backend.model.Lot;

@Mapper(componentModel = "spring")
public interface LotMapper {
    LotDto toDto(Lot entity);

    @Mapping(target = "customerCode", source = "customerCode")
    Lot toEntity(LotDto dto);

    @Mapping(target = "lotId", ignore = true)
    @Mapping(target = "customerCode", source = "customerCode",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLotFromDto(LotDto dto, @MappingTarget Lot entity);
}

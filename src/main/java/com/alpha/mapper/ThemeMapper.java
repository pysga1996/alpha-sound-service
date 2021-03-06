package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.ThemeDTO;
import com.alpha.model.entity.Theme;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class})
public abstract class ThemeMapper {

    @FullMapping
    @Mappings({
            @Mapping(target = "songs", qualifiedBy = PureMapping.class)
    })
    public abstract ThemeDTO entityToDto(Theme theme);

    @FullMapping
    @Mappings({
            @Mapping(target = "songs", qualifiedBy = PureMapping.class)
    })
    public abstract Theme dtoToEntity(ThemeDTO themeDTO);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<ThemeDTO> entityToDtoList(List<Theme> themes);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Theme> dtoToEntityList(List<ThemeDTO> themes);

    @PureMapping
    @Mappings({
            @Mapping(target = "songs", ignore = true)
    })
    public abstract ThemeDTO entityToDtoPure(Theme theme);

    @PureMapping
    @Mappings({
            @Mapping(target = "songs", ignore = true)
    })
    public abstract Theme dtoToEntityPure(ThemeDTO theme);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<ThemeDTO> entityToDtoListPure(List<Theme> themes);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Theme> dtoToEntityListPure(List<ThemeDTO> themes);
}

package com.yas.media.mapper;

import com.yas.commonlibrary.mapper.BaseMapper;
import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MediaVmMapper extends BaseMapper<Media, MediaVm> {

    @Override
    @Mapping(target = "filePath", ignore = true)
    Media toModel(MediaVm vm);

    @Override
    @Mapping(target = "url", ignore = true)
    MediaVm toVm(Media m);

    @Override
    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "filePath", ignore = true)
    void partialUpdate(@MappingTarget Media m, MediaVm v);
}

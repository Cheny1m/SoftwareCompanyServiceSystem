package cn.edu.sicnu.cs.datamapper;

import cn.edu.sicnu.cs.base.BaseMapper;
import cn.edu.sicnu.cs.dto.LogErrorDto;
import cn.edu.sicnu.cs.dto.LogSmallDto;
import cn.edu.sicnu.cs.entity.Log;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogErrorMapper extends BaseMapper<LogErrorDto, Log> {
    @Override
//    @Mapping(source = "createTime",target = "createTime")
//    @Mapping(source = "description",target = "description")
//    @Mapping(source = "requestIp",target = "requestIp")
//    @Mapping(source = "time",target = "time")
//    @Mapping(source = "address",target = "address",defaultValue = "")
//    @Mapping(source = "browser",target = "browser")
//    @Mapping(source = "username",target = "username")
//    @Mapping(source = "rolename",target = "rolename")
//    @Mapping(source = "entity.id",target = "id")
//    @Mapping(source = "entity.username",target = "username")
//    @Mapping(source = "entity.description",target = "description")
//    @Mapping(source = "entity.method",target = "method")
//    @Mapping(source = "entity.params",target = "params")
//    @Mapping(source = "entity.browser",target = "browser",defaultValue = "")
//    @Mapping(source = "entity.requestIp",target = "requestIp")
//    @Mapping(source = "entity.address",target = "address")
//    @Mapping(source = "entity.exceptionDetail",target = "exceptionDetail")
//    @Mapping(source = "entity.createTime",target = "createTime")
    LogErrorDto toDto(Log entity);


}

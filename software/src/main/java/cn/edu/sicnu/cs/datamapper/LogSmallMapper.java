package cn.edu.sicnu.cs.datamapper;


import cn.edu.sicnu.cs.base.BaseMapper;
import cn.edu.sicnu.cs.dto.LogSmallDto;
import cn.edu.sicnu.cs.entity.Log;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogSmallMapper extends BaseMapper<LogSmallDto, Log> {


    @Override
//    @Mapping(source = "createTime",target = "createTime")
//    @Mapping(source = "description",target = "description")
//    @Mapping(source = "requestIp",target = "requestIp")
//    @Mapping(source = "time",target = "time")
//    @Mapping(source = "address",target = "address",defaultValue = "")
//    @Mapping(source = "browser",target = "browser")
//    @Mapping(source = "username",target = "username")
//    @Mapping(source = "rolename",target = "rolename")
//    @Mapping(source = "createTime",target = "createTime")
//    @Mapping(source = "description",target = "description")
//    @Mapping(source = "requestIp",target = "requestIp")
//    @Mapping(source = "entity.time",target = "time")
//    @Mapping(source = "entity.address",target = "address",defaultValue = "")
//    @Mapping(source = "entity.browser",target = "browser")
//    @Mapping(source = "entity.username",target = "username")
//    @Mapping(source = "entity.rolename",target = "rolename")
    LogSmallDto toDto(Log entity);

//    @Override
//    @Mapping(source = "createTime",target = "createTime")
//    @Mapping(source = "description",target = "description")
//    @Mapping(source = "requestIp",target = "requestIp")
//    @Mapping(source = "time",target = "time")
//    @Mapping(source = "address",target = "address",defaultValue = "")
//    @Mapping(source = "browser",target = "browser")
//    @Mapping(source = "username",target = "username")
//    @Mapping(source = "rolename",target = "rolename")
//    List<LogSmallDto> toDto(List<Log> entityList);

    public static void main(String[] args) {
        Log log = new Log();
        log.setLogType("LOGIN");
        log.setRolename("ROLE_ADMIN");
        log.setDescription("登录");
        log.setBrowser("chrome 8");
        log.setAddress("四川省 成都市");
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        log.setUsername("ADMIN");

        log.setTime(115L);
        log.setRequestIp("1111");
//        System.out.println(new LogSmallMapperImpl().toDto(log));
    }
}

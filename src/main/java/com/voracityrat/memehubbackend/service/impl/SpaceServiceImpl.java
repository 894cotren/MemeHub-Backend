package com.voracityrat.memehubbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.space.SpaceAddRequest;
import com.voracityrat.memehubbackend.model.dto.space.SpaceQueryRequest;
import com.voracityrat.memehubbackend.model.entity.Space;
import com.voracityrat.memehubbackend.model.entity.SpaceUser;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.enums.SpaceLevelEnum;
import com.voracityrat.memehubbackend.model.enums.SpaceRoleEnum;
import com.voracityrat.memehubbackend.model.enums.SpaceTypeEnum;
import com.voracityrat.memehubbackend.model.vo.space.SpaceVO;
import com.voracityrat.memehubbackend.model.vo.user.UserVO;
import com.voracityrat.memehubbackend.service.SpaceService;
import com.voracityrat.memehubbackend.mapper.SpaceMapper;
import com.voracityrat.memehubbackend.service.SpaceUserService;
import com.voracityrat.memehubbackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author grey
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-08-26 21:25:36
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{

    @Resource
    private UserService userService;


    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SpaceUserService spaceUserService;

    @Override
    public void validSpace(Space space, boolean add) {
        /**
         * 验证空间名称合法，验证空间等级合法。
         * 根据传入add是否为true，分为两种判断并且抛出不同提示词。
         */
        ThrowUtil.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        //根据空间等级值获取对应的枚举类
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        //获取空间类型
        Integer spaceType = space.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);


        // 要创建
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
            if (spaceType == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不能为空");
            }
        }
        // 修改数据时，如果要改空间级别
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
        if (spaceType!=null && spaceTypeEnum==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类别不存在");
        }
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额，根据我们定义好的枚举类来填充。
        //但是下面判断做了区分，如果我们的管理员进行的更新或者创建的时候手动给了值，那么我们就不动它。
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        //转VO
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        //关联查询用户信息
        Long userId = spaceVO.getUserId();
        if (userId!=null && userId>0){
            User user = userService.getById(userId);
            UserVO userVo = userService.getUserVo(user);
            spaceVO.setUser(userVo);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        //一定要把分页信息给设置过来，不然会分页失败的。
        Page<SpaceVO> spaceVOPages= new Page<>(spacePage.getCurrent(), spacePage.getSize(),spacePage.getTotal());

        if (CollUtil.isEmpty(spaceList)){
            return spaceVOPages;
        }
        // 对象列表 =》封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream()
                .map(SpaceVO::objToVo)
                .collect(Collectors.toList());
        //关联用户查询信息
        //通过流获取到用户id的不重复集合。通过set去重
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        List<User> users = userService.listByIds(userIdSet);
        //对用户id进行处理，我们转为键值对的形式，后续可以通过id快速找到用户。
        Map<Long, List<User>> userIdUserListMap = users.stream().collect(Collectors.groupingBy(User::getId));
        //对VO结果进行创建者填充
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user=null;
            if (userIdUserListMap.containsKey(userId)){
                user=userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVo(user));
        });
        spaceVOPages.setRecords(spaceVOList);
        return spaceVOPages;
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        // 拼接查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "user_id", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "space_name", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "space_level", spaceLevel);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "space_type", spaceType);
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        // 如果空间名称为空给予默认值
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        //如果空间等级为空 给与默认的等级
        //后面对高等级做了校验
        if (spaceAddRequest.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        //为兼顾老代码，我们需要判断下spaceType是否为空，是的话我们设置一个默认值。
        //虽然我们数据库层面已经做了默认值，但是我们还是要尽量做好代码层面的校验
        //所以我们这里
        if (space.getSpaceType()==null){
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }

        // 填充数据
        this.fillSpaceBySpaceLevel(space);
        // 数据校验
        this.validSpace(space, true);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        // 权限校验  空间等级校验，必须要管理员才能开启高等级空间
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 针对用户进行加锁，防止用户瞬间点击创建多次引起并发问题。锁粒度为当前用户。String.valueOf(userId).intern();保证字符串是同一个对象。
        //注意：不同创建字符串的区别；
        // String s1 = "hello";      // 在字符串常量池中
        //String s2 = new String("hello"); // 在堆中创建新对象
        //字符串常量池中的String对象具备普通Java对象的所有特性。而且具有唯一性。
        //下面这个锁是在常量池中，为了保证是唯一对象嘛。
        //2025年9月1日新增，用户也只能创建一个团队空间。
        String userIdlock = String.valueOf(userId).intern();
        //TODO 锁待优化，可以优化为并发集合来控制，手动释放。 目前的经过常量区字符串对象进行加锁有点难释放的
        synchronized (userIdlock) {
            //这里为什么不能用声明式事务加载方法上呢？ 因为声明式事务是在方法执行完成后提交的。
            //如果要用@Transactional注解，那么我们最好把这个数据库操作提取出来成一个方法来加。
            //所以我们这里通过使用编程式事务来控制事务的范围。
            Long newSpaceId = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery()
                        .eq(Space::getUserId, userId)
                        .eq(Space::getSpaceType,space.getSpaceType())
                        .exists();
                ThrowUtil.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间和一个团队空间。");
                // 写入数据库
                boolean result = this.save(space);
                ThrowUtil.throwIf(!result, ErrorCode.OPERATION_ERROR);
                //创建成功之后，判断空间是否是团队空间，如果是团队空间我们还需要自动往用户空间表插入一条控件创建人信息。
                if(SpaceTypeEnum.TEAM.getValue()==space.getSpaceType()){
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setUserId(userId);
                    //创始人默认是管理员权限。
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    result = spaceUserService.save(spaceUser);
                    ThrowUtil.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建团队成员记录失败");
                }
                // 返回新写入的数据 id
                return space.getId();
            });
            // 返回结果是包装类，可以做一些处理
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }

        //这里是无锁但是方法未执行完的地方，如果使用声明式事务，很容易在这里引起并发导致创建两个空间出来。
    }


}





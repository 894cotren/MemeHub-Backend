package com.voracityrat.memehubbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.constant.UserConstant;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.file.UploadPictureResult;
import com.voracityrat.memehubbackend.model.dto.picture.*;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.enums.PictureReviewEnum;
import com.voracityrat.memehubbackend.model.enums.UserRoleEnum;
import com.voracityrat.memehubbackend.model.vo.picture.PicturePagesVO;
import com.voracityrat.memehubbackend.model.vo.picture.PictureVO;
import com.voracityrat.memehubbackend.service.PictureService;
import com.voracityrat.memehubbackend.mapper.PictureMapper;
import com.voracityrat.memehubbackend.service.UserPictureService;
import com.voracityrat.memehubbackend.service.UserService;
import com.voracityrat.memehubbackend.utils.PictureCosUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author voracityrat
 * @description 针对表【picture(图片表)】的数据库操作Service实现
 * @createDate 2025-06-25 14:41:40
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    @Resource
    private PictureCosUtil pictureCosUtil;

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private UserService userService;

    @Resource
    private UserPictureService userPictureService;

    @Override
    public PictureVO uploadPicture(PictureUploadRequest pictureUploadRequest, MultipartFile multipartFile, User loginUser) {
        /**
         * 入参：图片请求id的reqeust(可为空，区分是第一次新增还是第二次编辑)、当前登录对象，图片文件
         * 1. 参数校验、权限校验
         * 2. >判断是否是第一次?是第一次要上传cos嘛?如果不是第一次还需要上传吗?需要查询数据库对比一下url是否相同？避免重复上传？需要这一步吗？  算了不考虑那么复杂，都传
         *    上传cos，获取到图片基本信息
         * 3. 把图片基本信息封装，保存到数据库，获取到图片的id
         * 4. 返回该图片信息 id、url
         * 出参：图片id、url
         */
        // 1. 参数校验、权限校验
        //当前登陆用户不能为空
        ThrowUtil.throwIf(loginUser==null, ErrorCode.NO_AUTH_ERROR);
        // 判断是更新还是新增，如果是更新那么需要判断图片存在不存在
        Long pictureId=pictureUploadRequest.getId();
        if (pictureId!=null){
            boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists();
            ThrowUtil.throwIf(!exists,ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        }
        //2.上传cos，获取到图片基本信息
        //配置用户上传前缀,公共的放在public路径下，并且按照用户id划分保存的目录
        String uploadPathPrefix=String.format("public/%s",loginUser.getId());
        UploadPictureResult uploadPictureResult = pictureCosUtil.pictureUpload(multipartFile, uploadPathPrefix);
        // 3. 把图片基本信息封装，保存到数据库，获取到图片的id
        Picture picture = new Picture();
        picture.setPicUrl(uploadPictureResult.getUrl());
        picture.setPicName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        this.fillReviewParams(picture,loginUser);
        //如果传入的图片id不为空，那么得设置进来根据这个id更新。
        if(pictureId!=null){
            picture.setId(pictureId);
            picture.setUpdateTime(new Date());
        }

        //插入到数据库，如果没有id就是添加，那么会把id回写过来的，所以可以返回该对象
        boolean result = this.saveOrUpdate(picture);
        ThrowUtil.throwIf(!result,ErrorCode.SYSTEM_ERROR,"图片上传失败，数据库操作失败");
        return PictureVO.objToVo(picture);
    }

    /*
        填充图片审核信息。
     */
    private void fillReviewParams(Picture picture, User loginUser) {
        //如果用户是管理员的话自动审核，如果用户是普通用户，那么就需要将字段设置为待审核
        String userRole = loginUser.getUserRole();
        UserRoleEnum userRoleEnum = UserRoleEnum.getRoleEnumByValue(userRole);
        if (UserRoleEnum.ADMIN_USER == userRoleEnum) {
            //管理员
            picture.setReviewStatus(PictureReviewEnum.PASS.getValue());
            picture.setReviewMessage("管理员自动审核通过");
            picture.setReviewerId(loginUser.getId());
        } else {
            //普通用户
            picture.setReviewStatus(PictureReviewEnum.REVIEWING.getValue());
        }
    }

    @Override
    public boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, User loginUser) {
        /**
         * 1. 参数校验
         *    1. 更新对象不能为空
         *    2. 必须要有图片id
         *    3. 如果有url 需要校验url长度
         *    4. 如果有简介 需要校验简介长度
         *    5. 如果有图片名称需要校验图片名称长度
         * 2.查询验证图片是否存在！
         * 3. 进行图片更新
         */
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest,picture);
        validPicture(picture);
        //查询验证图片是否存在！
        Picture oldPicture = this.getById(pictureUpdateRequest.getId());
        ThrowUtil.throwIf(oldPicture==null,ErrorCode.NOT_FOUND_ERROR);
        //权限校验，只能图片上传者本人，或者管理员更新
        if (!loginUser.getId().equals(oldPicture.getUserId()) && !loginUser.getUserRole().equals(UserConstant.ADMIN)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"只能图片上传者或者管理员可以更改图片信息");
        }
        //进行图片更新
        //如果图片的tags不为空，需要将list转换json过来并复制到将要更新的对象里去
        List<String> tags = pictureUpdateRequest.getTags();
        if (tags!=null && !tags.isEmpty()){
            String tagsJson= JSONUtil.toJsonStr(tags);
            picture.setTags(tagsJson);
        }
        //图片审核信息更新， 如果是用户更新，那么需要将审核信息更改为待审核,如果是管理员更新，那么直接自动审核
        this.fillReviewParams(picture,loginUser);
        boolean result = this.updateById(picture);
        ThrowUtil.throwIf(!result,ErrorCode.OPERATION_ERROR,"图片更新失败");
        return true;
    }

    @Override
    public Picture getPictureByIdForAdmin(Long picId) {
        /**
         * 1. 权限校验  controller做了
         * 2. 参数校验
         *    1. id不为空
         * 3. 数据库查询
         * 4. 返回图片数据
         */
        if (picId ==null || picId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = this.getById(picId);
        ThrowUtil.throwIf(picture==null,ErrorCode.NOT_FOUND_ERROR);
        return picture;
    }

    @Override
    public Page<Picture> getPicturePages(PicturePagesRequest picturePagesRequest) {
        /**
         * 1. 参数校验
         * 2. 权限校验
         * 3. 分页查询
         * 4. 分页结果返回
         */
        //参数校验
        ThrowUtil.throwIf(picturePagesRequest==null,ErrorCode.PARAMS_ERROR);
        long pageNum = picturePagesRequest.getPageNum();
        long pageSize = picturePagesRequest.getPageSize();
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
     /*   //下面这个是手写sql的自定义分页方式。
        Page<Picture> picturePages = pictureMapper.getPicturePages(new Page<>(pageNum, pageSize), picturePagesRequest);
*/
        //对分页参数进行条件拼接
        QueryWrapper<Picture> pagesQueryWrapper = getPicturePagesQueryWrapper(picturePagesRequest);
        //分页查询
        Page<Picture> picturePages = this.page(new Page<>(pageNum, pageSize), pagesQueryWrapper);
        ThrowUtil.throwIf(picturePages==null,ErrorCode.OPERATION_ERROR);
        return picturePages;
    }

    @Override
    public Page<PicturePagesVO> getPictureVOPages(PictureVOPagesRequest pictureVOPagesRequest, Long loginUserId) {
        /**
         * 1. 参数校验
         * 2. 限制分页大小，防止爬虫。
         * 3. 进行分页搜索
         * 4. 进行数据脱敏
         * 5. 返回脱敏数据
         */
        ThrowUtil.throwIf(pictureVOPagesRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = pictureVOPagesRequest.getPageNum();
        long pageSize = pictureVOPagesRequest.getPageSize();
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        //限制分页大小，防止爬虫。
        ThrowUtil.throwIf(pageSize > 20, ErrorCode.OPERATION_ERROR, "普通用户不能使用每页20条以上分页");
        //进行分页搜索
        //拼接分页查询条件
        QueryWrapper<Picture> queryWrapper = getPictureVOPagesQueryWrapper(pictureVOPagesRequest);
        Page<Picture> picturePage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        long total = picturePage.getTotal();
        //进行数据脱敏
        List<Picture> records = picturePage.getRecords();
        List<PicturePagesVO> picturePagesVOList = getPicturePagesVOList(records,loginUserId);
        Page<PicturePagesVO> picturePagesVOPage = new Page<>(pageNum, pageSize);
        picturePagesVOPage.setTotal(total);
        picturePagesVOPage.setRecords(picturePagesVOList);
        return picturePagesVOPage;
    }

    /**
     * 将图片实体类转换为可以给用户看的脱敏后的PicturePagesVO
     *
     * @param pictureList
     * @return
     */
    @Override
    public List<PicturePagesVO> getPicturePagesVOList(List<Picture> pictureList, Long loginUserId) {
        List<PicturePagesVO> picturePagesVOList = new ArrayList<>(pictureList.size());
        if (pictureList==null || pictureList.size()==0){
            return picturePagesVOList;
        }
        //图片的用户id为了给图片赋值author
        List<Long> userIds = new ArrayList<>();

        //图片id后面用于判断用户是否收藏
        List<Long> picIds= new ArrayList<>();
        //数据脱敏，收集用户id
        for (Picture picture : pictureList) {
            picturePagesVOList.add(PicturePagesVO.objToPagesVo(picture));
            Long userId = picture.getUserId();
            //收集用户id用于批量查询
            if (userId != null && userId > 0) {
                userIds.add(userId);
            }
            //收集图片id用于判断是否收藏
            picIds.add(picture.getId());
        }
        //对用户id进行批量查询    (说真的我不太确定这种查出来内存里筛选的快还是我优化好的只查询用户名的sql快)
        List<User> users = userService.getUserNameByIds(userIds);
        //对users进行id分组  userid->User
        Map<Long, List<User>> collect = users.stream().collect(Collectors.groupingBy(User::getId));
        //新增收藏功能，新增isFavorite字段，如果是用户收藏的话这个字段需要设定为ture (字段默认为false)
        //查询当前用户在当前这分页图片里收藏了哪些
        Set<Long> favoritePicIds= userPictureService.favoriteInPictureIds(picIds,loginUserId);
        //遍历picturePagesVOList 去为每个对象设置用户信息,脱敏后的用户信息
        picturePagesVOList.forEach(picturePagesVO -> {
            Long userId = picturePagesVO.getUserId();
            if (collect.containsKey(userId)) {
                picturePagesVO.setUserName(collect.get(userId).get(0).getUserName());
            }
            //如果当前图片id在用户喜欢的图片id里，标记一下
            if (favoritePicIds.contains(picturePagesVO.getId())){
                picturePagesVO.setIsFavorite(true);
            }
        });
        return picturePagesVOList;
    }

    @AuthCheck(mustRole = UserConstant.ADMIN)
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 1. 参数校验
        ThrowUtil.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        String reviewMessage = pictureReviewRequest.getReviewMessage();
        PictureReviewEnum reviewEnum = PictureReviewEnum.getReviewEnumByValue(reviewStatus);
        if (id == null || reviewEnum == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //2. 图片是否存在校验
        Picture oldPicture = this.getById(id);
        ThrowUtil.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        //3. 图片审核状态校验，避免重复审核
        if (reviewEnum.getValue() == oldPicture.getReviewStatus()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请不要重复审核");
        }
        //4. 图片审核结果更新
        Picture newPicture = new Picture();
        newPicture.setId(id);
        newPicture.setReviewStatus(reviewEnum.getValue());
        newPicture.setReviewMessage(reviewMessage);
        newPicture.setReviewerId(loginUser.getId());
        boolean result = this.updateById(newPicture);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "审核操作失败");
        }
    }


    /**
     * 获取用户分页查询拼接后的QueryWrapper
     *
     * @param pictureVOPagesRequest
     * @return
     */
    private QueryWrapper<Picture> getPictureVOPagesQueryWrapper(PictureVOPagesRequest pictureVOPagesRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureVOPagesRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        String name = pictureVOPagesRequest.getPicName();
        String introduction = pictureVOPagesRequest.getIntroduction();
        String category = pictureVOPagesRequest.getCategory();
        List<String> tags = pictureVOPagesRequest.getTags();
        String searchText = pictureVOPagesRequest.getSearchText();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(
                    qw -> qw.lambda().like(Picture::getPicName, searchText)
                            .or()
                            .like(Picture::getIntroduction, searchText)
            );
        }
        queryWrapper.lambda().like(StrUtil.isNotBlank(name), Picture::getPicName, name);
        queryWrapper.lambda().like(StrUtil.isNotBlank(introduction), Picture::getIntroduction, introduction);
        queryWrapper.lambda().eq(StrUtil.isNotBlank(category), Picture::getCategory, category);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 用户只能查询审核通过的图片
        queryWrapper.lambda().eq(Picture::getReviewStatus, PictureReviewEnum.PASS.getValue());
        //排序 ,时间倒叙排，这里写死了。
        queryWrapper.orderBy(true,false,"create_time");
        return queryWrapper;
    }

    /**
     * 获取管理员分页查询条件拼接查询对象
     *
     * @param picturePagesRequest
     * @return
     */
    private QueryWrapper<Picture> getPicturePagesQueryWrapper(PicturePagesRequest picturePagesRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (picturePagesRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = picturePagesRequest.getId();
        String name = picturePagesRequest.getPicName();
        String introduction = picturePagesRequest.getIntroduction();
        String category = picturePagesRequest.getCategory();
        List<String> tags = picturePagesRequest.getTags();
        String searchText = picturePagesRequest.getSearchText();
        Long userId = picturePagesRequest.getUserId();
        String reviewMessage = picturePagesRequest.getReviewMessage();
        Integer reviewStatus = picturePagesRequest.getReviewStatus();
        Date startTime = picturePagesRequest.getStartTime();
        Date endTime = picturePagesRequest.getEndTime();
        //排序字段
        String sortField = picturePagesRequest.getSortField();
        String sortOrder = picturePagesRequest.getSortOrder();

        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(
                    qw -> qw.lambda().like(Picture::getPicName, searchText)
                            .or()
                            .like(Picture::getIntroduction, searchText)
            );
        }
        queryWrapper.lambda().eq(ObjUtil.isNotEmpty(id), Picture::getId, id);
        queryWrapper.lambda().eq(ObjUtil.isNotEmpty(userId), Picture::getUserId, userId);
        queryWrapper.lambda().like(StrUtil.isNotBlank(name), Picture::getPicName, name);
        queryWrapper.lambda().like(StrUtil.isNotBlank(introduction), Picture::getIntroduction, introduction);
        queryWrapper.lambda().eq(StrUtil.isNotBlank(category), Picture::getCategory, category);
        queryWrapper.lambda().like(StrUtil.isNotBlank(reviewMessage), Picture::getReviewMessage, reviewMessage);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        //审核状态
        queryWrapper.lambda().eq(reviewStatus != null, Picture::getReviewStatus, reviewStatus);

        // 时间范围查询
        if (startTime != null) {
            queryWrapper.lambda().ge(Picture::getCreateTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.lambda().le(Picture::getCreateTime, endTime);
        }
        //排序
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), "ascend".equals(sortOrder),sortField);
        return queryWrapper;
    }

    private void validPicture(Picture picture) {
        /**
         * 1. 参数校验
         *    1. 更新对象不能为空
         *    2. 必须要有图片id
         *    3. 如果有简介 需要校验简介长度
         *    4. 如果有图片名称需要校验图片名称长度
         */
        ThrowUtil.throwIf(picture==null,ErrorCode.PARAMS_ERROR);
        Long picId = picture.getId();
        if (picId==null || picId<=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String introduction = picture.getIntroduction();
        //如果有简介 需要校验简介长度
        if (StrUtil.isNotBlank(introduction)){
            if (128<introduction.length()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"简介过长，不能超过128位");
            }
        }
        //如果有图片名称需要校验图片名称长度
        String picName = picture.getPicName();
        if (StrUtil.isNotBlank(picName)){
            if (32<picName.length()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"图片名称，不能超过32位");
            }
        }
        String category = picture.getCategory();
        if (StrUtil.isNotBlank(category)){
            if (16<category.length()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"图片简介，不能超过16位");
            }
        }
    }
}





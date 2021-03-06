package com.saintdan.framework.controller;

import com.saintdan.framework.annotation.CurrentUser;
import com.saintdan.framework.component.ResultHelper;
import com.saintdan.framework.component.ValidateHelper;
import com.saintdan.framework.constant.ControllerConstant;
import com.saintdan.framework.constant.ResourceURL;
import com.saintdan.framework.constant.ResultConstant;
import com.saintdan.framework.constant.VersionConstant;
import com.saintdan.framework.domain.GroupDomain;
import com.saintdan.framework.enums.ErrorType;
import com.saintdan.framework.enums.OperationStatus;
import com.saintdan.framework.enums.OperationType;
import com.saintdan.framework.exception.CommonsException;
import com.saintdan.framework.param.GroupParam;
import com.saintdan.framework.po.Group;
import com.saintdan.framework.po.User;
import com.saintdan.framework.tools.QueryHelper;
import com.saintdan.framework.vo.GroupVO;
import com.saintdan.framework.vo.ResultVO;
import javax.validation.Valid;
import net.kaczmarzyk.spring.data.jpa.domain.DateBetween;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller of group.
 *
 * @author <a href="http://github.com/saintdan">Liao Yifan</a>
 * @date 10/17/15
 * @since JDK1.8
 */
@RestController
@RequestMapping(ResourceURL.RESOURCES + VersionConstant.V1 + ResourceURL.GROUPS)
public class GroupController {

  // ------------------------
  // PUBLIC METHODS
  // ------------------------

  /**
   * Create new {@link com.saintdan.framework.po.Group}.
   *
   * @param param {@link GroupParam}
   * @return {@link com.saintdan.framework.vo.GroupVO}
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResultVO create(@CurrentUser User currentUser, @Valid GroupParam param, BindingResult result) {
    try {
      // Validate current user, param and sign.
      ResultVO resultVO = validateHelper.validate(param, result, currentUser, logger, OperationType.CREATE);
      if (resultVO != null) {
        return resultVO;
      }
      // Return result and message.
      return resultHelper.successResp(groupDomain.create(param, currentUser));
    } catch (CommonsException e) {
      // Return error information and log the exception.
      return resultHelper.infoResp(logger, e.getErrorType(), e.getMessage());
    } catch (Exception e) {
      // Return unknown error and log the exception.
      return resultHelper.errorResp(logger, e, ErrorType.UNKNOWN, e.getMessage());
    }
  }

  /**
   * Show all.
   *
   * @param param {@link GroupParam}
   * @return all group.
   */
  @RequestMapping(method = RequestMethod.GET)
  public ResultVO all(
      @And({
          @Spec(path = "name", spec = Like.class),
          @Spec(path = "validFlag", constVal = "VALID", spec = In.class),
          @Spec(path = "createdDate", params = {"createdDateAfter, createdDateBefore"}, spec = DateBetween.class)}) Specification<Group> groupSpecification,
      GroupParam param) {
    try {
      if (param.getPageNo() == null) {
        return resultHelper.successResp(groupDomain.getAll(groupSpecification, QueryHelper.getSort(param.getSortBy()), GroupVO.class));
      }
      return resultHelper.successResp(groupDomain.getPage(groupSpecification, QueryHelper.getPageRequest(param), GroupVO.class));
    } catch (CommonsException e) {
      // Return error information and log the exception.
      return resultHelper.infoResp(logger, e.getErrorType(), e.getMessage());
    } catch (Exception e) {
      // Return unknown error and log the exception.
      return resultHelper.errorResp(logger, e, ErrorType.UNKNOWN, e.getMessage());
    }
  }

  /**
   * Show {@link com.saintdan.framework.vo.GroupVO} by ID.
   *
   * @param id id of group
   * @return {@link com.saintdan.framework.vo.GroupVO}
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResultVO detail(@PathVariable String id) {
    try {
      if (StringUtils.isBlank(id)) {
        return resultHelper.infoResp(ErrorType.SYS0002, String.format(ControllerConstant.PARAM_BLANK, ControllerConstant.ID_PARAM));
      }
      return resultHelper.successResp(groupDomain.getById((Long.valueOf(id)), GroupVO.class));
    } catch (CommonsException e) {
      // Return error information and log the exception.
      return resultHelper.infoResp(logger, e.getErrorType(), e.getMessage());
    } catch (Exception e) {
      // Return unknown error and log the exception.
      return resultHelper.errorResp(logger, e, ErrorType.UNKNOWN, e.getMessage());
    }
  }

  /**
   * Update {@link com.saintdan.framework.po.Group}.
   *
   * @param id    id of group
   * @param param {@link GroupParam}
   * @return {@link com.saintdan.framework.vo.GroupVO}
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResultVO update(@CurrentUser User currentUser, @PathVariable String id, @Valid GroupParam param, BindingResult result) {
    try {
      param.setId(StringUtils.isBlank(id) ? null : Long.valueOf(id));
      // Validate current user, param and sign.
      ResultVO resultVO = validateHelper.validate(param, result, currentUser, logger, OperationType.UPDATE);
      if (resultVO != null) {
        return resultVO;
      }
      // Update group.
      return resultHelper.successResp(groupDomain.update(param, currentUser));
    } catch (CommonsException e) {
      // Return error information and log the exception.
      return resultHelper.infoResp(logger, e.getErrorType(), e.getMessage());
    } catch (Exception e) {
      // Return unknown error and log the exception.
      return resultHelper.errorResp(logger, e, ErrorType.UNKNOWN, e.getMessage());
    }
  }

  /**
   * Delete group.
   *
   * @param id group's id
   * @return {@link com.saintdan.framework.vo.GroupVO}
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResultVO delete(@CurrentUser User currentUser, @PathVariable String id) {
    try {
      GroupParam param = new GroupParam(StringUtils.isBlank(id) ? null : Long.valueOf(id));
      // Validate current user and param.
      ResultVO resultVO = validateHelper.validate(param, currentUser, logger, OperationType.DELETE);
      if (resultVO != null) {
        return resultVO;
      }
      // Delete group.
      groupDomain.delete(param, currentUser);
      final String GROUP = "group";
      return new ResultVO(ResultConstant.OK, OperationStatus.SUCCESS, String.format(ControllerConstant.DELETE, GROUP));
    } catch (CommonsException e) {
      // Return error information and log the exception.
      return resultHelper.infoResp(logger, e.getErrorType(), e.getMessage());
    } catch (Exception e) {
      // Return unknown error and log the exception.
      return resultHelper.errorResp(logger, e, ErrorType.UNKNOWN, e.getMessage());
    }
  }

  // ------------------------
  // PRIVATE FIELDS
  // ------------------------

  private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

  @Autowired private ResultHelper resultHelper;

  @Autowired private ValidateHelper validateHelper;

  @Autowired private GroupDomain groupDomain;
}

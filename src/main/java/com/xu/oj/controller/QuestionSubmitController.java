package com.xu.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.oj.annotation.AuthCheck;
import com.xu.oj.common.BaseResponse;
import com.xu.oj.common.ErrorCode;
import com.xu.oj.common.ResultUtils;
import com.xu.oj.constant.UserConstant;
import com.xu.oj.exception.BusinessException;
import com.xu.oj.model.dto.question.QuestionQueryRequest;
import com.xu.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xu.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xu.oj.model.entity.Question;
import com.xu.oj.model.entity.QuestionSubmit;
import com.xu.oj.model.entity.User;
import com.xu.oj.model.vo.QuestionSubmitVO;
import com.xu.oj.service.QuestionSubmitService;
import com.xu.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * @author <a href="https://github.com/Polifishing/MiaxOJ">徐书恒</a>

 */
//@RestController
//@RequestMapping("/question_submit")
//@Slf4j
@Deprecated
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return BaseResponse 提交题目
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交代码
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);

        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（仅管理员和自己可见）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
    }
}

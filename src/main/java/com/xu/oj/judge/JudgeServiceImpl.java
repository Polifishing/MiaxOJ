package com.xu.oj.judge;

import cn.hutool.json.JSONUtil;
import com.xu.oj.common.ErrorCode;
import com.xu.oj.exception.BusinessException;
import com.xu.oj.judge.codesandbox.CodeSandbox;
import com.xu.oj.judge.codesandbox.CodeSandboxFactory;
import com.xu.oj.judge.codesandbox.CodeSandboxProxy;
import com.xu.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xu.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xu.oj.judge.strategy.JudgeContext;
import com.xu.oj.model.dto.question.JudgeCase;
import com.xu.oj.judge.codesandbox.model.JudgeInfo;
import com.xu.oj.model.entity.Question;
import com.xu.oj.model.entity.QuestionSubmit;
import com.xu.oj.model.enums.QuestionSubmitStatusEnum;
import com.xu.oj.service.QuestionService;
import com.xu.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService{
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    @Lazy
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if(questionSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);

        if(question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        if(!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目正在判题中");
        }

        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);

        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }

        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);

        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList =
                judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        //生成response
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        if(executeCodeResponse.getMessage()==null||!(executeCodeResponse.getMessage().equals("编译失败")||executeCodeResponse.getMessage().equals("运行超时")))
        {
            List<String> outputList = executeCodeResponse.getOutputList();
            JudgeContext judgeContext = new JudgeContext();
            judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
            judgeContext.setJudgeCaseList(judgeCaseList);
            judgeContext.setOutputList(outputList);
            judgeContext.setQuestion(question);
            judgeContext.setInputList(inputList);
            judgeContext.setQuestionSubmit(questionSubmit);
            //调用策略
            JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
            //修改数据库判题结果
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }
        else
        {
            //修改数据库判题结果
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
            judgeInfo.setMessage(executeCodeResponse.getMessage());
            judgeInfo.setTime(executeCodeResponse.getJudgeInfo().getTime());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionId);
        if(questionSubmitUpdate.getStatus()==2) {
            question.setAcceptedNum(question.getAcceptedNum()+1);
        }
        question.setSubmitNum(question.getSubmitNum()+1);
        update = questionService.updateById(question);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionSubmitResult;
    }
}

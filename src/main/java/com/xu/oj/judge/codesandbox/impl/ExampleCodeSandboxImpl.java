package com.xu.oj.judge.codesandbox.impl;

import com.xu.oj.judge.codesandbox.CodeSandbox;
import com.xu.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xu.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xu.oj.judge.codesandbox.model.JudgeInfo;
import com.xu.oj.model.enums.JudgeInfoMessageEnum;
import com.xu.oj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例测试
 */
public class ExampleCodeSandboxImpl implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setTime(100L);
        judgeInfo.setMemory(100L);

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        return executeCodeResponse;
    }
}

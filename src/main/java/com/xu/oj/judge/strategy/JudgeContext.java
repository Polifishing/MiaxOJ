package com.xu.oj.judge.strategy;

import com.xu.oj.model.dto.question.JudgeCase;
import com.xu.oj.judge.codesandbox.model.JudgeInfo;
import com.xu.oj.model.entity.Question;
import com.xu.oj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于传递参数）
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private Question question;

    private List<JudgeCase> judgeCaseList;

    private QuestionSubmit questionSubmit;
}

package com.xu.oj.judge;

import com.xu.oj.judge.strategy.DefaultJudgeStrategy;
import com.xu.oj.judge.strategy.JavaJudgeContextStrategy;
import com.xu.oj.judge.strategy.JudgeContext;
import com.xu.oj.judge.strategy.JudgeStrategy;
import com.xu.oj.judge.codesandbox.model.JudgeInfo;
import com.xu.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public class JudgeManager {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if(language.equals("java")){
            judgeStrategy = new JavaJudgeContextStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    };
}

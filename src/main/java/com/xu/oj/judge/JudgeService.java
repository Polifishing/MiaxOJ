package com.xu.oj.judge;

import com.xu.oj.model.entity.QuestionSubmit;

public interface JudgeService {

    QuestionSubmit doJudge(long QuestionSubmitId);
}

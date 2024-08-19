package com.xu.oj.model.dto.questionsubmit;


import lombok.Data;

import java.io.Serializable;



/**
 * 创建请求
 *
 * @author <a href="https://github.com/Polifishing/MiaxOJ">徐书恒</a>

 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
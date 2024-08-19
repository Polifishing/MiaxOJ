package com.xu.oj.judge.codesandbox;

import com.xu.oj.judge.codesandbox.impl.ExampleCodeSandboxImpl;
import com.xu.oj.judge.codesandbox.impl.RemoteCodeSandboxImpl;
import com.xu.oj.judge.codesandbox.impl.ThirdPartyCodeSandboxImpl;

/**
 * 代码沙箱工厂（根据字符串提供的参数创建指定的代码沙箱对象）
 */
public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodeSandboxImpl();
            case "remote":
                return new RemoteCodeSandboxImpl();
            case "thirdParty":
                return new ThirdPartyCodeSandboxImpl();
            default :
                return new ExampleCodeSandboxImpl();
        }

    }
}

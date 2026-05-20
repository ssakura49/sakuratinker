package com.ssakura49.sakuratinker.agent;

import com.ssakura49.sakuratinker.agent.HiddenClass.HiddenClassTransformer;
import com.ssakura49.sakuratinker.agent.helper.AgentLogWriter;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

public class SakuraAgent {

    public static void premain(String args, Instrumentation instrumentation) {
        load(args,instrumentation);
        System.out.println("[SakuraAgent] Agent 已就绪");
    }

    public static void agentmain(String args, Instrumentation ins) {
        load(args,ins);
        System.out.println("[SakuraAgent] Agent 已就绪");
    }

    private static void load(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new AgentHealthTransformer(),true);
        instrumentation.addTransformer(new HiddenClassTransformer(), true);
        AgentBridge.setInstrumentation(instrumentation);
        AgentBridge.retransformTargets();
    }

}
